package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

public class Staffs {

    @PBField("id")
    private String id;

    @PBField("user")
    private String user;

    @PBField("store")
    private String store;

    @PBField("is_active")
    private boolean isActive;

    @PBField("created")
    private String created;

    @PBField("updated")
    private String updated;

    // Static field name constants
    public static class Fields {
        public static final String ID = "id";
        public static final String USER = "user";
        public static final String STORE = "store";
        public static final String IS_ACTIVE = "is_active";
        public static final String CREATED = "created";
        public static final String UPDATED = "updated";
    }

    // Private constructor for builder
    private Staffs(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.store = builder.store;
        this.isActive = builder.isActive;
        this.created = builder.created;
        this.updated = builder.updated;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getStore() {
        return store;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    // Builder
    public static class Builder {
        private String id;
        private String user;
        private String store;
        private boolean isActive;
        private String created;
        private String updated;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder store(String store) {
            this.store = store;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder created(String created) {
            this.created = created;
            return this;
        }

        public Builder updated(String updated) {
            this.updated = updated;
            return this;
        }

        public Staffs build() {
            return new Staffs(this);
        }
    }
}
