package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;

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
    ProgressDialog pg;
    FirebaseAuth mauth=FirebaseAuth.getInstance();
//    ProgressDialog pg = new ProgressDialog(this);
    ConnecttoCartActivity ct=new ConnecttoCartActivity();
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
        Uri profileuri;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String usernames=user.getDisplayName();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference root=db.getReference("users");

        username.setText(user.getDisplayName());
        email.setText(user.getEmail());
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot x: snapshot.getChildren()){
                    dataholder temp=x.getValue(dataholder.class);
//                    Log.i("Action",String.valueOf(temp.getProfilephoto().isEmpty()));
                    Log.i("Action","Profile Page Username is"+user.getDisplayName()+"temp username is"+temp.getName()+"comp is"+String.valueOf(user.getDisplayName().equals(temp.getName())));
                    if(temp.getName().equals(user.getDisplayName()))
                    {
                        try{
                        if(temp.getProfilephoto().isEmpty())
                        {
//                            Log.i("Action","in");
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.blankprofile)
                                    .override(500, 500)
                                    .fitCenter() // scale to fit entire image within ImageView
                                    .into(profile);
                        }
                        else{
                            Glide.with(ProfileActivity.this)
                                    .load(Uri.parse(temp.getProfilephoto()))
                                    .override(500, 500)
                                    .fitCenter() // scale to fit entire image within ImageView
                                    .into(profile);
                        }
                    }
                        catch (Exception e)
                        {
                            Log.i("ActionError","Exception is"+e.toString());
                        }

                }
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Log.i("Action",root.child(usernames).get());

//        Glide.with(this)
//                .load(R.drawable.blankprofile)
//                .override(500, 500)
//                .fitCenter() // scale to fit entire image within ImageView
//                .into(profile);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,ScanActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,CartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        prevorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,PreviousOrderActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        editprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Action ","Logout clicked");
//                LogoutActivity lg=new LogoutActivity();
                try{

                    logouts();
                }
                catch (Exception e)
                {
                    Log.i("Action",e.toString());
                }
//                startActivity(new Intent(ProfileActivity.this,LogoutActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });



    }
    void logouts(){
        Log.i("Action","Inside");
        pg=new ProgressDialog(this);
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
        Log.i("Action","Logged out");
        pg.dismiss();
        if(ConnecttoCartActivity.connected){
            pg.setTitle("Closing Bluetooth ");
            pg.setMessage("Disconnecting from cart");
            pg.show();

            try {
                new ConnecttoCartActivity().close_BT();
            } catch (IOException e) {
                Toast.makeText(this,"Closing Bluetooth connection failes",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            pg.dismiss();
        }
        Log.i("Action","End");
        startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        Toast.makeText(this,"Logout Successful",Toast.LENGTH_SHORT).show();

    }

}