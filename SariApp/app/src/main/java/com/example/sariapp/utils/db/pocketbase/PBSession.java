package com.example.sariapp.utils.db.pocketbase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Mimics PocketBase authStore behavior.
 */
public class PBSession {

    private static PBSession instance;
    private String token;
    private JSONObject record;

    private PBSession() {
        // private constructor
    }

    public static PBSession getInstance() {
        if (instance == null) {
            instance = new PBSession();
        }
        return instance;
    }

    public void clear() {
        token = null;
        record = null;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setRecord(JSONObject record) {
        this.record = record;
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

}
