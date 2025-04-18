package com.example.sariapp.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("/assets")
            .filename("env")
            .load();
    public static final String PB_BASE_URL = dotenv.get("PB_BASE_URL");
    public static final String PB_ADMIN_EMAIL = dotenv.get("PB_ADMIN_EMAIL");
    public static final String PB_ADMIN_PASSWORD = dotenv.get("PB_ADMIN_PASSWORD");

}