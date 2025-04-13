package com.example.sariapp.utils.db.pocketbase;

import android.widget.Toast;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

public class PBCrud<T> {

    private final PBAuth auth = PBAuth.getInstance();
    private final PBConn conn = PBConn.getInstance();
    private final String baseUrl = auth.getBaseUrl();
    private final String authToken = auth.getToken();
    private final Class<T> modelClass;
    private final String collectionName;

    public PBCrud(Class<T> modelClass, String collectionName) {
        this.modelClass = modelClass;
        this.collectionName = collectionName;
    }

    public void create(T model, PBCallback callback) {
        JSONObject json = modelToJson(model);
        String url = baseUrl + "/api/collections/" + collectionName + "/records";
        conn.sendPostRequest(url, json, authToken, callback);
    }

    public void view(String recordId, PBCallback callback) {
        String url = baseUrl + "/api/collections/" + collectionName + "/records/" + recordId;
        conn.sendGetRequest(url, authToken, callback);
    }

    public void list(String fieldName, String value, PBCallback callback) {
        // Create the URL with a filter for the specified field
        try {
            String filter = fieldName + "=\"" + value + "\"";
            String encodedFilter = URLEncoder.encode(filter, "UTF-8");
            String url = baseUrl + "/api/collections/" + collectionName + "/records?filter=" + encodedFilter;

            conn.sendGetRequest(url, authToken, callback);
        } catch (UnsupportedEncodingException e) {
            callback.onError("Encoding failed: " + e.getMessage());
        }
    }

    public void update(String recordId, T model, PBCallback callback) {
        JSONObject json = modelToJson(model);
        String url = baseUrl + "/api/collections/" + collectionName + "/records/" + recordId;
        conn.sendPutRequest(url, json,  authToken, callback);
    }

    public void delete(String recordId, PBCallback callback) {
        String url = baseUrl + "/api/collections/" + collectionName + "/records/" + recordId;
        conn.sendDeleteRequest(url,  authToken, callback);
    }

    private JSONObject modelToJson(T model) {
        JSONObject json = new JSONObject();
        for (Field field : modelClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PBField.class)) {
                PBField annotation = field.getAnnotation(PBField.class);
                field.setAccessible(true);
                try {
                    Object value = field.get(model);
                    if (value != null) {
                        json.put(annotation.value(), value);
                    }
                } catch (JSONException | IllegalAccessException e) {
                    e.printStackTrace(); // You can improve error handling here
                }
            }
        }
        return json;
    }
}
