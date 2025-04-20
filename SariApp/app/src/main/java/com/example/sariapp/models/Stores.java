package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

public class Stores {

    @PBField("id")
    private String id;

    @PBField("name")
    private String name;

    @PBField("owner")
    private String owner;

    @PBField("description")
    private String description;

    @PBField("address")
    private String address;

    @PBField("establishment")
    private String establishmentDate;

    @PBField("location_x")
    private double locationX;

    @PBField("location_y")
    private double locationY;

    @PBField("location_z")
    private double locationZ;

    @PBField("zipcode")
    private String zipcode;

    @PBField("created")
    private String created; // ISO 8601

    // Static field name constants
    public static class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String OWNER = "owner";
        public static final String DESCRIPTION = "description";
        public static final String ADDRESS = "address";
        public static final String ESTABLISHMENT = "establishment";
        public static final String LOCATION_X = "location_x";
        public static final String LOCATION_Y = "location_y";
        public static final String LOCATION_Z = "location_z";
        public static final String ZIPCODE = "zipcode";
        public static final String CREATED = "created";
    }

    // Empty constructor for reflection
    public Stores() {}

    // Builder pattern
    public static class Builder {
        private String id;
        private String name;
        private String owner;
        private String description;
        private String address;
        private String establishmentDate;
        private double locationX;
        private double locationY;
        private double locationZ;
        private String zipcode;
        private String created;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setOwner(String ownerID) {
            this.owner = ownerID;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setEstablishmentDate(String establishmentDate){
            this.establishmentDate = establishmentDate;
            return this;
        }

        public Builder setLocationX(double locationX) {
            this.locationX = locationX;
            return this;
        }

        public Builder setLocationY(double locationY) {
            this.locationY = locationY;
            return this;
        }

        public Builder setLocationZ(double locationZ) {
            this.locationZ = locationZ;
            return this;
        }

        public Builder setZipcode(String zipcode) {
            this.zipcode = zipcode;
            return this;
        }

        public Builder setCreated(String created) {
            this.created = created;
            return this;
        }

        public Stores build() {
            Stores store = new Stores();
            store.id = this.id;
            store.name = this.name;
            store.owner = this.owner;
            store.description = this.description;
            store.address = this.address;
            store.locationX = this.locationX;
            store.locationY = this.locationY;
            store.locationZ = this.locationZ;
            store.zipcode = this.zipcode;
            store.created = this.created;
            store.establishmentDate = this.establishmentDate;
            return store;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEstablishmentDate() {
        return establishmentDate;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public double getLocationZ() {
        return locationZ;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getCreated() {
        return created;
    }
}
