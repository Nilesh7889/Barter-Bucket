package com.example.pacetrade.models;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class Product {
    private String itemId;
    private String itemName;
    private String itemImageUrl;
    private String mKey;

    private String uploadedById;
    private String uploadedByFullName;
    private Boolean availableForTrade;

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();


    public Product() {

    }

    public Product(String name, String imageUrl, String userId, String uploderName, String id) {
        itemId = id;
        if (name.trim().equals("")) {
            name = "No Name";
        }
        itemName = name;
        itemImageUrl = imageUrl;
        uploadedById = userId;
        uploadedByFullName = uploderName;
        availableForTrade = true;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public String getUploadedById() {
        return uploadedById;
    }

    public String getUploadedByFullName() {
        return uploadedByFullName;
    }

    public void setAvailableForTrade(Boolean availableForTrade) {
        this.availableForTrade = availableForTrade;
    }

    public Boolean getAvailableForTrade() {
        return availableForTrade;
    }

    @Exclude
    public String getkey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;


    }
}

