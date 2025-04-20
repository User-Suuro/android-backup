package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

public class Users {

    @PBField("id")
    private String id;

    @PBField("password")
    private String password;

    @PBField("passwordConfirm")
    private String passwordConfirm;

    @PBField("tokenKey")
    private String tokenKey;

    @PBField("email")
    private String email;

    @PBField("emailVisibility")
    private boolean emailVisibility;

    @PBField("verified")
    private boolean verified;

    @PBField("name")
    private String name;

    @PBField("avatar")
    private String avatar; // Assuming this is a file path or ID

    @PBField("created")
    private String created;

    @PBField("updated")
    private String updated;

    // Private constructor to be used by Builder
    private Users(Builder builder) {
        this.id = builder.id;
        this.password = builder.password;
        this.passwordConfirm = builder.passwordConfirm;
        this.tokenKey = builder.tokenKey;
        this.email = builder.email;
        this.emailVisibility = builder.emailVisibility;
        this.verified = builder.verified;
        this.name = builder.name;
        this.avatar = builder.avatar;
        this.created = builder.created;
        this.updated = builder.updated;
    }

    // Getters
    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getPasswordConfirm() { return passwordConfirm; }
    public String getTokenKey() { return tokenKey; }
    public String getEmail() { return email; }
    public boolean isEmailVisibility() { return emailVisibility; }
    public boolean isVerified() { return verified; }
    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public String getCreated() { return created; }
    public String getUpdated() { return updated; }

    // Builder class
    public static class Builder {
        private String id;
        private String password;
        private String passwordConfirm;
        private String tokenKey;
        private String email;
        private boolean emailVisibility;
        private boolean verified;
        private String name;
        private String avatar;
        private String created;
        private String updated;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder passwordConfirm(String passwordConfirm) {
            this.passwordConfirm = passwordConfirm;
            return this;
        }

        public Builder tokenKey(String tokenKey) {
            this.tokenKey = tokenKey;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder emailVisibility(boolean emailVisibility) {
            this.emailVisibility = emailVisibility;
            return this;
        }

        public Builder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
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

        public Users build() {
            return new Users(this);
        }
    }

    // Static field names
    public static class Fields {
        public static final String ID = "id";
        public static final String PASSWORD = "password";
        public static final String PASSWORD_CONFIRM = "passwordConfirm";
        public static final String TOKEN_KEY = "tokenKey";
        public static final String EMAIL = "email";
        public static final String EMAIL_VISIBILITY = "emailVisibility";
        public static final String VERIFIED = "verified";
        public static final String NAME = "name";
        public static final String AVATAR = "avatar";
        public static final String CREATED = "created";
        public static final String UPDATED = "updated";
    }
}
