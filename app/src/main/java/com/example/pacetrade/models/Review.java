package com.example.pacetrade.models;

public class Review {
    String comment;
    String by;
    String itemId;
    String reviewerFullName;

    public Review() {

    }

    public Review(String review, String reviewBy, String id, String reviewerName) {
        comment = review;
        by = reviewBy;
        itemId = id;
        reviewerFullName = reviewerName;
    }

    public String getReviewerFullName() {
        return reviewerFullName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getComment() {
        return comment;
    }

    public String getBy() {
        return by;
    }
}
