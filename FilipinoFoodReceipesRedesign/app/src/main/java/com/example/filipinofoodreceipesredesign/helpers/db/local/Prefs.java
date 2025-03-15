package com.example.filipinofoodreceipesredesign.helpers.db.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Prefs<T> {
    private static final String PREF_NAME = "app_prefs";
    private static final String AUTO_INCREMENT_KEY = "auto_increment_";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Class<T> modelClass;
    private String entityName;

    public Prefs(Context context, Class<T> modelClass) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.modelClass = modelClass;
        this.entityName = modelClass.getSimpleName();
    }

    private static class EntityWrapper<T> {
        List<T> rows = new ArrayList<>();
        DynamicValues dynamic_values = new DynamicValues();
    }

    private static class DynamicValues {
        public List<String> roles = new ArrayList<>();
        public List<String> categories = new ArrayList<>();
        public List<String> statuses = new ArrayList<>();
    }

    private EntityWrapper<T> getEntityWrapper() {
        String json = sharedPreferences.getString(entityName, null);
        if (json == null || json.isEmpty()) {
            return new EntityWrapper<>();
        }
        try {
            Type type = TypeToken.getParameterized(EntityWrapper.class, modelClass).getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return new EntityWrapper<>();
        }
    }

    private void saveEntityWrapper(EntityWrapper<T> wrapper) {
        sharedPreferences.edit().putString(entityName, gson.toJson(wrapper)).apply();
    }

    private long getNextId() {
        long nextId = sharedPreferences.getLong(AUTO_INCREMENT_KEY + entityName, 1);
        sharedPreferences.edit().putLong(AUTO_INCREMENT_KEY + entityName, nextId + 1).apply();
        return nextId;
    }

    private long getId(T item) {
        try {
            Field idField = item.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return idField.getLong(item);
        } catch (Exception e) {
            return -1;
        }
    }

    private void setId(T item, long id) {
        try {
            Field idField = item.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.setLong(item, id);
        } catch (Exception ignored) { }
    }

    public void save(T item) {
        EntityWrapper<T> wrapper = getEntityWrapper();
        long  id = getId(item);
        if (id == -1) {
            id = getNextId();
            setId(item, id);
        }

        long finalId = id;
        wrapper.rows.removeIf(existingItem -> getId(existingItem) == finalId);
        wrapper.rows.add(item);
        saveEntityWrapper(wrapper);
    }

    public List<T> getAll() {
        return getEntityWrapper().rows;
    }

    public T getById(long id) {
        return getEntityWrapper().rows.stream().filter(item -> getId(item) == id).findFirst().orElse(null);
    }

    public void remove(long id) {
        EntityWrapper<T> wrapper = getEntityWrapper();
        wrapper.rows.removeIf(item -> getId(item) == id);
        saveEntityWrapper(wrapper);
    }

    public List<T> filterByField(String fieldPath, Object value) { // -> Takes object cause anon data type
        List<T> filtered = new ArrayList<>();
        for (T item : getAll()) {
            try {
                Object current = item;
                for (String part : fieldPath.split("\\.")) {
                    Field field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    current = field.get(current);
                    if (current == null) break;
                }
                if (current != null && value.equals(current)) {
                    filtered.add(item);
                }
            } catch (Exception ignored) { }
        }
        return filtered;
    }

    // -- ENUMS IMPLEMENTATION -- //

    public void addDynamicValue(String key, String value) {
        updateDynamicValues(key, value, true);
    }

    public void removeDynamicValue(String key, String value) {
        updateDynamicValues(key, value, false);
    }

    public List<String> getDynamicValuesByKey(String key) {
        try {
            Field field = DynamicValues.class.getDeclaredField(key);
            field.setAccessible(true);
            return (List<String>) field.get(getEntityWrapper().dynamic_values);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void updateDynamicValues(String key, String value, boolean add) {
        EntityWrapper<T> wrapper = getEntityWrapper();
        try {
            Field field = DynamicValues.class.getDeclaredField(key);
            field.setAccessible(true);
            List<String> list = (List<String>) field.get(wrapper.dynamic_values);
            if (add) {
                if (!list.contains(value)) list.add(value);
            } else {
                list.remove(value);
            }
            saveEntityWrapper(wrapper);
        } catch (Exception ignored) { }
    }
}
