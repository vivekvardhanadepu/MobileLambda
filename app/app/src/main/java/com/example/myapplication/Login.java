package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Login extends AppCompatActivity {
    private TextInputEditText username, password;
    public String Uname = "";
    private Button loginbtn;
    ProgressBar progressBar;
    TextView signup;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);
        loginbtn = findViewById(R.id.buttonLogin);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "You are logged in!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please login!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        signup = findViewById(R.id.signUpText);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Registration.class);
                startActivity(intent);
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String uname, pass;
                uname = String.valueOf(username.getText());
                pass = String.valueOf(password.getText());

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", uname);
                startActivity(intent);
                finish();
                if(!uname.equals("")&&!pass.equals("")){
                    progressBar.setVisibility(view.VISIBLE);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mFirebaseAuth.signInWithEmailAndPassword(uname, pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Login failed, PLease login again!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "login success!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("username", uname);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "All fields are required!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }



            }
        });

    }

}
