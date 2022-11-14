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

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingcart.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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