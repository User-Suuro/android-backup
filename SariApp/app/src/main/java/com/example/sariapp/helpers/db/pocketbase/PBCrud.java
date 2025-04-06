package com.example.sariapp.helpers.db.pocketbase;

import com.example.sariapp.helpers.db.pocketbase.PBTypes.PBField;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PBCrud<T> {
    private static final Map<Class<?>, PBCrud<?>> INSTANCES = new HashMap<>();

    private final Class<T> modelClass;
    private final OkHttpClient client;
    private final String baseUrl;
    private final String collectionName;
    private final String authToken;

    private PBCrud(Class<T> modelClass, OkHttpClient client, String baseUrl, String collectionName, String authToken) {
        this.modelClass = modelClass;
        this.client = client;
        this.baseUrl = baseUrl;
        this.collectionName = collectionName;
        this.authToken = authToken;
    }

    // Static factory method (Singleton Pattern)
    public static synchronized <T> PBCrud<T> getInstance(Class<T> cls, OkHttpClient client, String baseUrl, String collectionName, String authToken) {
        if (!INSTANCES.containsKey(cls)) {
            PBCrud<T> instance = new PBCrud<>(cls, client, baseUrl, collectionName, authToken);
            INSTANCES.put(cls, instance);
        }
        @SuppressWarnings("unchecked")
        PBCrud<T> typedInstance = (PBCrud<T>) INSTANCES.get(cls);
        return typedInstance;
    }

    // CREATE method
    public void create(T model) throws IOException {
        JSONObject json = modelToJson(model);
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(baseUrl + "api/collections/" + collectionName + "/records")
                .addHeader("Authorization", "Admin " + authToken)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Create failed: " + response.code());
            }
        }
    }

    // READ method (Get all records)
    public String read() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "api/collections/" + collectionName + "/records")
                .addHeader("Authorization", "Admin " + authToken)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Read failed: " + response.code());
            }
            return response.body().string();
        }
    }

    // READ method (Get a specific record by ID)
    public String read(String recordId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "api/collections/" + collectionName + "/records/" + recordId)
                .addHeader("Authorization", "Admin " + authToken)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Read failed: " + response.code());
            }
            return response.body().string();
        }
    }

    // UPDATE method
    public void update(String recordId, T model) throws IOException {
        JSONObject json = modelToJson(model);
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(baseUrl + "api/collections/" + collectionName + "/records/" + recordId)
                .addHeader("Authorization", "Admin " + authToken)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Update failed: " + response.code());
            }
        }
    }

    // DELETE method
    public void delete(String recordId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "api/collections/" + collectionName + "/records/" + recordId)
                .addHeader("Authorization", "Admin " + authToken)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Delete failed: " + response.code());
            }
        }
    }

    // Convert model object to JSON
    private JSONObject modelToJson(T model) {
        JSONObject json = new JSONObject();
        for (Field field : modelClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PBField.class)) {
                PBField annotation = field.getAnnotation(PBField.class);
                field.setAccessible(true);
                try {
                    Object value = field.get(model);
                    json.put(annotation.value(), value);
                } catch (JSONException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return json;
    }
}
