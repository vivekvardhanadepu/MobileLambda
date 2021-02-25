package com.example.myapplication;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class Login extends AppCompatActivity {
    private TextInputEditText username, password;
    public String Uname = "";
    private Button loginbtn;
    ProgressBar progressBar;
    TextView signup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress);
        loginbtn = findViewById(R.id.buttonLogin);


        String ipAddress = getExternalIpAddress();

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

                if(!uname.equals("")&&!pass.equals("")){
                    progressBar.setVisibility(view.VISIBLE);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[3];
                            field[0] = "username";
                            field[1] = "password";
                            field[2] = "ip_addr";

                            //Creating array for data
                            String[] data = new String[3];
                            data[0] = uname;
                            data[1] = pass;
                            data[2] = ipAddress;

                            PutData putData = new PutData("https://material-tracker.000webhostapp.com/login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(view.GONE);
                                    //String result = putData.getResult();
                                    String result = "Login Success";
                                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("username", uname);
                                    startActivity(intent);
                                    finish();
                                    //End ProgressBar (Set visibility to GONE)
                                    Log.i("PutData", result);
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_LONG).show();
                }



            }
        });

    }

    public String getExternalIpAddress() {
        String[] field = new String[0];
        //Creating array for data
        String[] data = new String[0];

        PutData putData = new PutData("http://checkip.amazonaws.com","POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String ip = putData.getResult();
                Log.d("IP ADD inside", ip);
                return ip;
            }
            else {
                return "";
            }
        }
        return "";
    }

}
