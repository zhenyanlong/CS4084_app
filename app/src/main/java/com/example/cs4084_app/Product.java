package com.example.cs4084_app;

import java.math.BigDecimal;
import java.util.Locale;

public class Product {
    private int imageResourceId; // Assuming you use drawable resources for images
    private String name;



    private String short_description;



    private String long_description;
    private float price;
    private String location;

    // Constructor
    // No-argument constructor is required for Firestore's automatic data mapping
    public Product() {
        // Default constructor required for calls to DataSnapshot.toObject(Product.class)
    }
    public Product(int imageResourceId, String name, String description,
                   float price, String location) {
        this.imageResourceId = imageResourceId;
        this.name = name;
        this.short_description = description;
        this.price = price;
        this.location = location;
    }

    // Getters
    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getShort_description() {
        return short_description;
    }

    public float getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }

    // Setters (if you need to modify the product data)
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }
    public void setPrice(float price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
