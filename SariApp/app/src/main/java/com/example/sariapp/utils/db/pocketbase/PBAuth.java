package com.example.sariapp.utils.db.pocketbase;

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

public class PBAuth {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String base_url = "https://suuro.pockethost.io";
    private static PBAuth instance; // Singleton instance
    private String token;

    private PBAuth() {
        // Private constructor to prevent external instantiation
    }

    public static PBAuth getInstance() {
        if (instance == null) {
            instance = new PBAuth();
        }
        return instance;
    }

    public interface Callback {
        void onSuccess(String result);
        void onError(String error);
    }


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

    // Method to request email verification for a user

    public void requestOTP(String email, Callback callback) {
        String url = base_url + "/api/collections/users/request-otp";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        sendPostRequest(url, json, new Callback() {
            @Override
            public void onSuccess(String result) {
                // Parse the response to get OTP ID if needed
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String otpId = responseJson.optString("otpId");
                    if (!otpId.isEmpty()) {
                        callback.onSuccess("OTP sent successfully. OTP ID: " + otpId);
                    } else {
                        callback.onError("OTP ID not found in the response.");
                    }
                } catch (JSONException e) {
                    callback.onError("Error parsing OTP response.");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to send OTP: " + error);
            }
        });
    }

    public void authWithOTP(String otpId, String otp, Callback callback) {
        String url = base_url + "/api/collections/users/auth-with-otp";

        JSONObject json = new JSONObject();
        try {
            json.put("otpId", otpId);
            json.put("otp", otp);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        sendPostRequest(url, json, new Callback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    String authToken = responseJson.optString("token");
                    if (!authToken.isEmpty()) {
                        setToken(authToken);  // Save token for future use
                        callback.onSuccess("User authenticated successfully.");
                    } else {
                        callback.onError("Authentication failed: Auth token not found.");
                    }
                } catch (JSONException e) {
                    callback.onError("Error parsing authentication response.");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Authentication failed: " + error);
            }
        });
    }



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

