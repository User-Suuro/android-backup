package com.example.sariapp.helpers.db.pocketbase.PBTypes;

public enum PBCollection {
    PRODUCTS("products"),
    USERS("users"),
    STORES("stores");

    private final String name;

    PBCollection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
