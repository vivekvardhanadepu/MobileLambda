package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView welcome_note, message;
    Button start_btn, stop_btn, logout_btn;
    public String username;
    CheckBox rememberMe;
    boolean isRunning = false;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


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

        fetchRequest periodicFetchRequest = new fetchRequest();

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

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
                            periodicFetchRequest.scheduleFetchRequest();
                            isRunning = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Server has started!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

//
                    }
                });
                thread.start();
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    periodicFetchRequest.stopFetchRequest();
                    isRunning = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Server is stopped!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // close server
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Server is already stopped!!", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                FirebaseAuth.getInstance().signOut();
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
}


