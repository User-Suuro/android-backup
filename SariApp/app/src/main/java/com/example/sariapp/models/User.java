package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

public class User {

    @PBField("username")
    private String username;
    @PBField("email")
    private String email;
    @PBField("password")
    private String password;
    @PBField("passwordConfirm")
    private String passwordConfirm;

    // Private constructor to be used by Builder
    private User(Builder builder) {
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
        public User build() {
            return new User(this);
        }
    }


}
