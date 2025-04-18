package com.example.sariapp.utils.db.pocketbase;

import android.content.Context;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBListCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PBCrud<T> {
    private final Context context;
    private final PBSession session;
    private final String authToken;
    private final PBAuth auth = PBAuth.getInstance();
    private final PBConn conn = PBConn.getInstance();
    private final String baseUrl = auth.getBaseUrl();
    private final Class<T> modelClass;
    private final String collectionName;

    public PBCrud(Context context, PBSession session, Class<T> modelClass, String collectionName) {
        this.context = context;
        this.session =  session;
        this.authToken = session.getToken();
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

    public void collectionAsList(String fieldName, String value, PBListCallback<T> callback) {
        list(fieldName, value, new PBCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    List<T> list = jsonArrayToList(json);
                    callback.onSuccess(list);
                } catch (JSONException e) {
                    callback.onError("Failed to parse JSON: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
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

    private T jsonToModel(JSONObject json) {
        try {
            T instance = modelClass.newInstance();
            for (Field field : modelClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(PBField.class)) {
                    PBField annotation = field.getAnnotation(PBField.class);
                    field.setAccessible(true);
                    Object value = json.opt(annotation.value());
                    if (value != null) field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<T> jsonArrayToList(JSONObject response) {
        List<T> list = new ArrayList<>();
        try {
            JSONArray items = response.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                list.add(jsonToModel(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
