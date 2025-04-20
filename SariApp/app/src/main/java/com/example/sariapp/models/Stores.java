package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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

    @PBField("establishment")
    private String establishment;

    @PBField("created")
    private String created;

    @PBField("updated")
    private String updated;

    public Stores() {}

    // === Fluent Setters ===
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

    public Stores setEstablishment(String establishment) {
        this.establishment = establishment;
        return this;
    }

    public Stores setCreated(String created) {
        this.created = created;
        return this;
    }

    public Stores setUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    // === Getters ===
    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public double getLocationX() { return locationX; }
    public double getLocationY() { return locationY; }
    public double getLocationZ() { return locationZ; }
    public String getZipcode() { return zipcode; }
    public String getEstablishment() { return establishment; }
    public String getCreated() { return created; }
    public String getUpdated() { return updated; }

    // === Builder ===
    public static class Builder {
        private final Stores store;

        public Builder() {
            store = new Stores();
        }

        public Builder name(String name) {
            store.setName(name);
            return this;
        }

        public Builder owner(String owner) {
            store.setOwner(owner);
            return this;
        }

        public Builder description(String description) {
            store.setDescription(description);
            return this;
        }

        public Builder address(String address) {
            store.setAddress(address);
            return this;
        }

        public Builder location(double x, double y, double z) {
            store.setLocationX(x).setLocationY(y).setLocationZ(z);
            return this;
        }

        public Builder zipcode(String zipcode) {
            store.setZipcode(zipcode);
            return this;
        }

        public Builder establishment(String establishment) {
            store.setEstablishment(establishment);
            return this;
        }

        public Builder createdNow() {
            store.setCreated(now());
            return this;
        }

        public Stores build() {
            return store;
        }
    }

    public static String now() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public String toString() {
        return "Stores{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", address='" + address + '\'' +
                ", establishment='" + establishment + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }
}
