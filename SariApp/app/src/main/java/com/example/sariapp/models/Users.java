package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

public class Users {
    @PBField("id")
    private String id;
    @PBField("username")
    private String username;
    @PBField("email")
    private String email;
    @PBField("password")
    private String password;
    @PBField("passwordConfirm")
    private String passwordConfirm;

    // Private constructor to be used by Builder
    private Users(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.passwordConfirm = builder.passwordConfirm;
    }


    // Getters for each field
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
    public String getID() {return id;}


    // Static Builder class
    public static class Builder {
        private String username;
        private String email;
        private String password;
        private String passwordConfirm;

        // Set username
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        // Set email
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        // Set password
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder confirmPassword(String confirmPassword) {
            this.passwordConfirm = confirmPassword;
            return this;
        }

        // Build the User object
        public Users build() {
            return new Users(this);
        }
    }


}
