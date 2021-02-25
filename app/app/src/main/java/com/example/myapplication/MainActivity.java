package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    TextView welcome_note, message;
    Button start_btn, stop_btn, logout_btn;
    public String username;
    CheckBox rememberMe;
    ServerSocket server = null;
    Socket socket = null;
    Thread Thread1 = null;
    DataInputStream in = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        start_btn = findViewById(R.id.startbtn);
        stop_btn = findViewById(R.id.stopbtn);
        logout_btn = findViewById(R.id.buttonLogout);

        welcome_note = findViewById(R.id.Message);

        welcome_note.setText("Welcome "+username);
        message = findViewById(R.id.response);


        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    String response = "";
                    @Override
                    public void run() {
                        if(server!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Server is already running!!",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }


                        if(server!=null){
                            if(server.isClosed()==false){
                                try{
                                    server.close();
                                    server = null;
                                }
                                catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }

                        if(socket!=null){
                            try{
                                socket.close();
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }

                        }

                        try{
                            server = new ServerSocket(1234);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Server started !!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            try{
                                socket = server.accept();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), socket.getInetAddress().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);

                                byte[] buffer = new byte[1024];
                                int bytesRead;

                                InputStream inputStream = socket.getInputStream();

                                /*
                                 * notice:
                                 * inputStream.read() will block if no data return
                                 */
                                while ((bytesRead = inputStream.read(buffer)) != -1){
                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                    response += byteArrayOutputStream.toString("UTF-8");
                                }
                                socket.close();
                                message.setText(response);
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(server!=null){
                    try{
                        server.close();
                        server=null;
                        Toast.makeText(getApplicationContext(),"Server Stopped!!", Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Server is already closed!!", Toast.LENGTH_SHORT).show();
                }
                if(socket!=null){
                    try {
                        socket.close();
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                message.setText("");
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

    }




}


