package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextView btnlogin;
    private EditText inputPassword,inputEmail,inputConfirmPassword,inputUsername;
    Button btnRegister;
    String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    ArrayList<String> pres_users=new ArrayList<>(1);
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnlogin=findViewById(R.id.AlreayhaveanAccount);
        inputUsername=findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputCartName);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister=findViewById(R.id.send_email);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseDatabase dt=FirebaseDatabase.getInstance();
        DatabaseReference root=dt.getReference("users");
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot x:snapshot.getChildren()){
                    pres_users.add(x.getValue(dataholder.class).getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuth();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }
    private void PerformAuth() {
        Log.i("Action",pres_users.toString());
        String email =inputEmail.getText().toString();
        String Password = inputPassword.getText().toString();
        String ConfirmPassword = inputConfirmPassword.getText().toString();
        String username=inputUsername.getText().toString();

        if (!email.matches(EmailPattern)){
            showError(inputEmail,"Your email is invalid!");
        }
        else if (Password.isEmpty() || Password.length()<8){
            showError(inputPassword,"Password must be 8 characters!");
        }
        else if(ConfirmPassword.isEmpty() || !ConfirmPassword.equals(Password)){
            showError(inputConfirmPassword,"Password not match!");
        }
        else if(username.isEmpty())
        {
            showError(inputUsername,"Username cannot be empty");
        }
        else if(pres_users.contains(username)){
            showError(inputUsername,"Username already exists");
        }
        else
        {
            progressDialog.setMessage("Please Wait While Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Log.i("Action",user.getDisplayName());
                            }
                        });
                        FirebaseDatabase db=FirebaseDatabase.getInstance();
                        DatabaseReference root=db.getReference("users");
                        Map<String,Map<String,Integer>> ma=new HashMap<String, Map<String, Integer>>();
                        dataholder.Orders x=new dataholder.Orders();
//                        x.setOrderid(0);
                        x.setOrderid(0);
                        dataholder obj=new dataholder();
                        obj.setName(inputUsername.getText().toString());
                        obj.setProfilephoto("");
                        obj.setPrevorder(x);
                        root.child(username).setValue(obj);
                        progressDialog.dismiss();
                        sendUsertoNextActivity();
                        Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void sendUsertoNextActivity() {
        Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }}
