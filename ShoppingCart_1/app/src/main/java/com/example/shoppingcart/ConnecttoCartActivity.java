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

        startActivity(new Intent(ConnecttoCartActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    private EditText cartname;
    TextView status;
    Button connect;
    static boolean connected=false;
    static ProgressDialog progressDialog;
    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mmSocket;
    static BluetoothDevice mmDevice;
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;
    static Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    static public ArrayList<Float> arrli=new ArrayList<>(1);
    static float prev=0.0f;
//    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        arrli.add(0.0f);
        progressDialog= new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectto_cart);
        if(connected)
        {
            Toast.makeText(ConnecttoCartActivity.this,"Moving You to Scan Activity Because You are connected to cart",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ConnecttoCartActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
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
//                        progressDialog.dismiss();
                        startActivity(new Intent(ConnecttoCartActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        Toast.makeText(ConnecttoCartActivity.this,"Connection Established",Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        status.setText("Failed");
//                        progressDialog.dismiss();
                    }
                }
                else
                {
                    if(connected)
                    {
                        status.setText("You are already connected to app so still in the Home Page");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(ConnecttoCartActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
//        Log.i("Action","Connect to cart : Findable is "+findable);
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
//        Log.i("Action","Connect to cart : Inside Open Bluetooth");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
//        Log.i("Action","Connect to cart : Connected set to true");
        connected=true;
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        Log.i("Action", String.valueOf(mmInputStream.available()));
        progressDialog.dismiss();
        status.setText("Bluetooth Opened with "+mmDevice.getName());
//        Log.i("Action","Connect to cart : End of bluetooth");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
//        Log.i("Action","Connect to cart : insidebeginListenForData");
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {

            public void run()
            {
//                Log.i("Action","Connect to cart : New THREAD initiated and run insidebeginListenForData");
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
//                    Log.i("Action","Connect to cart : Inside While loop insidebeginListenForData");
                    try
                    {
//                        Log.i("Action","Connect to cart : Inside try and run insidebeginListenForData");
//                        int bytesAvailable=0;
//                        try{
                           int bytesAvailable= mmInputStream.available();
                           Log.i("Action Done","Connect to Cart: "+mmInputStream.toString());
//                           break;

//                        catch(Exception e)
//                        {
////                            Log.i("Action Error","Error here");
////                            Log.i("Action Exception",e.toString());
//                        }
//                        Log.i("Action","Connect to cart : After Available");
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
//                            Log.i("Action","Connect to cart : Before Read Data");
                            mmInputStream.read(packetBytes);

//                            Log.i("Action","Connect to cart : After Read Data");
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
//                                    Log.i("Action","Connect to cart : Delimiter Found");
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
//                                    Log.i("Action","Connect to cart : Delimiter Found");
                                    handler.postDelayed(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Log.i("Action Weight","Connect to cart :"+data);
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
                                    },5000);
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
//                        else{
//                            Log.i("Action","Connect To cart: No data to work with");
//                        }
                    }
                    catch (IOException ex)
                    {
//                        Log.i("Action Error","Connect to cart :"+ex.toString());
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    public void close_BT() throws IOException {

        stopWorker=true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();

    }
    public float return_last_val(){

        return arrli.get(arrli.size()-1);

    }

}