package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Registration extends AppCompatActivity {
    TextInputEditText fullname, username, email, password;

    Button signup_btn;
    ProgressBar progressBar;

    TextView login_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);

        signup_btn =findViewById(R.id.buttonSignup);
        login_view = findViewById(R.id.loginText);

        login_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname, eml, passw, fname;

                uname = String.valueOf(username.getText());
                eml = String.valueOf(email.getText());
                passw = String.valueOf(password.getText());
                fname = String.valueOf(fullname.getText());

                if(!uname.equals("")&&!passw.equals("")&&!eml.equals("")&&!fname.equals("")){
                    progressBar.setVisibility(view.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[4];
                            field[0] = "username";
                            field[1] = "password";
                            field[2] = "fullname";
                            field[3] = "email";

                            //Creating array for data
                            String[] data = new String[4];
                            data[0] = uname;
                            data[1] = passw;
                            data[2] = fname;
                            data[3] = eml;
                            PutData putData = new PutData("https://material-tracker.000webhostapp.com/signup.php", "POST", field, data);
                            Log.d("url : ",putData.toString());
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(view.GONE);
                                    String result = putData.getResult();
                                    if(result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                    Log.i("PutData", result);
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
