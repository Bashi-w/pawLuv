package com.example.pawluv;

public class DataClassUser {
    private String userID;
    private String name;
    private String mobile;

    public DataClassUser(String userID, String name, String mobile) {
        this.userID = userID;
        this.name = name;
        this.mobile = mobile;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public DataClassUser(){

    }
}
