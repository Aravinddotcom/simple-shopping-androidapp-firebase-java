package com.tetradev.farmeroapp;

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

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView forgotPassword;

    ProgressDialog mDialog;
    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    TextView tvseller;
    FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText3);
        btnSignIn = findViewById(R.id.button);
        tvSignUp = findViewById(R.id.textView);
        tvseller = findViewById(R.id.seller);

        forgotPassword =(TextView)findViewById(R.id.tvforgotpassword);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();


//                if (mFirebaseUser != null) {
//                    Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(LoginActivity.this, "Please login", Toast.LENGTH_SHORT).show();
//                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Paper.book().write(Common.USER_KEY, emailId.getText().toString());


                String email = emailId.getText().toString();
                String pwd = password.getText().toString();

    //check connection
                if(!isConnected(LoginActivity.this)){
                        showCustomDialog();
                    }
           else if (email.isEmpty()) {
                    emailId.setError("Please enter your Email ID");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your Password");
                    password.requestFocus();
                }


                else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields Are Empty!!", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && pwd.isEmpty())) {
                    closeKeyboard();

                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                Toasty.warning(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT,true).show();
                            } else {
                                mDialog = new ProgressDialog(LoginActivity.this);
                                mDialog.show();
                                mDialog.setContentView(R.layout.progress_dialog);

                                mDialog.getWindow().setBackgroundDrawableResource(
                                        android.R.color.transparent
                                );

                                checkEmailVerification();

                                //Intent intToHome = new Intent(LoginActivity.this, ImageListActivity.class);
                                //startActivity(intToHome);
                            }

                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                }

            }
        });


        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intSignUp);
            }
        });

        tvseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SellerSignIn.class);
                startActivity(i);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PasswordActivity.class));
            }
        });


    }

    private void showCustomDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
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
                   startActivity(new Intent(getApplicationContext(),MainActivity.class));
                   finish();
                    }
                });
        builder.show();
    }

    private boolean isConnected(LoginActivity login) {
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
            startActivity(new Intent(LoginActivity.this, ImageListActivity.class));
        }else{
            Toasty.info(this, "Need to Verify your email", Toast.LENGTH_SHORT,true).show();
            mDialog.dismiss();
            mFirebaseAuth.signOut();


       }
    }

}






