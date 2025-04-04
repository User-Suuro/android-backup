package com.example.sariapp.helpers.db.local;

import android.app.Application;

public class Runtime extends Application {
    private static volatile Runtime instance;
    public Runtime() {

    }
    public static Runtime getInstance() {
        if (instance == null) {
            synchronized (Runtime.class) {
                if (instance == null) {
                    instance = new Runtime();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}