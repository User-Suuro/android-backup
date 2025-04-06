package com.example.sariapp.helpers.db.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class PBToken {
    private static final String PREFS_NAME = "token";
    private static final String KEY_ADMIN_TOKEN = "admin_token";
    private static final String KEY_PB_URL = "pb_url";

    private final SharedPreferences prefs;

    public PBToken(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAdminToken(String token) {
        prefs.edit().putString(KEY_ADMIN_TOKEN, token).apply();
    }

    public String getAdminToken() {
        return prefs.getString(KEY_ADMIN_TOKEN, null);
    }

    public void clearAdminToken() {
        prefs.edit().remove(KEY_ADMIN_TOKEN).apply();
    }

    public void savePBUrl(String url) {
        prefs.edit().putString(KEY_PB_URL, url).apply();
    }

    public String getPBUrl() {
        return prefs.getString(KEY_PB_URL, null);
    }

    public void clearPrefs() {
        prefs.edit().clear().apply();
    }
}
