package com.example.shoppingcart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingcart.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    Button yesButton;
    Button noButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//    @Override
        setContentView(R.layout.activity_payment);
        ProgressDialog pg = new ProgressDialog(this);
        yesButton = (Button) findViewById(R.id.yes_Button);
        noButton = (Button) findViewById(R.id.no_Button);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pg;
                stopService(ConnecttoCartActivity.ins);
                Log.i("Action","Inside");
                pg=new ProgressDialog(PaymentActivity.this);
                pg.setTitle("Logout ");
                pg.setMessage("Logging Out");
                pg.show();
                Log.i("Action","Logged out");
                pg.dismiss();
                if(ConnecttoCartActivity.connected){
                    pg.setTitle("Closing Bluetooth ");
                    pg.setMessage("Disconnecting from cart");
                    pg.show();

                    try {
                        new ConnecttoCartActivity().close_BT();
                    } catch (IOException e) {
                        Toast.makeText(PaymentActivity.this,"Closing Bluetooth connection failes",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    pg.dismiss();
                }
                Log.i("Action","End");
//                startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                Toast.makeText(PaymentActivity.this,"Payment Succesful",Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(ScanActivity.PREFS_TAG, Context.MODE_PRIVATE);

                String jsonSaved = sharedPref.getString(ScanActivity.PRODUCT_TAG, "");
                List<CartItem> cartItemList = new ArrayList<CartItem>();


                if(jsonSaved.length()!=0){
                    Type type = new TypeToken<List<CartItem>>() {
                    }.getType();
                    cartItemList = gson.fromJson(jsonSaved, type);
                }
                Log.d("cart", cartItemList.toString());

                FirebaseAuth mAuth;
                FirebaseUser mUser;
                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear().apply();
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
//                finish();

            }
        });
//    protected void
    }
}