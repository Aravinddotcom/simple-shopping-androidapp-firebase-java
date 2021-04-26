package com.tetradev.farmeroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SellerSignUp extends AppCompatActivity {


    EditText  name, phone, emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    TextView tvUserPage;
    FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_sign_up);


        mFirebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.svname);
        phone = findViewById(R.id.svphone);
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText3);
        btnSignUp = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);
        tvUserPage = findViewById(R.id.userpage);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = name.getText().toString();
                final String phoneid = phone.getText().toString();
                final String email = emailId.getText().toString();
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
                    Toast.makeText(SellerSignUp.this,  "Fields are Empty!!", Toast.LENGTH_SHORT).show();

                }
                else if (pwd.length()<8){
                    password.setError("Min password length should be 8 characters!");
                    password.requestFocus();
                    return;

                }


                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SellerSignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                User user =  new User(username,email,phoneid);

                                FirebaseDatabase.getInstance().getReference("Sellers")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                           // Toast.makeText(SellerSignUp.this,"You Have Successfully Registered!",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            }




                            if(!task.isSuccessful()){
                                Toast.makeText(SellerSignUp.this, "Registration Unsuccessful, Account already exists, Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                sendEmailVerification();
                               // startActivity(new Intent(SellerSignUp.this,SellerHome.class));
                            }
                        }
                    });


                }
                else {
                    Toast.makeText(SellerSignUp.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerSignUp.this,SellerSignIn.class);
                startActivity(i);
            }
        });

        tvUserPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerSignUp.this,MainActivity.class);
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
                        Toast.makeText(SellerSignUp.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(SellerSignUp.this, SellerSignIn.class));
                    }else{
                        Toast.makeText(SellerSignUp.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

}