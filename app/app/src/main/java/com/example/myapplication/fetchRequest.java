package com.example.myapplication;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.annotation.NonNull;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

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

public class fetchRequest extends Worker {

    private static final String TAG = fetchRequest.class.getSimpleName();
    private String gateway_url = "http://7f732fdbed45.ngrok.io";

    public fetchRequest(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        //simulate slow work
        // WorkerUtils.makeStatusNotification("Fetching Data", applicationContext);
        Log.i(TAG, "Fetching Data from Remote host");
//        WorkerUtils.sleep();

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
                    if (!response.isSuccessful() && response.body() != null) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        if(response.body().toString().length() > 0 && !response.body().toString().isEmpty()) {
                            String resp = response.body().string();
                            JSONObject data = null;
                            try {
                                data = new JSONObject(resp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i(TAG, "before Json:" + resp);
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

                            String output = runCode(code);

                            sendOutput(output, currId, code, input);
                        }
                    }
                }
            });
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error fetching data", e);
            return Result.failure();
        }

    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(TAG, "OnStopped called for this worker");
    }

    public String runCode(String code){
        Interpreter interpreter = new Interpreter();
        String temp = "null";
        try {
            interpreter.set("context", this);//set any variable, you can refer to it directly from string
            interpreter.eval(code);//execute code
            temp = interpreter.get("result").toString();
        }
        catch (Exception e){    //handle exception
            e.printStackTrace();
        }
        return temp;
    }

    public void sendOutput(String output, String currId, String code, String input){

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
                        if(response.body().toString().length() > 0 && !response.body().toString().isEmpty()) {

                            JSONObject data = null;
                            try {
                                data = new JSONObject(response.body().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i(TAG, "Json String from gateway " + data);

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error returning output", e);
        }
    }
}
