package com.example.myapplication;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import bsh.Interpreter;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.TimerTask;
import java.util.Timer;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import java.io.FileInputStream;

public class fetchRequest{

    private static final String TAG = fetchRequest.class.getSimpleName();
    private static final String gateway_url = "https://android-lambda.herokuapp.com";
    Timer timer;
    public String username;
    private Context appContext;

    public fetchRequest(@NonNull Context appContext, String username) {
        this.appContext = appContext;
        this.username = username;
    }
    public void scheduleFetchRequest() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Log.i(TAG, "Fetching Data from Remote host");

                try {

                    OkHttpClient client = new OkHttpClient();

                    HttpUrl.Builder urlBuilder = HttpUrl.parse(gateway_url+"/getCodes").newBuilder();
                    urlBuilder.addQueryParameter("username", username);
                    //                            urlBuilder.addQueryParameter("user", "vogella");
                    String url = urlBuilder.build().toString();

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful() || response.body() == null) {
                                throw new IOException("Unexpected code " + response);
                            } else {
                                if(response.body().toString().length() > 0) {
                                    String json = response.body().string();
                                    JSONObject data = null;
                                    if(json.lastIndexOf("}")<=0){
                                        return;
                                    }
                                    try {
                                        data = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Log.i(TAG, "before Json:" + json);
                                    Log.i(TAG, "Json String from gateway " + data);

                                    String code = null;
                                    String input = null;
                                    String currId = null;

                                    try {
                                        code = data.getString("code");
                                        input = data.getString("input");
                                        currId = data.getString("currId");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    final String tempQueryID = currId;
                                    Timer runTimer = new Timer();

                                    TimerTask runTimerTask = new TimerTask() {

                                        @Override
                                        public void run() {
                                            sendQueryID(tempQueryID);
                                        }
                                    };
                                    timer.schedule(runTimerTask,0,1000);
                                    Log.i(TAG, "Running code ");
                                    String[] outputs = new String[2];
                                    outputs = runCode(code, input);
                                    Log.i(TAG, "Sending output");
//                                    ContextCompat.getMainExecutor(appContext).execute(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(appContext, outputs[0]+outputs[1],Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                                    sendOutput(outputs[0], currId, code, input, outputs[1]);
                                    runTimerTask.cancel();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error fetching data", e);
                }

            }

        },0,5000);//Update text every 5 seconds
    }

    public void stopFetchRequest(){
        timer.cancel();
    }


    private String[] runCode(String code, String input) throws FileNotFoundException {
        final ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
        String[] outputs = new String[2];
        PrintStream out = new PrintStream(tempStream);
        Interpreter interpreter = new Interpreter();
        try {
            PrintStream stdout = System.out;
            InputStream stdin = System.in;
            System.setOut(out);
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            interpreter.set("context", this);   //set any variable,
                                                        // you can refer to it directly from string
            try {
                interpreter.eval(code);             //execute code
                outputs[0] = tempStream.toString();
                Log.i(TAG, "output:" + outputs[0]);
            } catch (Exception ex){
                Log.i(TAG,"script execution failed: " + ex);
                outputs[1] = "error:  " + ex;
            }
            System.setOut(stdout);
            System.setIn(stdin);
        }
        catch (Exception e){    //handle exception
            e.printStackTrace();
        }
        return outputs;
    }

    private void sendQueryID(String queryID){

        try {

            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse(gateway_url+"/processing").newBuilder();
            urlBuilder.addQueryParameter("query_id", queryID);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful() && response.body() != null) {
                        throw new IOException("Unexpected code " + response);
                    } else {
//                        Log.i(TAG, "Json String from gateway ");
                        ;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error sending queryID", e);
        }
    }

    private void sendOutput(String output, String currId, String code, String input, String error){

        try {

            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse(gateway_url+"/submitOutput").newBuilder();
            urlBuilder.addQueryParameter("username", username);
            urlBuilder.addQueryParameter("error", error);
            urlBuilder.addQueryParameter("code_output", output);
            urlBuilder.addQueryParameter("currId", currId);
            urlBuilder.addQueryParameter("code_input", input);
            urlBuilder.addQueryParameter("code", code);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful() && response.body() != null) {
                        throw new IOException("Unexpected code " + response);
                    } else {
//                        Log.i(TAG, "Json String from gateway ");
                        ;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error returning output", e);
        }
    }
}
