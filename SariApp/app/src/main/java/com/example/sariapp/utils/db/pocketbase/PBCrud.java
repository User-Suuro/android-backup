package com.example.sariapp.utils.db.pocketbase;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBCallback;
import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;


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

    public PBListBuilder listBuilder() {
        return new PBListBuilder();
    }

    public class PBListBuilder {
        private Integer page;
        private Integer perPage;
        private String filter;
        private String sort;
        private String expand;
        private String fields;
        private Boolean skipTotal;

        public PBListBuilder page(int page) {
            this.page = page;
            return this;
        }

        public PBListBuilder perPage(int perPage) {
            this.perPage = perPage;
            return this;
        }

        public PBListBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public PBListBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public PBListBuilder expand(String expand) {
            this.expand = expand;
            return this;
        }

        public PBListBuilder fields(String fields) {
            this.fields = fields;
            return this;
        }

        public PBListBuilder skipTotal(boolean skipTotal) {
            this.skipTotal = skipTotal;
            return this;
        }

        public void execute(PBCallback callback) {
            try {
                StringBuilder urlBuilder = new StringBuilder(baseUrl)
                        .append("/api/collections/")
                        .append(collectionName)
                        .append("/records?");

                if (page != null) {
                    urlBuilder.append("page=").append(page).append("&");
                }
                if (perPage != null) {
                    urlBuilder.append("perPage=").append(perPage).append("&");
                }
                if (filter != null) {
                    urlBuilder.append("filter=").append(URLEncoder.encode(filter, "UTF-8")).append("&");
                }
                if (sort != null) {
                    urlBuilder.append("sort=").append(URLEncoder.encode(sort, "UTF-8")).append("&");
                }
                if (expand != null) {
                    urlBuilder.append("expand=").append(URLEncoder.encode(expand, "UTF-8")).append("&");
                }
                if (fields != null) {
                    urlBuilder.append("fields=").append(URLEncoder.encode(fields, "UTF-8")).append("&");
                }
                if (skipTotal != null) {
                    urlBuilder.append("skipTotal=").append(skipTotal).append("&");
                }

                String url = urlBuilder.toString();
                if (url.endsWith("&")) {
                    url = url.substring(0, url.length() - 1);
                }

                conn.sendGetRequest(url, authToken, callback);
            } catch (Exception e) {
                callback.onError("Encoding failed: " + e.getMessage());
            }
        }
    }
}
