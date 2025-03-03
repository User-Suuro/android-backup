package com.example.mytodolist.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefsHelper<T> {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String entName;           // Key for storing the list (same as the entity name)
    private String autoIncrementKey;  // Key for auto-incrementing IDs
    private Class<T> model;

    public PrefsHelper(Context context, Class<T> modelClass) {
        this.model = modelClass;
        this.entName = getEntityNameFromModel();
        this.sharedPreferences = context.getSharedPreferences(entName, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.autoIncrementKey = entName + "_auto_increment_id";
    }

    private String getEntityNameFromModel() {
        try {
            Field field = model.getField("ENTITY_NAME");
            return (String) field.get(null); // static field, so pass null as instance
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If not defined, fallback to the model's simple name.
            return model.getSimpleName();
        }
    }

    // Retrieve all data for the given model using the key (entity name)
    public List<T> getAllData() {
        String json = sharedPreferences.getString(entName, null);
        if (json == null) return new ArrayList<>();
        Type listType = TypeToken.getParameterized(List.class, model).getType();
        return gson.fromJson(json, listType);
    }

    // Save or update a model instance (assumed to have a field "id")
    public boolean saveData(T newModel) {
        try {
            // Retrieve the "id" field from the newModel object.
            Field idField = newModel.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(newModel);
            long newId;

            // If the current id is null or zero, we assign a new id.
            if (idValue == null || (idValue instanceof Number && ((Number) idValue).longValue() == 0)) {
                long lastId = sharedPreferences.getLong(autoIncrementKey, 0);
                newId = lastId + 1;

                try {
                    // Try to get and invoke the setId method.
                    Method setIdMethod = newModel.getClass().getDeclaredMethod("setId", long.class);
                    setIdMethod.setAccessible(true); // Make it accessible
                    setIdMethod.invoke(newModel, newId);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    // If setId isn't available, fallback to directly setting the field.
                    idField.set(newModel, newId);
                }

                // Update the auto-increment value in SharedPreferences.
                sharedPreferences.edit().putLong(autoIncrementKey, newId).apply();
            } else {
                newId = ((Number) idValue).longValue();
            }

            // Retrieve the current list of items.
            List<T> itemList = getAllData();

            // Remove any existing item with the same id.
            itemList.removeIf(item -> {
                try {
                    Field itemIdField = item.getClass().getDeclaredField("id");
                    itemIdField.setAccessible(true);
                    return ((Number) itemIdField.get(item)).longValue() == newId;
                } catch (IllegalAccessException | NoSuchFieldException ex) {
                    return false;
                }
            });

            // Add the new or updated model.
            itemList.add(newModel);
            String json = gson.toJson(itemList);
            sharedPreferences.edit().putString(entName, json).apply();

            return true;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Retrieve a single item by id
    public T getTask(long id) {
        List<T> itemList = getAllData();
        for (T item : itemList) {
            try {
                Field idField = item.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                if (((Number) idField.get(item)).longValue() == id) {
                    return item;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Update a model instance by id
    public boolean update(long id, T updatedModel) {
        List<T> itemList = getAllData();
        boolean updated = false;
        for (int i = 0; i < itemList.size(); i++) {
            T item = itemList.get(i);
            try {
                Field idField = item.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                long currentId = ((Number) idField.get(item)).longValue();
                if (currentId == id) {
                    // Ensure updatedModel carries the same id.
                    Field updatedIdField = updatedModel.getClass().getDeclaredField("id");
                    updatedIdField.setAccessible(true);
                    updatedIdField.set(updatedModel, id);
                    itemList.set(i, updatedModel);
                    updated = true;

                    break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (updated) {
            String json = gson.toJson(itemList);
            sharedPreferences.edit().putString(entName, json).apply();
            return true;
        }
        return false;
    }

    // Remove an item by id
    public void remove(long id) {
        List<T> itemList = getAllData();
        itemList.removeIf(item -> {
            try {
                Field idField = item.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                return ((Number) idField.get(item)).longValue() == id;
            } catch (IllegalAccessException | NoSuchFieldException e) {
                return false;
            }
        });
        String json = gson.toJson(itemList);
        sharedPreferences.edit().putString(entName, json).apply();
    }
}