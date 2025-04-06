package com.example.sariapp.helpers.db.pocketbase;

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
    private static PBConn instance; // Singleton instance
    private static String base_url;

    private PBConn() {
        // Private constructor to prevent external instantiation
    }

    public static PBConn getInstance() {
        if (instance == null) {
            instance = new PBConn();
        }
        return instance;
    }

    public interface Callback {
        void onSuccess(String result);
        void onError(String error);
    }

    public void authenticateAdmin(String email, String password, String pb_url, Callback callback) {
        base_url = pb_url;
        String url = base_url + "/api/admins/auth-with-password";
        JSONObject json = new JSONObject();
        try {
            json.put("identity", email);
            json.put("password", password);
        } catch (Exception e) {
            callback.onError("Invalid JSON");
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Login failed: " + response.code());
                } else {
                    String responseBody = response.body().string();
                    try {
                        callback.onSuccess(responseBody);
                    } catch (Exception e) {
                        callback.onError("Invalid response JSON");
                    }
                }
            }
        });
    }
}
