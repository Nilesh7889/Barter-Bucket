package com.example.pacetrade.models;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String gradYear;
    private String uid;

    public User() {

    }

    public User(String userEmail, String fName, String lName, String userProfilePictureurl, String userGradYear, String userId) {
        this.email = userEmail;
        this.firstName = fName;
        this.lastName = lName;
        this.profilePictureUrl = userProfilePictureurl;
        this.gradYear = userGradYear;
        this.uid = userId;

    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getGradYear() {
        return gradYear;
    }

    public String getUid() {
        return uid;
    }
}
