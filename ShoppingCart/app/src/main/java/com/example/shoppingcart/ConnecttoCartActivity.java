package com.example.shoppingcart;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ConnecttoCartActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ConnecttoCartActivity.this,HomeActivity.class));
    }

    private EditText cartname;
    TextView status;
    Button connect;
    public boolean connected = true;
    ProgressDialog progressDialog;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    public ArrayList<Float> arrli=new ArrayList<>(1);
    float prev=0.0f;
//    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        arrli.add(0.0f);
        if(connected)
        {
            status.setText("You are already connected to app so taking u to add Scan Page");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(ConnecttoCartActivity.this,ScanActivity.class));
        }
//        Log.i("Action", "Works");
        progressDialog= new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectto_cart);
        cartname = (EditText) findViewById(R.id.inputCartName);
        status = findViewById(R.id.statusText);
        connect = (Button) findViewById(R.id.connect_cart);
        connect.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                boolean findable=establishConnection();
                if(findable && !connected)
                {
                    progressDialog.setMessage("Connecting to Bluetooth device ");
                    progressDialog.setTitle("Connection");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    try {
                        openBT();
                        progressDialog.dismiss();
                        startActivity(new Intent(ConnecttoCartActivity.this,ScanActivity.class));
                    } catch (IOException e) {
                        status.setText("Failed");
                        progressDialog.dismiss();
                    }
                }
                else
                {
                    if(connected)
                    {
                        status.setText("You are already connected to app so taking u to add Scan Page");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(ConnecttoCartActivity.this,ScanActivity.class));
                    }
                    else{
                        status.setText("Device with given name not found");

                    }

                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean establishConnection() {
        String cart = cartname.getText().toString();
        boolean findable = findBT(cart);
        return findable;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean findBT(String cart) {
        boolean found = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            status.setText("No bluetooth adapter available");
        }

        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }
        startActivityForResult(enableBluetooth, 0);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(cart)) {
                    mmDevice = device;
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        progressDialog.dismiss();
        status.setText("Bluetooth Opened with "+mmDevice.getName());
        connected=true;

    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            float temp=Float.parseFloat(data);
                                            if(counter>=1)
                                            {
                                                prev=temp;
                                            }
                                            if(counter==0)
                                            {
                                                counter+=1;
                                            }
                                            if(temp-arrli.get(arrli.size()-1)>=150.0 && Math.abs(temp-prev)<=50.0)
                                            {
                                                arrli.add(temp);
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    public float return_last_val(){
        return arrli.get(arrli.size()-1);
    }

}