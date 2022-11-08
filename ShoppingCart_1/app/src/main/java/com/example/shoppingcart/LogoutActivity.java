package com.example.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class LogoutActivity extends AppCompatActivity {

    FirebaseAuth mauth=FirebaseAuth.getInstance();
    ProgressDialog pg = new ProgressDialog(this);
    ConnecttoCartActivity ct=new ConnecttoCartActivity();


    void logouts(){
        Log.i("Action","Inside");
//        super.onCreate(savedInstanceState);

        pg.setTitle("Logout ");
        pg.setMessage("Logging Out");
        pg.show();
        try{
            mauth.signOut();
        }
        catch(Exception e)
        {
            Log.i("Action",e.toString());
        }
        Log.i("ACtion","Logged out");
        pg.dismiss();
        if(ConnecttoCartActivity.connected){
            pg.setTitle("Closing Bluetooth ");
            pg.setMessage("Disconnecting from cart");
            pg.show();

            try {
                close_BT();
            } catch (IOException e) {
                Toast.makeText(this,"Closing Bluetooth connection failes",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            pg.dismiss();
        }
        Log.i("Action","End");
        startActivity(new Intent(this,LoginActivity.class));
        Toast.makeText(this,"Logout Succesful",Toast.LENGTH_SHORT).show();


    }
    void close_BT() throws IOException {

        ct.stopWorker=true;
        ConnecttoCartActivity.mmOutputStream.close();
        ConnecttoCartActivity.mmInputStream.close();
        ConnecttoCartActivity.mmSocket.close();

    }
}