package com.example.filipinofoodreceipesredesign.helpers.db.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prefs<T> {
    private static final String PREF_NAME = "app_prefs";
    private static final String AUTO_INCREMENT_KEY = "auto_increment_";
    private static final String DYNAMIC_ENUMS_KEY = "dynamic_enums_";

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
    }

    // -- CREATE & UPDATE -- //

    private void saveEntityWrapper(EntityWrapper<T> wrapper) {
        sharedPreferences.edit().putString(entityName, gson.toJson(wrapper)).apply();
    }

    public boolean save(T item) {
        try {
            EntityWrapper<T> wrapper = getEntityWrapper();
            long id = getNextId();
            setId(item, id);
            wrapper.rows.add(item);
            saveEntityWrapper(wrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(T item) {
        try {
            EntityWrapper<T> wrapper = getEntityWrapper();
            long id = getId(item);
            if (id == -1) return false;

            wrapper.rows.removeIf(existingItem -> getId(existingItem) == id);
            wrapper.rows.add(item);
            saveEntityWrapper(wrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // -- READ -- //

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

    public List<T> getAll() {
        return getEntityWrapper().rows;
    }

    public T getById(long id) {
        for (T item : getAll()) {
            if (getId(item) == id) return item;
        }
        return null;
    }

    public List<T> filterByField(String fieldPath, Object value) {
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

    private void setId(T item, long id) {
        try {
            Field idField = item.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.setLong(item, id);
        } catch (Exception ignored) { }
    }

    // -- DELETE -- //

    public boolean remove(long id) {
        try {
            EntityWrapper<T> wrapper = getEntityWrapper();
            if (wrapper.rows.removeIf(existingItem -> getId(existingItem) == id)) {
                saveEntityWrapper(wrapper);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- ENUM MANAGEMENT --- //

    public <E extends Enum<E>> List<String> getEnumValues(Class<E> enumClass) {
        List<String> values = new ArrayList<>();
        for (E value : enumClass.getEnumConstants()) {
            values.add(value.name());
        }
        values.addAll(getDynamicEnumValues(enumClass.getSimpleName())); // Add dynamic values
        return values;
    }

    private List<String> getDynamicEnumValues(String key) {
        Set<String> storedValues = sharedPreferences.getStringSet(DYNAMIC_ENUMS_KEY + key, new HashSet<>());
        return new ArrayList<>(storedValues);
    }

    public void addEnumValue(String key, String value) {
        Set<String> values = new HashSet<>(getDynamicEnumValues(key));
        values.add(value);
        sharedPreferences.edit().putStringSet(DYNAMIC_ENUMS_KEY + key, values).apply();
    }

    public void removeEnumValue(String key, String value) {
        Set<String> values = new HashSet<>(getDynamicEnumValues(key));
        values.remove(value);
        sharedPreferences.edit().putStringSet(DYNAMIC_ENUMS_KEY + key, values).apply();
    }

    // --- STATIC DATA MANAGEMENT --- //

    public boolean loadStaticData(List<T> staticData) {
        try {
            EntityWrapper<T> wrapper = getEntityWrapper();
            List<T> currentData = wrapper.rows;

            // Remove items that are not in staticData
            currentData.removeIf(existingItem -> staticData.stream()
                    .noneMatch(staticItem -> getId(existingItem) == getId(staticItem)));

            // Add missing static items
            for (T item : staticData) {
                if (currentData.stream().noneMatch(existingItem -> getId(existingItem) == getId(item))) {
                    currentData.add(item);
                }
            }

            saveEntityWrapper(wrapper);
            return true; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Failure
        }
    }
}
