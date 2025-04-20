package com.example.sariapp.models;

import com.example.sariapp.utils.db.pocketbase.PBTypes.PBField;

public class Products {

    @PBField("id")
    private String id;

    @PBField("name")
    private String name;

    @PBField("description")
    private String description;

    @PBField("price_per_qty")
    private double pricePerQty;

    @PBField("avail_qty")
    private int availQty;

    @PBField("sold_qty")
    private int soldQty;

    @PBField("store")
    private Stores store;

    @PBField("created_by")
    private String createdBy;

    @PBField("created")
    private String created;

    @PBField("updated")
    private String updated;

    // Static field name constants
    public static class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String PRICE_PER_QTY = "price_per_qty";
        public static final String AVAIL_QTY = "avail_qty";
        public static final String SOLD_QTY = "sold_qty";
        public static final String STORE = "store";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED = "created";
        public static final String UPDATED = "updated";
    }

    // Constructors
    public Products() {}

    // Fluent Setters
    public Products setName(String name) {
        this.name = name;
        return this;
    }

    public Products setDescription(String description) {
        this.description = description;
        return this;
    }

    public Products setPricePerQty(double pricePerQty) {
        this.pricePerQty = pricePerQty;
        return this;
    }

    public Products setAvailQty(int availQty) {
        this.availQty = availQty;
        return this;
    }

    public Products setSoldQty(int soldQty) {
        this.soldQty = soldQty;
        return this;
    }

    public Products setStore(Stores store) {
        this.store = store;
        return this;
    }

    public Products setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Products setCreated(String created) {
        this.created = created;
        return this;
    }

    public Products setUpdated(String updated) {
        this.updated = updated;
        return this;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPricePerQty() {
        return pricePerQty;
    }

    public int getAvailQty() {
        return availQty;
    }

    public int getSoldQty() {
        return soldQty;
    }

    public Stores getStore() {
        return store;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }
}
