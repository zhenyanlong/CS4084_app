package com.example.cs4084_app;

import java.math.BigDecimal;
import java.util.Locale;

public class Product {




    private String imageUrl; // Assuming you use drawable resources for images
    private String name;


    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    private String shortDescription;


    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    private String longDescription;
    private float price;
    private String location;

    // Constructor
    // No-argument constructor is required for Firestore's automatic data mapping
    public Product() {
        // Default constructor required for calls to DataSnapshot.toObject(Product.class)
    }
    public Product(String imageResourceId, String name, String short_description,
                   float price, String location) {
        this.imageUrl = imageResourceId;
        this.name = name;
        this.shortDescription = short_description;
        this.price = price;
        this.location = location;
    }

    // Getters

    public String getImageUrl() {
        return imageUrl;
    }
    public String getName() {
        return name;
    }



    public float getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }





    // Setters (if you need to modify the product data)


    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setPrice(float price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
