package com.example.sariapp.utils.db.pocketbase;

import android.util.Log;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PBConn {
    private static final OkHttpClient client = new OkHttpClient();
    private static PBConn instance;


    public static PBConn getInstance() {
        if (instance == null) {
            instance = new PBConn();
        }
        return instance;
    }

    private void executeRequest(Request request, PBCallback callback) {
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    callback.onError(responseBody);
                } else {
                    callback.onSuccess(responseBody);
                }
            }
        });
    }

    private Request.Builder addAuthHeader(Request.Builder builder, String token) {
        if (token != null) {
            builder.addHeader("Authorization",  token);
        }
        return builder;
    }

    public void sendPostRequest(String url, JSONObject json,  String token, PBCallback callback) {
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json");

        addAuthHeader(builder, token);

        executeRequest(builder.build(), callback);
    }

    public void sendPutRequest(String url, JSONObject json, String token, PBCallback callback) {
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Content-Type", "application/json");

        addAuthHeader(builder, token);

        executeRequest(builder.build(), callback);
    }

    public void sendDeleteRequest(String url, String token, PBCallback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();

        addAuthHeader(builder, token);

        executeRequest(builder.build(), callback);
    }

    public void sendGetRequest(String url, String token, PBCallback callback) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();

        addAuthHeader(builder, token);

        executeRequest(builder.build(), callback);
    }
    public OkHttpClient getClient() {
        return client;
    }
}
