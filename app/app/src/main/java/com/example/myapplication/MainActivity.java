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

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.Call;
import okhttp3.Callback;

import fi.iki.elonen.NanoHTTPD;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
//import android.net.wifi.WifiManager;
//import android.text.format.Formatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import bsh.Interpreter;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.NetworkType;
import androidx.work.BackoffPolicy;

import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    TextView welcome_note, message;
    Button start_btn, stop_btn, logout_btn;
    public String username;
    CheckBox rememberMe;
    //    ServerSocket server = null;
//    Socket socket = null;
    boolean isRunning = false;
//    android.app.Application application =
    private WorkManager mWorkManager = WorkManager.getInstance(getApplication());
//    Thread Thread1 = null;
//    DataInputStream in = null;


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

//        MyHTTPD server = null;
//        try {
//            server = new MyHTTPD();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        MyHTTPD finalServer = server;

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        message.setText(response);
//                        try {
//                            FileOutputStream fileout=openFileOutput("demo.py", MODE_PRIVATE);
//                            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
////                            outputWriter.write("print(\"damnu\")\ndef main():\n\tn=365\n\tprint(n)\n\treturn n\ndef main2():\n\treturn35");
//                            outputWriter.write("print(\"damn\")");
//                            outputWriter.close();

//                        Interpreter interpreter = new Interpreter();
//                        String temp = "eval";
//                        try {
//                            interpreter.set("context", this);//set any variable, you can refer to it directly from string
//                            interpreter.eval("result = (7+21*6)/(32-27)");//execute code
//                            temp = interpreter.get("result").toString();
//                        }
//                        catch (Exception e){    //handle exception
//                            e.printStackTrace();
//                        }
//
//                        final String temp2 = temp;
//                        //display file saved message
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), getIPAddress(true) , Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

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
                        else {
//                            try {
//                                OkHttpClient client = new OkHttpClient();
//
//                                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://73037d736a67.ngrok.io/conn_request").newBuilder();
//                                //                            urlBuilder.addQueryParameter("code", "happy aa?");
//                                //                            urlBuilder.addQueryParameter("user", "vogella");
//                                String url = urlBuilder.build().toString();
//
//                                Request request = new Request.Builder()
//                                        .url(url)
//                                        .build();
//
//                                client.newCall(request).enqueue(new Callback() {
//                                    @Override
//                                    public void onFailure(Call call, IOException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    @Override
//                                    public void onResponse(Call call, final Response response) throws IOException {
//                                        if (!response.isSuccessful()) {
//                                            throw new IOException("Unexpected code " + response);
//                                        } else {
//                                            //                                        finalServer.start();
//                                            isRunning = true;
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(getApplicationContext(), "Server started !!", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        }
//                                    }
//                                });
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            // Create Network constraint
                            Constraints constraints = new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build();


                            PeriodicWorkRequest periodicRunRequestWork =
                                    new PeriodicWorkRequest.Builder(fetchRequest.class, 15, TimeUnit.SECONDS)
                                            .addTag("run_requests")
                                            .setConstraints(constraints)
                                            // setting a backoff on case the work needs to retry
                                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                                            .build();
                            mWorkManager.enqueueUniquePeriodicWork(
                                    "fetchRequest",
                                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                                    periodicRunRequestWork //work request
                            );
                            isRunning = true;
                        }

//                            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//                            InetAddress ia = InetAddress.getLocalHost();
//                            String ip = ia.getHostAddress();


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
//                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://054072438282.ngrok.io/disc_request").newBuilder();
////                    urlBuilder.addQueryParameter("type", "1");
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
//                                        Toast.makeText(getApplicationContext(), "Server stopped !!", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//                    });

//                    finalServer.stop();
                    isRunning = false;

                } else {
                    // close server
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

//    @NotNull
//    public static String getIPAddress(boolean useIPv4) {
//        try {
//            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface intf : interfaces) {
//                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
//                for (InetAddress addr : addrs) {
//                    if (!addr.isLoopbackAddress()) {
//                        String sAddr = addr.getHostAddress();
//
//                        boolean isIPv4 = sAddr.indexOf(':')<0;
//
//                        if (useIPv4) {
//                            if (isIPv4)
//                                return sAddr;
//                        } else {
//                            if (!isIPv4) {
//                                int delim = sAddr.indexOf('%');
//                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception ignored) { }
//        return "";
//    }

//    public class MyHTTPD extends NanoHTTPD {
//        public static final int PORT = 8765;
//        public String response;
////        public String uri = session.getUri();
//        public MyHTTPD() throws IOException {
//            super(null, PORT);
//        }
//
//        @Override
//        public Response serve(IHTTPSession session) {
//            String uri = session.getUri();
//            if (uri.equals("/hello")) {
//
//                String code = session.getParameters().get("code").get(0);
//
//                Interpreter interpreter = new Interpreter();
//                String temp = "eval";
//                try {
//                    interpreter.set("context", this);//set any variable, you can refer to it directly from string
//                    interpreter.eval(code);//execute code
//                    temp = interpreter.get("result").toString();
//                }
//                catch (Exception e){    //handle exception
//                    e.printStackTrace();
//                }
//
//                final String temp2 = temp;
//                //display file saved message
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), temp2 , Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                response = temp2;
//                return newFixedLengthResponse(response);
//            }
//            return null;
//        }
//    }


}


