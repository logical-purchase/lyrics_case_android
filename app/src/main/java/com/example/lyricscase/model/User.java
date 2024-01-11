package com.example.lyricscase.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    public int userId;

    @SerializedName("username")
    public String username;

    @SerializedName("user_email")
    public String userEmail;

    @SerializedName("user_password")
    public String userPassword;

    @SerializedName("user_image")
    public String userImage;

    @SerializedName("user_bio")
    public String userBio;

    @SerializedName("user_points")
    public int userPoints;

    @SerializedName("id_role")
    public int userRole;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }
}
