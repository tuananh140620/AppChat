package com.example.project.model;

import java.util.List;

public class User {
    private String id;
    private String username;
    private int avatar;
    private String email;
    private String phone;
    private String imageURL;

    public User() {
    }

    public User(String id, String username, int avatar, String email, String phone, String imageURL) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.email = email;
        this.phone = phone;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
