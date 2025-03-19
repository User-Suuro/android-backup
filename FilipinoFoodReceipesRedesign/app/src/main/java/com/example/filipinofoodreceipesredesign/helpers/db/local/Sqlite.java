package com.example.filipinofoodreceipesredesign.helpers.db.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Sqlite<T> extends SQLiteOpenHelper {
    private static final String DEFAULT_DATABASE_NAME = "app_database.db"; // Fixed DB name
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATA = "data";

    private final Gson gson = new Gson();
    private final String tableName;
    private final Context context;
    private final Class<T> modelClass;

    public Sqlite(Context context, Class<T> modelClass) {
        this(context, modelClass, DEFAULT_DATABASE_NAME);
    }

    public Sqlite(Context context, Class<T> modelClass, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.context = context;
        this.modelClass = modelClass;
        // The table name is derived from the model class name (in lowercase)
        this.tableName = modelClass.getSimpleName().toLowerCase();

        createTableIfNotExists();
        checkAndUpdateSchema();
    }

    public static class Builder<T> {
        private final Context context;
        private final Class<T> modelClass;
        private String databaseName = DEFAULT_DATABASE_NAME; // Default DB Name

        public Builder(Context context, Class<T> modelClass) {
            this.context = context;
            this.modelClass = modelClass;
        }

        public Builder<T> setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Sqlite<T> build() {
            return new Sqlite<>(context, modelClass, databaseName);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        String createQuery = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATA + " TEXT NOT NULL);";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        checkAndUpdateSchema();
    }

    // INSERT: Serializes object into JSON and stores it.
    // This method works for any model, including those with nested objects.
    public long insert(T object) {
        createTableIfNotExists();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Gson automatically converts nested objects (whether a single nested object or a list) to JSON.
        values.put(COLUMN_DATA, gson.toJson(object));

        long id = db.insert(tableName, null, values);
        db.close();
        return id;
    }

    // GET ALL OBJECTS: Retrieves all records from the table and deserializes them.
    public List<T> getAll(Class<T> modelClass) {
        createTableIfNotExists();
        List<T> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DATA + " FROM " + tableName, null);

        if (cursor == null) {
            Log.e("Sqlite", "ERROR: Cursor is null for table: " + tableName);
            return dataList;
        }

        try {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_DATA);
                if (columnIndex == -1) {
                    Log.e("Sqlite", "ERROR: Column '" + COLUMN_DATA + "' not found in table: " + tableName);
                    return dataList;
                }
                do {
                    String json = cursor.getString(columnIndex);
                    if (json == null || json.trim().isEmpty()) {
                        Log.w("Sqlite", "WARNING: Found empty JSON in table: " + tableName);
                        continue;
                    }
                    try {
                        T object = gson.fromJson(json, modelClass);
                        dataList.add(object);
                    } catch (JsonSyntaxException e) {
                        Log.e("Sqlite", "ERROR: JSON parsing failed for data: " + json, e);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return dataList;
    }

    // GET SINGLE OBJECT BY ID:
    // This implementation loops through all objects and uses reflection to compare the 'id' field.
    // For models with nested objects, ensure the nested data is properly handled by Gson.
    public T getById(Class<T> modelClass, int id) {
        List<T> allData = getAll(modelClass);
        for (T object : allData) {
            try {
                Field idField = modelClass.getDeclaredField("id");
                idField.setAccessible(true);
                int objectId = idField.getInt(object);
                if (objectId == id) {
                    return object;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.e("Sqlite", "ERROR: Unable to access ID field in " + modelClass.getSimpleName(), e);
                return null;
            }
        }
        return null; // Return null if no matching object is found.
    }

    // UPDATE OBJECT: Serializes the updated object and updates the record with the given id.
    public int update(int id, T object) {
        createTableIfNotExists();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, gson.toJson(object));

        int result = db.update(tableName, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // DELETE OBJECT: Deletes the record with the given id.
    public void delete(int id) {
        createTableIfNotExists();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // --- AUTOMATIC SCHEMA UPDATE FUNCTIONS ---

    // Save the current model field names to SharedPreferences.
    private void saveModelSchema(Set<String> fields) {
        SharedPreferences prefs = context.getSharedPreferences("schema_prefs", Context.MODE_PRIVATE);
        prefs.edit().putStringSet(tableName, fields).apply();
    }

    // Retrieve the last known schema (set of field names) from SharedPreferences.
    private Set<String> getLastKnownSchema() {
        SharedPreferences prefs = context.getSharedPreferences("schema_prefs", Context.MODE_PRIVATE);
        return prefs.getStringSet(tableName, new HashSet<>());
    }

    // Get the current model's field names using reflection.
    private Set<String> getCurrentModelFields() {
        Set<String> fields = new HashSet<>();
        for (Field field : modelClass.getDeclaredFields()) {
            fields.add(field.getName());
        }
        return fields;
    }

    // Check and update the table schema by adding new columns for any new model fields.
    private void checkAndUpdateSchema() {
        SQLiteDatabase db = this.getWritableDatabase();
        Set<String> lastSchema = getLastKnownSchema();
        Set<String> currentSchema = getCurrentModelFields();
        for (String field : currentSchema) {
            if (!lastSchema.contains(field)) {
                // Add a new column for the field. Note: Even nested objects are stored as JSON in COLUMN_DATA,
                // so this is just a fallback if additional columns are needed.\n
                String alterQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + field + " TEXT;";
                db.execSQL(alterQuery);
                Log.d("Sqlite", "Added new column: " + field);
            }
        }
        saveModelSchema(currentSchema);
        db.close();
    }

    // STATIC DATA SYNCHRONIZATION:
    // This method syncs a given list of static data with the database.
    // If insertOnce is true, static data is only inserted if not already present; otherwise, it is updated and/or deleted as needed.
    public void loadStaticData(List<T> staticData, boolean insertOnce) {
        List<T> existingData = getAll(modelClass);
        // Convert existing and static data to maps keyed by the 'id' field (using reflection).
        Map<Integer, T> existingMap = existingData.stream()
                .collect(Collectors.toMap(this::getIdValue, item -> item));
        Map<Integer, T> staticMap = staticData.stream()
                .collect(Collectors.toMap(this::getIdValue, item -> item));
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // INSERT or UPDATE static data based on the insertOnce flag.
            for (Map.Entry<Integer, T> entry : staticMap.entrySet()) {
                int id = entry.getKey();
                T staticItem = entry.getValue();
                if (!existingMap.containsKey(id)) {
                    insert(staticItem);
                } else if (!insertOnce && !existingMap.get(id).equals(staticItem)) {
                    update(id, staticItem);
                }
            }
            // DELETE records that are not in static data if insertOnce is false.
            if (!insertOnce) {
                Set<Integer> staticIds = staticMap.keySet();
                for (Integer existingId : existingMap.keySet()) {
                    if (!staticIds.contains(existingId)) {
                        delete(existingId);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private int getIdValue(T object) {
        try {
            Field idField = modelClass.getDeclaredField("id");
            idField.setAccessible(true);
            return (int) idField.get(object);
        } catch (Exception e) {
            throw new RuntimeException("ERROR: ID field not found!", e);
        }
    }
}
