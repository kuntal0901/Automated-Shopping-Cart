package com.example.shoppingcart;

import static android.content.ContentValues.TAG;

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
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
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
    static Intent ins;
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
        progressDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        if (connected) {
            Toast.makeText(ConnecttoCartActivity.this, "Already Connected to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ConnecttoCartActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {


            setContentView(R.layout.activity_connectto_cart);

            cartname = (EditText) findViewById(R.id.inputCartName);
            status = findViewById(R.id.statusText);
            connect = (Button) findViewById(R.id.connect_cart);
            connect.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    boolean findable = establishConnection();
                    if (findable && !connected) {
                        progressDialog.setMessage("Connecting to Bluetooth device ");
                        progressDialog.setTitle("Connection");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        try {
                            openBT();
//                        progressDialog.dismiss();
                            startActivity(new Intent(ConnecttoCartActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            Toast.makeText(ConnecttoCartActivity.this, "Connection Established", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            status.setText("Failed");
//                        progressDialog.dismiss();
                        }
                    } else {
                        if (connected) {
                            status.setText("You are already connected to app so still in the Home Page");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(ConnecttoCartActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            status.setText("Device with given name not found");

                        }

                    }
                }
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean establishConnection() {
        String cart = cartname.getText().toString();
        boolean findable = findBT(cart);
//        Log.i("Action","Connect to cart : Findable is "+findable);
        return findable;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        connected = true;
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        progressDialog.dismiss();
        status.setText("Bluetooth Opened with " + mmDevice.getName());
        Log.i("Action", "Here");
        if(WeightService.item_added.size()==0)
        {
            WeightService.item_added.put(java.time.LocalTime.now(),0.0f);
            Log.i("ActionOne",WeightService.item_added.toString());
        }
        if(WeightService.item_removed.size()==0)
        {
            WeightService.item_removed.put(java.time.LocalTime.now(),0.0f);
            Log.i("ActionOne",WeightService.item_removed.toString());
        }
        if(WeightService.item_pred.size()==0)
        {
            WeightService.item_pred.put(java.time.LocalTime.now()," ");
            Log.i("ActionOne","Empty string added");
        }

        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e) {

                    }
                    try {
                        Handler h1 = new Handler(Looper.getMainLooper());
                        h1.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run()
                            {
                                ArrayList<Float> datas = beginListenForData();
                                if(datas.size()>0)
                                {
                                    WeightService.li.addAll(datas);
                                    if(WeightService.shopping_cart_weights.size()==0)
                                    {
                                        WeightService.shopping_cart_weights.add(WeightService.li.get(0));
                                        Log.i("ActionOne",WeightService.shopping_cart_weights.toString());
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e) {

                    }
                }
            }
        });
        th1.start();
        ins=new Intent(ConnecttoCartActivity.this,WeightService.class);
        startService(ins);

    }


    ArrayList<Float> beginListenForData() {
        ArrayList<Float> li=new ArrayList<>(1);
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        int bytesAvailable = 0;
        try {
            bytesAvailable = mmInputStream.available();
            if (bytesAvailable > 0) {
                byte[] packetBytes = new byte[bytesAvailable];
                mmInputStream.read(packetBytes);
                ArrayList<Character> temp = new ArrayList(1);
                for (int i = 0; i < bytesAvailable; i++) {
                    byte b = packetBytes[i];
                    if (b == 10)
                    {
//                        Log.i("Action", "Data is" + temp.toString());
                        String conv = "";
                        for (int temp_num = 0; temp_num < temp.size(); temp_num++)
                        {
                            conv = conv + temp.get(temp_num);
                        }
                        float converted = Float.parseFloat(conv);
//                        Log.i("Action", "Converted to float is " + converted);
                        li.add(converted);
                        temp = new ArrayList<>(1);
                    } else if (b == 32) {

                    } else {
                        temp.add((char) b);
                    }
                }
            }
            return li;
        } catch (IOException ex) {
            ex.printStackTrace();
            li.add(1.0f);
            return li;
        }
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