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

    /**
     * Set session data after successful login/register
     */
    public void setSession(String token, JSONObject record) {
        this.token = token;
        this.record = record;
    }

    /**
     * Populate session from login JSON response
     */
    public void setSessionFromResponse(String responseJson) {
        try {
            JSONObject json = new JSONObject(responseJson);
            setToken(json.optString("token"));
            setRecord(json.optJSONObject("record"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        return token != null && !token.isEmpty();
    }

    public void clear() {
        token = null;
        record = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JSONObject getRecord() {
        return record;
    }

    public void setRecord(JSONObject record) {
        this.record = record;
    }

    public String getUserId() {
        return record != null ? record.optString("id", "") : "";
    }

    public String getUserEmail() {
        return record != null ? record.optString("email", "") : "";
    }

    public String getAuthHeader() {
        return isValid() ? "Bearer " + token : "";
    }
}
