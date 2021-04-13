package com.example.myapplication;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.TimerTask;
import java.util.Timer;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class fetchRequest{

    private static final String TAG = fetchRequest.class.getSimpleName();
    private static final String gateway_url = "http://1fc70c299c8d.ngrok.io";
    Timer timer = new Timer();
    private Context appContext;

    public fetchRequest(@NonNull Context appContext) {
        this.appContext = appContext;
    }
    public void scheduleFetchRequest() {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Log.i(TAG, "Fetching Data from Remote host");

                try {

                    OkHttpClient client = new OkHttpClient();

                    HttpUrl.Builder urlBuilder = HttpUrl.parse(gateway_url+"/getCodes").newBuilder();
                    //                            urlBuilder.addQueryParameter("code", "happy aa?");
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

                                    Log.i(TAG, "Running code ");
                                    String output = runCode(code);
                                    Log.i(TAG, "Sending output");
                                    ContextCompat.getMainExecutor(appContext).execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(appContext,output,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    sendOutput(output, currId, code, input);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error fetching data", e);
                }

            }

        },0,5000);//Update text every second
    }

    public void stopFetchRequest(){
        timer.cancel();
        timer.purge();
    }


    private String runCode(String code) throws FileNotFoundException {
        final ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
        String temp = "null";
        PrintStream out = new PrintStream(tempStream);
        Interpreter interpreter = new Interpreter();
        try {
            PrintStream stdout = System.out;
            System.setOut(out);
            interpreter.set("context", this);   //set any variable,
                                                        // you can refer to it directly from string
            try {
                interpreter.eval(code);             //execute code
                temp = tempStream.toString();
            } catch (Exception ex){
                Log.i(TAG,"script execution failed" + ex);
                temp = "error:  " + ex;
            }
            System.setOut(stdout);
            Log.i(TAG, "output:" + temp);
        }
        catch (Exception e){    //handle exception
            e.printStackTrace();
        }
        return temp;
    }

    private void sendOutput(String output, String currId, String code, String input){

        try {

            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse(gateway_url+"/submitOutput").newBuilder();
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
