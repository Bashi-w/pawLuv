package com.example.pawluv;

public class DataClassPet {

    private String name;
    private String type;
    private String age;
    private String gender;

    private String imageURL;

    private String currentUser;

    private String petID;
    private String weight;
    private String height;
    private String ecName;
    private String ecNum;

    public DataClassPet(String name, String type, String age, String gender, String imageURL, String currentUser, String petID, String weight, String height, String ecName, String ecNum) {
        this.name = name;
        this.type = type;
        this.age = age;
        this.gender = gender;
        this.imageURL = imageURL;
        this.currentUser = currentUser;
        this.petID = petID;
        this.weight = weight;
        this.height = height;
        this.ecName = ecName;
        this.ecNum = ecNum;
    }

    public String getEcName() {
        return ecName;
    }

    public String getEcNum() {
        return ecNum;
    }

    public String getPetID() {
        return petID;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getImageURL() {
        return imageURL;
    }


    public DataClassPet(){

    }
}
