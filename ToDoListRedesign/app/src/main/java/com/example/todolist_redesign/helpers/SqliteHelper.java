package com.example.todolist_redesign.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SqliteHelper<T> extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final Gson gson = new Gson();

    private final String DATABASE_NAME;
    private final Class<T> model;
    private final String TABLE_NAME;
    private final String CREATE_TABLE_QUERY;

    public SqliteHelper(Context context, Class<T> model) {
        super(context, getModelDBName(model), null, DATABASE_VERSION);
        this.model = model;
        this.DATABASE_NAME = getModelDBName(model);
        this.TABLE_NAME = getModelEntName(model);
        this.CREATE_TABLE_QUERY = initTableQuery();
    }

    // -- LIFECYCLES -- //

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // -- UTILS -- //

    private static <T> String getModelDBName(Class<T> model) {
        try {
            Field field = model.getField("DATABASE_NAME");
            return (String) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return "app.db"; // default db
        }
    }

    private static <T> String getModelEntName(Class<T> model) {
        try {
            Field field = model.getField("ENTITY_NAME");
            return (String) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException("Model " + model.getSimpleName() +
                    " must define a public static final String ENTITY_NAME", e);
        }
    }

    private String initTableQuery() {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT");
        for (Field field : model.getDeclaredFields()) {
            if (!field.getName().equals("id")) {
                query.append(", ").append(field.getName()).append(" TEXT");
            }
        }
        query.append(")");
        return query.toString();
    }

    // -- DATA CONVERSION METHODS -- //

    private ContentValues modelToContentDB(T modelInstance) {
        ContentValues values = new ContentValues();
        for (Field field : model.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if ("id".equals(field.getName()))
                    continue;
                Object value = field.get(modelInstance);
                if (value != null) {
                    if (isSimpleType(field.getType())) {
                        values.put(field.getName(), value.toString());
                    } else {
                        values.put(field.getName(), gson.toJson(value));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    private T cursorToModel(Cursor cursor) {
        try {
            T instance = model.newInstance();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                Field field = model.getDeclaredField(columnName);
                field.setAccessible(true);
                String storedValue = cursor.getString(i);
                if (storedValue == null)
                    continue;
                // Use Gson to convert the stored string into the proper field type.
                field.set(instance, gson.fromJson(storedValue, field.getType()));
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isSimpleType(Class<?> cls) {
        return cls.equals(String.class) ||
                cls.isPrimitive() ||
                Number.class.isAssignableFrom(cls) ||
                cls.equals(Boolean.class);
    }

    private long extractId(T modelInstance) {
        try {
            Field field = model.getDeclaredField("id");
            field.setAccessible(true);
            Object value = field.get(modelInstance);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                throw new IllegalStateException("The id field is not a number");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not extract id from model", e);
        }
    }

    // -- CRUD METHODS -- //

    public long insert(T modelInstance) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, modelToContentDB(modelInstance));
        db.close();
        return id;
    }

    public int update(long id, T modelInstance) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_NAME, modelToContentDB(modelInstance), "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int update(T modelInstance) {
        return update(extractId(modelInstance), modelInstance);
    }

    public int delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int delete(T modelInstance) {
        return delete(extractId(modelInstance));
    }

    public T get(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = ?",
                new String[]{String.valueOf(id)});
        T instance = null;
        if (cursor.moveToFirst()) {
            instance = cursorToModel(cursor);
        }
        cursor.close();
        db.close();
        return instance;
    }

    public T get(T modelInstance) {
        return get(extractId(modelInstance));
    }

    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToModel(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
