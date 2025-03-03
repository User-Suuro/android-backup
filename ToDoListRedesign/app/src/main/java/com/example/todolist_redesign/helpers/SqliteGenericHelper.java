package com.example.todolist_redesign.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SqliteGenericHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private final String DATABASE_NAME;

    public SqliteGenericHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.DATABASE_NAME = databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This helper is for inspection purposes only.
        // No tables are created here.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrade logic required for inspection.
    }

    public List<String> getAllTables() {
        List<String> tables = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
                String tableName = cursor.getString(0);
                tables.add(tableName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tables;
    }
}
