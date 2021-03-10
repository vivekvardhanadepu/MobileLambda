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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.Call;
import okhttp3.Callback;

import fi.iki.elonen.NanoHTTPD;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import bsh.Interpreter;

public class MainActivity extends AppCompatActivity {

    TextView welcome_note, message;
    Button start_btn, stop_btn, logout_btn;
    public String username;
    CheckBox rememberMe;
    //    ServerSocket server = null;
//    Socket socket = null;
    boolean isRunning = false;
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

        welcome_note.setText("Welcome " + username);
        message = findViewById(R.id.response);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        MyHTTPD server = null;
        try {
            server = new MyHTTPD();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MyHTTPD finalServer = server;

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    //                    String response = "";
                    @Override
                    public void run() {
//                        message.setText(response);
                        try {
                            FileOutputStream fileout=openFileOutput("demo.py", MODE_PRIVATE);
                            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
//                            outputWriter.write("print(\"damnu\")\ndef main():\n\tn=365\n\tprint(n)\n\treturn n\ndef main2():\n\treturn35");
                            outputWriter.write("print(\"damn\")");
                            outputWriter.close();
                            Interpreter interpreter = new Interpreter();
                            String temp = "eval";
                            try {
                                interpreter.set("context", this);//set any variable, you can refer to it directly from string
                                interpreter.eval("result = (7+21*6)/(32-27)");//execute code
                                temp = interpreter.get("result").toString();
                            }
                            catch (Exception e){//handle exce   ption
                                e.printStackTrace();
                            }
                            final String temp2 = temp;
                            //display file saved message
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), temp2 , Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (isRunning) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Server is already running!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


//                        if(server!=null){
//                            if(server.isClosed()==false){
//                                try{
//                                    server.close();
//                                    server = null;
//                                }
//                                catch (IOException e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        if(socket!=null){
//                            try{
//                                socket.close();
//                            }
//                            catch (IOException e){
//                                e.printStackTrace();
//                            }
//
//                        }

//                        try {
//                            OkHttpClient client = new OkHttpClient();
//
//                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.0.105:5000/conn_request").newBuilder();
//                            urlBuilder.addQueryParameter("type", "0");
////                            urlBuilder.addQueryParameter("user", "vogella");
//                            String url = urlBuilder.build().toString();
//
//                            Request request = new Request.Builder()
//                                    .url(url)
//                                    .build();
//
//                            client.newCall(request).enqueue(new Callback() {
//                                @Override
//                                public void onFailure(Call call, IOException e) {
//                                    e.printStackTrace();
//                                }
//
//                                @Override
//                                public void onResponse(Call call, final Response response) throws IOException {
//                                    if (!response.isSuccessful()) {
//                                        throw new IOException("Unexpected code " + response);
//                                    } else {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(getApplicationContext(), "Server started !!", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            });

//                            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//                            InetAddress ia = InetAddress.getLocalHost();
//                            String ip = ia.getHostAddress();

                            try {

                                finalServer.start();
                                isRunning = true;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "lyaaggg", Toast.LENGTH_SHORT).show();
                                    }
                                });

//                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
//
//                                byte[] buffer = new byte[1024];
//                                int bytesRead;
//
//                                InputStream inputStream = socket.getInputStream();
//
//                                /*
//                                 * notice:
//                                 * inputStream.read() will block if no data return
//                                 */
//                                while ((bytesRead = inputStream.read(buffer)) != -1){
//                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
//                                    response += byteArrayOutputStream.toString("UTF-8");
//                                }
//                                socket.close();
//                                message.setText(finalServer.response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                });
                thread.start();

            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
//                    OkHttpClient client = new OkHttpClient();
//
//                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://e0481ef8000f.ngrok.io/conn_request").newBuilder();
//                    urlBuilder.addQueryParameter("type", "1");
////                    urlBuilder.addQueryParameter("user", "vogella");
//                    String url = urlBuilder.build().toString();
//
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .build();
//
//                    client.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onResponse(Call call, final Response response) throws IOException {
//                            if (!response.isSuccessful()) {
//                                throw new IOException("Unexpected code " + response);
//                            } else {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "Server started !!", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//                    });
//                    try{
                    finalServer.stop();
                    isRunning = false;
//                        Toast.makeText(getApplicationContext(),"Server Stopped!!", Toast.LENGTH_SHORT).show();
//                    }
//                    catch (IOException e){
//                        e.printStackTrace();
//                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Server is already closed!!", Toast.LENGTH_SHORT).show();
                }
//                if(socket!=null){
//                    try {
//                        socket.close();
//                    }
//                    catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Python py = Python.getInstance();
                        PyObject temp = py.getModule("hello").callAttr("main");
                        String temp_text = temp.toString();
//                        message.setText("helllo");
                        Log.d("CREATION", temp_text);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                Toast.makeText(getApplicationContext(), temp_text, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                thread.start();
//                message.setText(temp_text);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public class MyHTTPD extends NanoHTTPD {
        public static final int PORT = 8765;
        public String response;
//        public String uri = session.getUri();
        public MyHTTPD() throws IOException {
            super(PORT);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            if (uri.equals("/hello")) {
                response = "HelloWorld";
                return newFixedLengthResponse(response);
            }
            return null;
        }
    }


}


