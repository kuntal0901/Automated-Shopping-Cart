package com.example.shoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {
    ImageView profile,home,scan,cart;;
    EditText username,email,password,retype_password;
    CheckBox reset_enable;
    Button cancel,save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        profile=(ImageView) findViewById(R.id.profilepic);
        username= (EditText) findViewById(R.id.username);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        retype_password=(EditText) findViewById(R.id.retype_password); ;
        reset_enable=(CheckBox) findViewById(R.id.reset_enable);
        cancel=(Button) findViewById(R.id.cancel);
        save=(Button) findViewById(R.id.save_changes);
        username.setText(user.getDisplayName());
        home=(ImageView) findViewById(R.id.home);
        scan=(ImageView) findViewById(R.id.inputscan2);
        cart=(ImageView) findViewById(R.id.cart);

        email.setText(user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .override(100, 200)
                    .fitCenter() // scale to fit entire image within ImageView
                    .into(profile);
        }

        else{
            Glide.with(this)
                    .load(R.drawable.blankprofile)
                    .override(100, 200)
                    .fitCenter() // scale to fit entire image within ImageView
                    .into(profile);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this,ScanActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this,CartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });




    }
}