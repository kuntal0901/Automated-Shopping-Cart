package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to the app which would lead to signing out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private ImageView home,scan,cart,profile;
    private View connecttocart,previousorder,help;
    View status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activtiy);
        home = findViewById(R.id.home);
        scan = findViewById(R.id.inputscan);
        cart = findViewById(R.id.cart);
        connecttocart = findViewById(R.id.connectocart);
        previousorder = findViewById(R.id.previousord);
        help = findViewById(R.id.connectbutt3);
        profile=findViewById(R.id.profilepic);
        status=findViewById(R.id.status);
        if(ConnecttoCartActivity.connected){
            status.setBackground(getResources().getDrawable(R.drawable.connected));
        }
        else{
            status.setBackground(getResources().getDrawable(R.drawable.notconnected));
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference root=db.getReference("users");
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.reload();
                for (DataSnapshot x : snapshot.getChildren()) {
                    dataholder temp = x.getValue(dataholder.class);
//                    Log.i("Action", "Home Activity Username is" + user.getDisplayName() + "temp username is" + temp.getName() + "comp is" + String.valueOf(user.getDisplayName().equals(temp.getName())));
                    if (temp.getName().equals(user.getDisplayName())) {
                        try{
                            if (temp.getProfilephoto().isEmpty()) {
                                Glide.with(HomeActivity.this)
                                        .load(R.drawable.blankprofile)
                                        .override(200, 200)
                                        .fitCenter() // scale to fit entire image within ImageView
                                        .into(profile);
                            } else {
                                Glide.with(HomeActivity.this)
                                        .load(Uri.parse(temp.getProfilephoto()))
                                        .override(200, 200)
                                        .fitCenter() // scale to fit entire image within ImageView
                                        .into(profile);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.i("ActionError","Exception is"+e.toString());
                        }

                    }
            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ScanActivity.class);
                startActivity(intent);
            }
        });
        cart.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, itemlist.class);
            startActivity(intent);
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