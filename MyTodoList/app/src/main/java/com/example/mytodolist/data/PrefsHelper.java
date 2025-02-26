package com.example.mytodolist.data;

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

public class PrefsHelper {

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private String entName;           // Key for storing the list (same as the entity name)
    private String autoIncrementKey;  // Key for auto-incrementing IDs

    public PrefsHelper(Context context, String entity) {
        // Use the provided entity as the SharedPreferences name
        this.sharedPreferences = context.getSharedPreferences(entity, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.entName = entity;
        this.autoIncrementKey = entity + "_auto_increment_id";
    }

    // Retrieve all data for the given model class using the key same as the entity name.
    public <T> List<T> getAllData(Class<T> cl) {
        String json = sharedPreferences.getString(entName, null);
        if (json == null) return new ArrayList<>();
        Type listType = TypeToken.getParameterized(List.class, cl).getType();
        return gson.fromJson(json, listType);
    }

    // Save or update a model instance (assumed to have a field "id")
    public <T> void saveData(T model) {
        try {

            Field idField = model.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(model);
            long newId;

            if (idValue == null || (idValue instanceof Number && ((Number) idValue).longValue() == 0)) {

                long lastId = sharedPreferences.getLong(autoIncrementKey, 0);
                newId = lastId + 1;

                try {
                    Method setIdMethod = model.getClass().getDeclaredMethod("setId", long.class);
                    setIdMethod.invoke(model, newId);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    idField.set(model, newId); // Fallback if setter isn't available
                }
                sharedPreferences.edit().putLong(autoIncrementKey, newId).apply();
            } else {
                newId = ((Number) idValue).longValue();
            }


            List<T> taskList = getAllData((Class<T>) model.getClass());

            // Remove any existing task with the same id
            taskList.removeIf(task -> {
                try {
                    Field taskIdField = task.getClass().getDeclaredField("id");
                    taskIdField.setAccessible(true);
                    return ((Number) taskIdField.get(task)).longValue() == newId;
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    return false;
                }
            });

            // Add the new or updated task
            taskList.add(model);
            String json = gson.toJson(taskList);
            sharedPreferences.edit().putString(entName, json).apply();

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Retrieve a single task by id
    public <T> T getTask(Class<T> cl, long id) {
        List<T> taskList = getAllData(cl);
        for (T task : taskList) {
            try {
                Field idField = task.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                if (((Number) idField.get(task)).longValue() == id) {
                    return task;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    // Remove a task by id from the list stored under the entity key.
    public <T> void remove(Class<T> cl, long id) {
        List<T> taskList = getAllData(cl);
        taskList.removeIf(task -> {
            try {
                Field idField = task.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                return ((Number) idField.get(task)).longValue() == id;
            } catch (IllegalAccessException | NoSuchFieldException e) {
                return false;
            }
        });
        String json = gson.toJson(taskList);
        sharedPreferences.edit().putString(entName, json).apply();
    }
}