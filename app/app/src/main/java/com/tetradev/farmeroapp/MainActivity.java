package com.tetradev.farmeroapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {


    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";


    EditText name, phone, emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    TextView tvseller;
    FirebaseAuth mFirebaseAuth;


    String email,phoneid,username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.tvname);
        phone = findViewById(R.id.tvphone);
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText3);
        btnSignUp = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);
        tvseller = findViewById(R.id.seller);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = name.getText().toString();
                phoneid = phone.getText().toString();
                email = emailId.getText().toString();
                String pwd = password.getText().toString();

                if(username.isEmpty()){
                    name.setError("Please enter your Name");
                    name.requestFocus();
                }
                else if(phoneid.isEmpty()) {
                    phone.setError("Please enter your Phone number");
                    phone.requestFocus();
                }

                else if (phoneid.length()<10){
                    phone.setError("Enter 10 digit numbers!");
                    phone.requestFocus();
                    return;

                }



                else if(email.isEmpty()){
                    emailId.setError("Please enter your Email ID");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("please enter your Password");
                    password.requestFocus();
                    return;
                }
                else if(email.isEmpty() || pwd.isEmpty() || username.isEmpty() || phoneid.isEmpty()){
                    Toast.makeText(MainActivity.this,  "Fields are Empty!!", Toast.LENGTH_SHORT).show();

                }


                else if (pwd.length()<8){
                    password.setError("Min password length should be 8 characters!");
                    password.requestFocus();
                    return;

                }




                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){




                            }

                            if(!task.isSuccessful()){
                                Toasty.warning(MainActivity.this, "Registration Unsuccessful, Account already exists, Please Try Again", Toast.LENGTH_SHORT,true).show();

                            }
                            else {
                                    sendEmailVerification();
                                //startActivity(new Intent(MainActivity.this,ImageListActivity.class));
                            }
                        }
                    });


                }
                else {
                    Toast.makeText(MainActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();

                }
            }

        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        tvseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SellerSignIn.class);
                startActivity(i);
            }
        });

    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser =  mFirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){

            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        //storingdata to database

                        User user =  new User(username,email,phoneid);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    // Toast.makeText(MainActivity.this,"You Have Successfully Registered!",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        //end


                        Toasty.success(MainActivity.this, "Successfully Registered, Verification mail sent!",Toast.LENGTH_SHORT,true).show();
                        mFirebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }else{
                        Toast.makeText(MainActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

}
