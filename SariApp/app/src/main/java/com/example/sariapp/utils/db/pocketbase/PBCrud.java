package com.example.sariapp.utils.db.pocketbase;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;


import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PBCrud<T> {

    private final Class<T> modelClass;
    private final OkHttpClient client;
    private final String baseUrl;
    private final String collectionName;
    private final String authToken;

    private final String role;

    public PBCrud(Class<T> modelClass, PBAuth pb, String collectionName, String role) {
        this.modelClass = modelClass;
        this.collectionName = collectionName;
        this.client = pb.getClient();
        this.baseUrl = pb.getBaseUrl();
        this.authToken = pb.getToken();
        this.role = role;
    }

    /// CREATE method (Asynchronous)
    public void create(T model, final Callback callback) {
        JSONObject json = modelToJson(model);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "/api/collections/" + collectionName + "/records?=")
                .addHeader("Authorization", role + " " + authToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Create failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Create failed: " + response.body().string());
                } else {
                    callback.onSuccess(response.body().string());
                }
            }
        });
    }


    // READ method (Get all records - Asynchronous)
    public void read(final Callback callback) {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/collections/" + collectionName + "/records")
                .addHeader("Authorization", role + " " + authToken)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Read failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Read failed: " + response.code());
                } else {
                    callback.onSuccess(response.body().string());
                }
            }
        });
    }

    // READ method (Get a specific record by ID - Asynchronous)
    public void read(String recordId, final Callback callback) {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/collections/" + collectionName + "/records/" + recordId)
                .addHeader("Authorization", role + " " + authToken)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Read failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Read failed: " + response.code());
                } else {
                    callback.onSuccess(response.body().string());
                }
            }
        });
    }

    // UPDATE method (Asynchronous)
    public void update(String recordId, T model, final Callback callback) {
        JSONObject json = modelToJson(model);
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(baseUrl + "/api/collections/" + collectionName + "/records/" + recordId)
                .addHeader("Authorization", role + " " + authToken)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Update failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Update failed: " + response.code());
                } else {
                    callback.onSuccess(response.body().string());
                }
            }
        });
    }

    // DELETE method (Asynchronous)
    public void delete(String recordId, final Callback callback) {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/collections/" + collectionName + "/records/" + recordId)
                .addHeader("Authorization", role + " " + authToken)
                .addHeader("Content-Type", "application/json")
                .delete()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Delete failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Delete failed: " + response.code());
                } else {
                    callback.onSuccess(response.body().string());
                }
            }
        });
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

    public interface Callback {
        void onSuccess(String result);
        void onError(String error);
    }

}
