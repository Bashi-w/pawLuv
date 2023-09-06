package com.example.pawluv;

public class DataClassJournal {
    private String entry;
    private String date;
    private String currentUser;
    private String petID;
    private String entryID;

    public String getEntry() {
        return entry;
    }

    public String getDate() {
        return date;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getPetID() {
        return petID;
    }

    public String getEntryID() {
        return entryID;
    }

    public DataClassJournal(String entry, String date, String currentUser, String petID, String entryID) {
        this.entry = entry;
        this.date = date;
        this.currentUser = currentUser;
        this.petID = petID;
        this.entryID = entryID;
    }

    public DataClassJournal(){

    }
}
