package com.example.sariapp.utils.db.pocketbase;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBListCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBModelCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBRelation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PBCrud<T> {

    private final String authToken;
    private final PBAuth auth = PBAuth.getInstance();
    private final PBConn conn = PBConn.getInstance();
    private final String baseUrl = auth.getBaseUrl();
    private final Class<T> modelClass;
    private final String collectionName;

    public PBCrud(Class<T> modelClass, String collectionName, String token) {
        this.authToken = token;
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

    public void getById(String id, String token, PBModelCallback callback) {
        String url = baseUrl + "/api/collections/" + collectionName + "/records/" + id + "?expand=*";

        PBConn.getInstance().sendGetRequest(url, token, new PBCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    T item = parseModel(json, modelClass);
                    callback.onSuccess(item);
                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void list(String fieldName, String value, PBCallback callback) {
        try {
            String filter = fieldName + "=\"" + value + "\"";
            String encodedFilter = URLEncoder.encode(filter, "UTF-8");
            String url = baseUrl + "/api/collections/" + collectionName + "/records?expand=*&filter=" + encodedFilter;

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
        conn.sendPutRequest(url, json, authToken, callback);
    }

    public void delete(String recordId, PBCallback callback) {
        String url = baseUrl + "/api/collections/" + collectionName + "/records/" + recordId;
        conn.sendDeleteRequest(url, authToken, callback);
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
                    e.printStackTrace();
                }
            }
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    private <R> R parseModel(JSONObject json, Class<?> clazz) throws Exception {
        Object instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(PBField.class)) {
                String key = field.getAnnotation(PBField.class).value();
                Object value = json.opt(key);
                if (value != null && !JSONObject.NULL.equals(value)) {
                    field.set(instance, value);
                }
            }

            if (field.isAnnotationPresent(PBRelation.class)) {
                PBRelation rel = field.getAnnotation(PBRelation.class);
                String key = field.getName();

                JSONObject expandObj = json.optJSONObject("expand");
                if (expandObj != null) {
                    if (rel.isList()) {
                        JSONArray relArray = expandObj.optJSONArray(key);
                        if (relArray != null) {
                            List<Object> relList = new ArrayList<>();
                            for (int i = 0; i < relArray.length(); i++) {
                                JSONObject relJson = relArray.getJSONObject(i);
                                Object relObj = parseModel(relJson, rel.relatedType());
                                relList.add(relObj);
                            }
                            field.set(instance, relList);
                        }
                    } else {
                        JSONObject relJson = expandObj.optJSONObject(key);
                        if (relJson != null) {
                            Object relObj = parseModel(relJson, rel.relatedType());
                            field.set(instance, relObj);
                        }
                    }
                }
            }
        }

        return (R) instance;
    }

    private List<T> jsonArrayToList(JSONObject response) {
        List<T> list = new ArrayList<>();
        try {
            JSONArray items = response.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                list.add(parseModel(obj, modelClass));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
