package com.example.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    ImageView profile,home,scan,cart;
    TextView username,email;
    Button prevorder,editprof,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        profile=(ImageView) findViewById(R.id.profilepic);
        home=(ImageView) findViewById(R.id.home);
        scan=(ImageView) findViewById(R.id.inputscan2);
        cart=(ImageView) findViewById(R.id.cart);
        username=(TextView) findViewById(R.id.username);
        email=(TextView) findViewById(R.id.email);
        prevorder=(Button) findViewById(R.id.previous_order);
        editprof=(Button) findViewById(R.id.edit_profile);
        logout=(Button) findViewById(R.id.logout);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        username.setText(user.getDisplayName());
        email.setText(user.getEmail());

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,ScanActivity.class));
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,CartActivity.class));
            }
        });

        prevorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,PreviousOrderActivity.class));
            }
        });

        editprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,LogoutActivity.class));
            }
        });

    }
}