package com.example.sariapp.utils.db.pocketbase.PBTypes;

public enum PBCollection {
    PRODUCTS("products"),
    USERS("users"),
    STORES("stores"),
    STAFFS("staffs");

    private final String name;

    PBCollection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
