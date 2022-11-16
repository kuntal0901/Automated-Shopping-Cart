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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingcart.models.CartItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
//                ProgressDialog pg;
//                stopService(ConnecttoCartActivity.ins);
//                Log.i("Action","Inside");
//                pg=new ProgressDialog(PaymentActivity.this);
//                pg.setTitle("Logout ");
//                pg.setMessage("Logging Out");
//                pg.show();
//                Log.i("Action","Logged out");
//                pg.dismiss();
//                if(ConnecttoCartActivity.connected){
//                    pg.setTitle("Closing Bluetooth ");
//                    pg.setMessage("Disconnecting from cart");
//                    pg.show();
//
//                    try {
//                        new ConnecttoCartActivity().close_BT();
//                    } catch (IOException e) {
//                        Toast.makeText(PaymentActivity.this,"Closing Bluetooth connection failes",Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//                    pg.dismiss();
//                }
//                Log.i("Action","End");
////                startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
//                Toast.makeText(PaymentActivity.this,"Payment Succesful",Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(ScanActivity.PREFS_TAG, Context.MODE_PRIVATE);

                String jsonSaved = sharedPref.getString(ScanActivity.PRODUCT_TAG, "");
                List<CartItem> cartItemList = new ArrayList<CartItem>();
                if(jsonSaved.length()!=0){
                    Type type = new TypeToken<List<CartItem>>() {
                    }.getType();
                    cartItemList = gson.fromJson(jsonSaved, type);
                }
                Log.d("json4", jsonSaved);

                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
                FirebaseAuth mAuth;
                FirebaseUser mUser;
                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser();
                Map<String, ArrayList> userMap= new HashMap<String, ArrayList>();
//                userMap.put("2", jsonSaved);
                String userId = mUser.getDisplayName();
                database.child(userId).child("prevorder").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            Map<String, String> ordersMap = (Map<String, String>) task.getResult().getValue();

                            if(Objects.equals(ordersMap.get("orderid"), "")){
                                ordersMap.remove("orderid");

                            }

                            Random rd = new Random();

                            String generatedString = String.valueOf(Math.abs(rd.nextInt()));
                            ordersMap.put(generatedString, jsonSaved);
                            database.child(userId).child("prevorder").setValue(ordersMap);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear().apply();
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(i);
                        }
                    }
                });






//                finish();

            }
        });
//    protected void
    }
}