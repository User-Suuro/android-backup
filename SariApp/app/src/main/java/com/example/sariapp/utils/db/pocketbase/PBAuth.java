package com.example.sariapp.utils.db.pocketbase;

import com.example.sariapp.utils.EnvConfig;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PBAuth {
    private static final String base_url = EnvConfig.PB_BASE_URL;
    private static PBAuth instance; // Singleton instance
    private final PBConn api = PBConn.getInstance();

    private PBAuth() {
        // Private constructor to prevent external instantiation
    }

    public static PBAuth getInstance() {
        if (instance == null) {
            instance = new PBAuth();
        }
        return instance;
    }

    // Method to authenticate using admin credentials
    public void authenticateAdmin(String email, String password, PBCallback callback) {
        String url = base_url + "/api/collections/_superusers/auth-with-password";
        JSONObject json = new JSONObject();
        try {
            json.put("identity", email);
            json.put("password", password);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        api.sendPostRequest(url, json, null, callback);
    }

    // Method to send Google ID Token to PocketBase
    public void loginToGoogle(String idToken, PBCallback callback) {
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
        api.sendPostRequest(url, json, null, callback);
    }

    // Method to request email verification for a user

    public void requestOTP(String email, PBCallback callback) {
        String url = base_url + "/api/collections/users/request-otp";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        api.sendPostRequest(url, json, null, callback);
    }

    public void loginUser(String email, String password, PBCallback callback) {
        String url = base_url + "/api/collections/users/auth-with-password";  // Endpoint for login via email and password

        JSONObject json = new JSONObject();
        try {
            json.put("identity", email);
            json.put("password", password);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        api.sendPostRequest(url, json,  null, callback);
    }


    public void authWithOTP(String otpId, String otp, PBCallback callback) {
        String url = base_url + "/api/collections/users/auth-with-otp";

        JSONObject json = new JSONObject();
        try {
            json.put("otpId", otpId);
            json.put("password", otp);
        } catch (JSONException e) {
            callback.onError("Invalid JSON format");
            return;
        }

        api.sendPostRequest(url, json, null, callback);
    }

    public void refreshAdminToken(String currentToken, PBCallback callback) {
        String url = base_url + "/api/collections/_superusers/auth-refresh";
        api.sendPostRequest(url,  new JSONObject(), currentToken, callback);
    }

    public void refreshUserToken(String currentToken, PBCallback callback) {
        String url = base_url + "/api/collections/users/auth-refresh";
        api.sendPostRequest(url, new JSONObject(), currentToken, callback);
    }

    public String getBaseUrl() {
        return base_url;
    }

}

