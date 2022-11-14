package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class Forgotpassword extends AppCompatActivity {
    EditText email;
    Button send;
    String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth auth= FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        email=(EditText) findViewById(R.id.email_id);
        send=(Button) findViewById(R.id.send_email);
        ImageView im=(ImageView)findViewById(R.id.profilepic);
        Glide.with(Forgotpassword.this)
                .load(R.drawable.person_forgot)
                .override(500, 500)
                .fitCenter() // scale to fit entire image within ImageView
                .into(im);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("Action","here");
                String email_send=email.getText().toString();
                Log.i("Action","Mail id is"+email_send);

                if (!email_send.matches(EmailPattern)){
                    Toast.makeText(Forgotpassword.this,"Email id entered does not match the desired patter",Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        auth.sendPasswordResetEmail(email_send).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Forgotpassword.this,"Reset Link sent to entered email id ",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Forgotpassword.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Forgotpassword.this,"User doesn't exist with the given mail id ",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e){


                    }


                }
            }
        });
    }
}