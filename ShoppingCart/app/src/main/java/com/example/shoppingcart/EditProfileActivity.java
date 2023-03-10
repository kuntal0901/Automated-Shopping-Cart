package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {
    ImageView profile,home,scan,cart;
    EditText username,email,password,retype_password;
    CheckBox reset_enable;
    Button cancel,save;
    String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog pg;
    Uri filepath;
    Bitmap bitmap;
    FirebaseUser user;
    int image_clicked;
    FirebaseDatabase db=FirebaseDatabase.getInstance();
    DatabaseReference root=db.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        image_clicked=0;
        user = FirebaseAuth.getInstance().getCurrentUser();
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

        password.setEnabled(false);
        retype_password.setEnabled(false);
        email.setText(user.getEmail());

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(EditProfileActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent=new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent,100);
//                                startActivity(Intent.createChooser(intent,));
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });


        /*
         * Reset enable is the checkbox which on being selected will provide the user the option to type in the password into the edit text for password
         */
        reset_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reset_enable.isChecked())
                {
                    password.setEnabled(true);
                    retype_password.setEnabled(true);
                }
                else
                {
                    password.setEnabled(false);
                    retype_password.setEnabled(false);
                }
            }
        });

        /*
         * Set the profile image of user as set in his database
         * */

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot x: snapshot.getChildren()){
                    dataholder temp=x.getValue(dataholder.class);
//                    Log.i("Action",temp.toString());
                    if(temp.getName().equals(user.getDisplayName())){
                        try{
                            if(temp.getProfilephoto().isEmpty()){
                                Glide.with(EditProfileActivity.this)
                                        .load(R.drawable.blankprofile)
                                        .override(500, 500)
                                        .fitCenter() // scale to fit entire image within ImageView
                                        .into(profile);

                            }
                            else{
                                Glide.with(EditProfileActivity.this)
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                startActivity(new Intent(EditProfileActivity.this,itemlist.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email_updated=email.getText().toString();
                String name_updates=username.getText().toString();

                Log.i("Action","Name in Input Field is"+name_updates);

                if (!email_updated.matches(EmailPattern)){
                    Toast.makeText(EditProfileActivity.this,"Email doesnot match the desire pattern",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!email_updated.equals(user.getEmail())){
                        user.updateEmail(email_updated);
                        Toast.makeText(EditProfileActivity.this,"Email changed username and password might not have been updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProfileActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    if(!name_updates.equals(user.getDisplayName()))
                    {
                        root.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                root.
//                                snapshot.child()
                                for(DataSnapshot x:snapshot.getChildren()){
                                    dataholder temp=x.getValue(dataholder.class);
                                    if(temp.getName().equals(user.getDisplayName())){

                                        if(temp.getPrevorder() == null)
                                        {
                                            dataholder.Orders x1=new dataholder.Orders();
                                            x1.setOrderid("");
                                            temp.setPrevorder(x1);
                                        }
                                        Log.i("Action","Before Class objects is "+temp.getPrevorder().getOrderid()+" Name is"+temp.getName());
                                        temp.setName(name_updates);
                                        root.child(user.getDisplayName()).orderByChild("name").equalTo(user.getDisplayName()).getRef().removeValue();
                                        root.child(name_updates).setValue(temp);
                                        Log.i("Action","Username in db updated");
                                        Log.i("Action","Class objects is "+temp.getPrevorder().getOrderid());
                                        UserProfileChangeRequest updates=new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name_updates)
                                                .build();
                                        try{
                                            Log.i("Action","Editing the username in Auth");
                                            user.updateProfile(updates);
                                                int sbnk=0;
                                                while(sbnk<1000000)
                                                {
                                                    sbnk+=1;
                                                }

                                            user.reload();
                                            Log.i("Action","After updating in Auth username is "+user.getDisplayName());
                                        }
                                        catch(Exception e)
                                        {
                                            Log.i("Action",e.toString());
                                        }
                                        break;
                                    }
                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    if(reset_enable.isChecked())
                    {
                        String pass1=password.getText().toString();
                        String pass2=retype_password.getText().toString();
                        if(pass1.isEmpty()||pass2.isEmpty()||pass1.length()<8 || pass2.length()<8 || !pass1.equals(pass2)){
                            Toast.makeText(EditProfileActivity.this,"Password Not updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            user.updatePassword(pass1);
                            Toast.makeText(EditProfileActivity.this,"Password Updated",Toast.LENGTH_SHORT).show();
                        }
                    }



                    startActivity(new Intent(EditProfileActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                }
//
//


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==100){
            filepath=data.getData();
            try{
                image_clicked+=1;
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                profile.setImageBitmap(Bitmap.createScaledBitmap(bitmap,500,500,false));
                uploadtofirebase();
            }
            catch (Exception ex){
                Log.i("Action","Exception raised"+ex.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadtofirebase(){
        ProgressDialog pg = new ProgressDialog(EditProfileActivity.this);
        pg.setTitle("File Uploader");
        pg.show();

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference uploader= storage.getReference("Image1"+new Random().nextInt(50));

        uploader.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pg.dismiss();
                        root.child(user.getDisplayName()).child("profilephoto").setValue(uri.toString());
                        Log.i("Action",root.toString());
                        Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent=(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                pg.setMessage("Uploaded:"+(int)percent+"%");
            }
        });


    }

}