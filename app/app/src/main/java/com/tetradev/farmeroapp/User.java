package com.tetradev.farmeroapp;

public class User {

    public String username;
    public String email;
    private String userphone;

    public User() {

    }

    public User(String username, String email, String userphone) {
        this.username = username;
        this.email = email;
       this.userphone = userphone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }
}


