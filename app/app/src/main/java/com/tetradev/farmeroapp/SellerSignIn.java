package com.tetradev.farmeroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class SellerSignIn extends AppCompatActivity {

    private TextView forgotPassword;

    ProgressDialog mDialog;

    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    TextView tvUserPage;
    FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_sign_in);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText3);
        btnSignIn = findViewById(R.id.button);
        tvSignUp = findViewById(R.id.textView);
        tvUserPage = findViewById(R.id.userpage);

        forgotPassword =(TextView)findViewById(R.id.svpasswordforgot);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
//                if (mFirebaseUser != null) {
//                    Toast.makeText(SellerSignIn.this, "you are logged in", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(SellerSignIn.this, SellerHome.class);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(SellerSignIn.this, "please login", Toast.LENGTH_SHORT).show();
//                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();

                //check connection
                if(!isConnected(SellerSignIn.this)){
                        showCustomDialog();
                    }
                else if (email.isEmpty()) {
                    emailId.setError("Please enter your Email ID");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your Password");
                    password.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(SellerSignIn.this, "Fields Are Empty!!", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    closeKeyboard();
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SellerSignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toasty.warning(SellerSignIn.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT,true).show();
                            } else {
                                mDialog = new ProgressDialog(SellerSignIn.this);
                                mDialog.show();
                                mDialog.setContentView(R.layout.progress_dialog);

                                mDialog.getWindow().setBackgroundDrawableResource(
                                        android.R.color.transparent
                                );

                                checkEmailVerification();
                               // Intent intToHome = new Intent(SellerSignIn.this, SellerHome.class);
                              //  startActivity(intToHome);
                            }
                        }
                    });
                } else {
                    Toast.makeText(SellerSignIn.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerSignIn.this,SellerSignUp.class);
                startActivity(i);
            }
        });

        tvUserPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerSignIn.this,LoginActivity.class);
                startActivity(i);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerSignIn.this, PasswordActivity.class));
            }
        });
    }
    private void showCustomDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(SellerSignIn.this);
        builder.setTitle("Connection Failed");
        builder.setMessage("Please Check Your Internet Connection")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),SellerSignUp.class));
                        finish();
                    }
                });
        builder.show();
    }

    private boolean isConnected(SellerSignIn login) {
        ConnectivityManager connectivityManager= (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return  true;
        }
        else {
            return false;
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = mFirebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();


        if(emailflag){
            finish();
            startActivity(new Intent(SellerSignIn.this, SellerHome.class));
        }else{
            Toasty.info(this, "Need to Verify your email", Toast.LENGTH_SHORT,true).show();
            mDialog.dismiss();
            mFirebaseAuth.signOut();
        }
    }

}

