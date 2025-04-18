package com.example.sariapp.utils.db.local.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Sqlite<T> extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;
    private final String table_name;
    private final Class<T> model;
    private List<String> tableColumns; // Cached column names
    private final Gson gson = new Gson();

    public Sqlite(Context context, Class<T> model) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.model = model;
        this.table_name = model.getSimpleName();
        this.tableColumns = new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = generateCreateTableQuery(model, table_name);
        db.execSQL(createTableQuery);
        tableColumns = getTableColumnNames(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);
    }

    private String generateCreateTableQuery(Class<?> modelClass, String tableName) {
        StringBuilder query = new StringBuilder("CREATE TABLE " + tableName + " (");
        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                query.append(column.name()).append(" ").append(column.type()).append(", ");
            } else if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                query.append(fk.columnName()).append(" INTEGER, ");
                String referencedTable = fk.referencedTable().getSimpleName().toLowerCase();
                query.append("FOREIGN KEY(")
                        .append(fk.columnName())
                        .append(") REFERENCES ")
                        .append(referencedTable)
                        .append("(")
                        .append(fk.referencedColumn())
                        .append("), ");
            }
        }
        if (query.lastIndexOf(", ") == query.length() - 2) {
            query.setLength(query.length() - 2);
        }
        query.append(");");
        return query.toString();
    }

    private List<String> getTableColumnNames(SQLiteDatabase db) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table_name + ")", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndex("name");
                if (index != -1) {
                    columns.add(cursor.getString(index));
                }
            }
            cursor.close();
        }
        return columns;
    }

    private ContentValues convertModelToContentValues(Object obj) {
        ContentValues values = new ContentValues();
        if (obj instanceof org.json.JSONObject) {
            org.json.JSONObject jsonObject = (org.json.JSONObject) obj;
            try {
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof Integer) values.put(key, (Integer) value);
                    else if (value instanceof Long) values.put(key, (Long) value);
                    else if (value instanceof Float) values.put(key, (Float) value);
                    else if (value instanceof Double) values.put(key, (Double) value);
                    else if (value instanceof String) values.put(key, (String) value);
                    else if (value instanceof Boolean) values.put(key, (Boolean) value ? 1 : 0);
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        } else {
            Class<?> clazz = obj.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String columnName = field.isAnnotationPresent(Column.class)
                        ? field.getAnnotation(Column.class).name()
                        : field.getName();
                try {
                    Object value = field.get(obj);
                    if ("id".equalsIgnoreCase(columnName) && (value instanceof Number) && ((Number) value).longValue() == 0) {
                        continue;
                    }
                    if (value == null) continue;
                    if (value instanceof List) {
                        values.put(columnName, gson.toJson(value));
                    } else if (value instanceof Boolean) {
                        values.put(columnName, ((Boolean) value) ? 1 : 0);
                    } else if (value instanceof Integer) values.put(columnName, (Integer) value);
                    else if (value instanceof Long) values.put(columnName, (Long) value);
                    else if (value instanceof Double) values.put(columnName, (Double) value);
                    else if (value instanceof String) values.put(columnName, (String) value);
                    else values.put(columnName, value.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    public long insert(SQLiteDatabase db, T modelObj) {
        return db.insert(table_name, null, convertModelToContentValues(modelObj));
    }

    public int update(SQLiteDatabase db, T modelObj) {
        return db.update(table_name, convertModelToContentValues(modelObj), "id=?", new String[]{String.valueOf(getIdFromModel(modelObj))});
    }

    public int delete(SQLiteDatabase db, long id) {
        return db.delete(table_name, "id=?", new String[]{String.valueOf(id)});
    }

    public List<T> getAll() {
        List<T> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                T instance = createInstanceFromCursor(cursor, db);
                if (instance != null) {
                    dataList.add(instance);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return dataList;
    }

    private List<T> getAll(SQLiteDatabase db) {
        List<T> dataList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                T instance = createInstanceFromCursor(cursor, db);
                if (instance != null) {
                    dataList.add(instance);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return dataList;
    }

    private long getIdFromModel(T modelObj) {
        try {
            Field idField = model.getDeclaredField("id");
            idField.setAccessible(true);
            return ((Number) idField.get(modelObj)).longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private T createInstanceFromCursor(Cursor cursor, SQLiteDatabase db) {
        try {
            if (tableColumns.isEmpty()) {
                tableColumns = getTableColumnNames(db);
            }
            T instance = model.getDeclaredConstructor().newInstance();
            for (String colName : tableColumns) {
                int colIndex = cursor.getColumnIndex(colName);
                if (colIndex == -1) continue;
                Field field;
                try {
                    field = model.getDeclaredField(colName);
                } catch (NoSuchFieldException e) {
                    continue;
                }
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Object value = null;
                if (fieldType == int.class || fieldType == Integer.class) {
                    value = cursor.getInt(colIndex);
                } else if (fieldType == long.class || fieldType == Long.class) {
                    value = cursor.getLong(colIndex);
                } else if (fieldType == double.class || fieldType == Double.class) {
                    value = cursor.getDouble(colIndex);
                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                    value = (cursor.getInt(colIndex) == 1);
                } else if (fieldType == String.class) {
                    value = cursor.getString(colIndex);
                } else if (List.class.isAssignableFrom(fieldType)) {
                    value = gson.fromJson(cursor.getString(colIndex), new TypeToken<List<String>>() {}.getType());
                } else {
                    value = cursor.getString(colIndex);
                }
                field.set(instance, value);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getUniqueValues(String columnName) {
        List<String> uniqueValues = new ArrayList<>();
        if (!tableColumns.contains(columnName)) {
            return uniqueValues;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + columnName + " FROM " + table_name, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndex(columnName);
                if (index != -1) {
                    uniqueValues.add(cursor.getString(index));
                }
            }
            cursor.close();
        }
        db.close();
        return uniqueValues;
    }

    public void loadStaticData(List<T> staticData) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Map<String, T> staticMap = new HashMap<>();
            for (T item : staticData) {
                String key = getKey(item);
                if (key != null) staticMap.put(key, item);
            }
            List<T> existingData = getAll(db);
            Map<String, T> existingMap = new HashMap<>();
            for (T item : existingData) {
                String key = getKey(item);
                if (key != null) existingMap.put(key, item);
            }
            for (Map.Entry<String, T> entry : staticMap.entrySet()) {
                String key = entry.getKey();
                T staticItem = entry.getValue();
                if (existingMap.containsKey(key)) {
                    T dbItem = existingMap.get(key);
                    if (isChanged(dbItem, staticItem)) update(db, staticItem);
                } else insert(db, staticItem);
            }
            for (Map.Entry<String, T> entry : existingMap.entrySet()) {
                if (!staticMap.containsKey(entry.getKey())) {
                    delete(db, getIdFromModel(entry.getValue()));
                }
            }
        } finally {
            db.close();
        }
    }

    private String getKey(T modelObj) {
        try {
            Field field = model.getDeclaredField("name");
            field.setAccessible(true);
            Object key = field.get(modelObj);
            return key != null ? key.toString() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isChanged(T a, T b) {
        return !convertModelToContentValues(a).toString().equals(convertModelToContentValues(b).toString());
    }
}
