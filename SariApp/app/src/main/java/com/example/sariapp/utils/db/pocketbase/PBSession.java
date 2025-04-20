package com.example.sariapp.utils.db.pocketbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.sariapp.models.Users;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Mimics PocketBase authStore behavior with SharedPreferences.
 * Supports separate sessions for user and admin roles.
 */
public class PBSession {

    private static final String PREF_NAME_USER = "PBSessionPrefs_User";
    private static final String PREF_NAME_ADMIN = "PBSessionPrefs_Admin";
    private static final String KEY_RECORD = "auth_record";

    private static PBSession userInstance;
    private static PBSession adminInstance;

    private final SharedPreferences prefs;
    private Users user;

    private PBSession(Context context, String prefName) {
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        loadSession();
    }

    public static PBSession getUserInstance(Context context) {
        if (userInstance == null) {
            userInstance = new PBSession(context.getApplicationContext(), PREF_NAME_USER);
        }
        return userInstance;
    }

    public static PBSession getAdminInstance(Context context) {
        if (adminInstance == null) {
            adminInstance = new PBSession(context.getApplicationContext(), PREF_NAME_ADMIN);
        }
        return adminInstance;
    }

    private void loadSession() {
        String recordStr = prefs.getString(KEY_RECORD, null);
        if (recordStr != null) {
            try {
                JSONObject record = new JSONObject(recordStr);
                user = new Users.Builder()
                        .id(record.optString(Users.Fields.ID))
                        .name(record.optString(Users.Fields.NAME))
                        .email(record.optString(Users.Fields.EMAIL))
                        .tokenKey(record.optString(Users.Fields.TOKEN_KEY))
                        .build();
            } catch (JSONException e) {
                Log.e("PBSession", "Failed to parse saved record JSON", e);
            }
        }
    }

    private void saveSession() {
        SharedPreferences.Editor editor = prefs.edit();
        if (user != null) {
            try {
                JSONObject recordJson = new JSONObject();
                recordJson.put(Users.Fields.ID, user.getId());
                recordJson.put(Users.Fields.NAME, user.getName());
                recordJson.put(Users.Fields.EMAIL, user.getEmail());
                recordJson.put(Users.Fields.TOKEN_KEY, user.getTokenKey());
                editor.putString(KEY_RECORD, recordJson.toString());
            } catch (JSONException e) {
                Log.e("PBSession", "Failed to convert Users model to JSON", e);
            }
        } else {
            editor.remove(KEY_RECORD);
        }
        editor.apply();
    }

    public String getToken() {
        return user != null ? user.getTokenKey() : null;
    }

    public void setUser(Users user) {
        this.user = user;
        saveSession();
    }

    public Users getUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return user != null && user.getTokenKey() != null && !user.getTokenKey().isEmpty();
    }

    public void clear() {
        user = null;
        prefs.edit().clear().apply();
    }
}
