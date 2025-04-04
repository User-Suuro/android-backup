package com.example.sariapp.helpers.db.appwrite;

import android.content.Context;

import io.appwrite.BuildConfig;
import io.appwrite.Client;

public class AppWriteClient {
    private static Client client;

    public static Client getInstance(Context context) {

        return client;
    }
}