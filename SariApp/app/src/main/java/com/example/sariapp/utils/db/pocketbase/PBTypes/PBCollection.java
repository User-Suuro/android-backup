package com.example.sariapp.utils.db.pocketbase.PBTypes;

public enum PBCollection {
    PRODUCTS(""),
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
