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

    @PBField("location_x")
    private double locationX;

    @PBField("location_y")
    private double locationY;

    @PBField("location_z")
    private double locationZ;

    @PBField("zipcode")
    private String zipcode;

    @PBField("created")
    private String created; // ISO 8601 string, e.g., "2025-04-18T10:00:00Z"

    public Stores() {
        // Empty constructor required for reflection/deserialization
    }

    // Builder pattern
    public Stores setName(String name) {
        this.name = name;
        return this;
    }

    public Stores setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public Stores setDescription(String description) {
        this.description = description;
        return this;
    }

    public Stores setAddress(String address) {
        this.address = address;
        return this;
    }

    public Stores setLocationX(double locationX) {
        this.locationX = locationX;
        return this;
    }

    public Stores setLocationY(double locationY) {
        this.locationY = locationY;
        return this;
    }

    public Stores setLocationZ(double locationZ) {
        this.locationZ = locationZ;
        return this;
    }

    public Stores setZipcode(String zipcode) {
        this.zipcode = zipcode;
        return this;
    }

    public Stores setCreated(String created) {
        this.created = created;
        return this;
    }

    // Getters if needed (optional)
    public String getId() { return id; }

    public String getName() { return name; }
    public String getOwner() { return owner; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public double getLocationX() { return locationX; }
    public double getLocationY() { return locationY; }
    public double getLocationZ() { return locationZ; }
    public String getZipcode() { return zipcode; }
    public String getCreated() { return created; }
}
