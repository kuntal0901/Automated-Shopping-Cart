package com.example.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;

//@GlideModule
//public final class MyAppGlideModule extends AppGlideModule {
//    @Override
//    public void applyOptions(Context context, GlideBuilder builder) {
//        // Glide default Bitmap Format is set to RGB_565 since it
//        // consumed just 50% memory footprint compared to ARGB_8888.
//        // Increase memory usage for quality with:
//
//        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
//    }
//}
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
//        Log.i("Action",user.getPhotoUrl().toString());
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .override(100, 200)
                    .fitCenter() // scale to fit entire image within ImageView
                    .into(profile);
        }
        else{
            Log.i("Action","In this"+String.valueOf(R.drawable.blankprofile));
                Glide.with(this)
                        .load(R.drawable.blankprofile)
                        .override(100, 200)
                        .fitCenter() // scale to fit entire image within ImageView
                        .into(profile);
        }
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