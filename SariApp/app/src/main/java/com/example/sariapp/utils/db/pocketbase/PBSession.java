package com.example.sariapp.utils.db.pocketbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Mimics PocketBase authStore behavior with SharedPreferences.
 * Supports separate sessions for user and admin roles.
 */
public class PBSession {

    private static final String PREF_NAME_USER = "PBSessionPrefs_User";
    private static final String PREF_NAME_ADMIN = "PBSessionPrefs_Admin";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_RECORD = "auth_record";

    private static PBSession userInstance;
    private static PBSession adminInstance;

    private final SharedPreferences prefs;
    private String token;
    private JSONObject record;

    private PBSession(Context context, String prefName) {
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        loadSession();
    }

    /**
     * Retrieves the singleton instance for the user role.
     */
    public static PBSession getUserInstance(Context context) {
        if (userInstance == null) {
            userInstance = new PBSession(context.getApplicationContext(), PREF_NAME_USER);
        }
        return userInstance;
    }

    /**
     * Retrieves the singleton instance for the admin role.
     */
    public static PBSession getAdminInstance(Context context) {
        if (adminInstance == null) {
            adminInstance = new PBSession(context.getApplicationContext(), PREF_NAME_ADMIN);
        }
        return adminInstance;
    }

    private void loadSession() {
        token = prefs.getString(KEY_TOKEN, null);
        String recordStr = prefs.getString(KEY_RECORD, null);
        if (recordStr != null) {
            try {
                record = new JSONObject(recordStr);
            } catch (JSONException e) {
                Log.e("PBSession", "Failed to parse saved record JSON", e);
            }
        }
    }

    private void saveSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_RECORD, record != null ? record.toString() : null);
        editor.apply();
    }

    /**
     * Sets the token for the current session.
     */
    public void setToken(String token) {
        this.token = token;
        saveSession();
    }

    /**
     * Sets the token for the user session.
     */
    public static void setUserToken(Context context, String token) {
        getUserInstance(context).setToken(token);
    }

    /**
     * Sets the token for the admin session.
     */
    public static void setAdminToken(Context context, String token) {
        getAdminInstance(context).setToken(token);
    }

    public String getToken() {
        return token;
    }

    public void setRecord(JSONObject record) {
        this.record = record;
        saveSession();
    }

    public JSONObject getRecord() {
        return record;
    }

    public String getUserId() {
        return record != null ? record.optString("id", "") : "";
    }

    public String getUserEmail() {
        return record != null ? record.optString("email", "") : "";
    }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public void clear() {
        token = null;
        record = null;
        prefs.edit().clear().apply();
    }
}
