package com.example.sariapp.helpers.db.pocketbase;

import android.util.Log;
import org.json.JSONException;
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
    private static final String base_url = "https://suuro.pockethost.io";
    private static PBConn instance; // Singleton instance
    private String token;

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

    // -- API -- //

    // Helper method for sending POST requests
    private void sendPostRequest(String url, JSONObject json, Callback callback) {
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
                Log.e("PBConn", "Request failed", e);
                callback.onError("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Request failed: " + response.code());
                } else {
                    String responseBody = response.body().string();
                    if (responseBody.isEmpty()) {
                        callback.onError("Empty response body");
                        return;
                    }
                    callback.onSuccess(responseBody);
                }
            }
        });
    }

    // Helper method for sending GET requests
    private void sendGetRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("PBConn", "Request failed", e);
                callback.onError("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Request failed: " + response.code());
                } else {
                    String responseBody = response.body().string();
                    if (responseBody.isEmpty()) {
                        callback.onError("Empty response body");
                        return;
                    }
                    callback.onSuccess(responseBody);
                }
            }
        });
    }

    // -- POCKETBASE -- //

    // Method to authenticate using admin credentials
    public void authenticateAdmin(String email, String password, Callback callback) {
        String url = base_url + "/api/collections/_superusers/auth-with-password";
        JSONObject json = new JSONObject();
        try {
            json.put("identity", email);
            json.put("password", password);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        // Use the generic POST request helper method
        sendPostRequest(url, json, callback);
    }

    // Method to send Google ID Token to PocketBase
    public void loginToGoogle(String idToken, Callback callback) {
        String url = base_url + "/api/collections/users/auth-with-oauth2";  // Adjust this URL if needed

        // Prepare JSON with the ID token and provider
        JSONObject json = new JSONObject();
        try {
            json.put("provider", "google");
            json.put("code", idToken);  // Sending the Google ID token as "code"
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        // Use the generic POST request helper method
        sendPostRequest(url, json, new Callback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String authToken = responseJson.optString("token");
                    if (authToken.isEmpty()) {
                        callback.onError("Auth token not found in response");
                    } else {
                        setToken(authToken);  // Save token for future use
                        callback.onSuccess("Login successful");
                    }
                } catch (JSONException e) {
                    callback.onError("Error parsing response JSON");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);  // Forward the error
            }
        });
    }

    // Set token when user is authenticated
    public void setToken(String token) {
        this.token = token;
    }

    // Get token when needed
    public String getToken() {
        return token;
    }

    // Check if logged in
    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public String getBaseUrl() {
        return base_url;
    }

    public OkHttpClient getClient() {
        return client;
    }

    // Optional: log out
    public void logout() {
        token = null;
    }
}

