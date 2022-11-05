package com.example.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private ImageView home,scan,cart,profile;
    private View connecttocart,previousorder,help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activtiy);
        home = findViewById(R.id.home);
        scan = findViewById(R.id.inputscan);
        cart = findViewById(R.id.cart);
        connecttocart = findViewById(R.id.connectocart);
        previousorder = findViewById(R.id.previousord);
        help = findViewById(R.id.connectbutt3);
        profile=findViewById(R.id.profilepic);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.i("Action",user.getPhotoUrl().toString());
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .override(200, 200)
                    .fitCenter() // scale to fit entire image within ImageView
                    .into(profile);
        }
        else{
            Glide.with(this)
                    .load(R.drawable.blankprofile)
                    .override(200, 200)
                    .fitCenter() // scale to fit entire image within ImageView
                    .into(profile);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ScanActivity.class);
                startActivity(intent);
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
        connecttocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ConnecttoCartActivity.class);
                startActivity(intent);
            }
        });
        previousorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,HelpActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });
    }
}