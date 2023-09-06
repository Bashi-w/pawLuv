package com.example.pawluv;

public class DataClassSchedule {
    private String name;
    private String date;
    private String startTime;
    private String endTime;
    private String currentUser;
    private String petID;
    private String scheduleID;
    private String status;

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getScheduleID() {
        return scheduleID;
    }

    public String getPetID() {
        return petID;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataClassSchedule(String name, String date, String startTime, String endTime, String currentUser, String petID, String scheduleID, String status) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.currentUser = currentUser;
        this.petID = petID;
        this.scheduleID = scheduleID;
        this.status = status;
    }

    public DataClassSchedule(){

    }
}
