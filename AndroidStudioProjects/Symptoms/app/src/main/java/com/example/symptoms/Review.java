package com.example.symptoms;

public class Review {
    //object parameters
    private String userID, symptomsCaseID, text;

    //constructor
    public Review(String userID, String symptomsCaseID, String text) {
        this.userID = userID;
        this.symptomsCaseID = symptomsCaseID;
        this.text = text;
    }
    //empty constructor for access
    public Review(){};

    //getters and setters
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getSymptomsCaseID() {
        return symptomsCaseID;
    }
    public void setSymptomsCaseID(String symptomsCaseID) {
        this.symptomsCaseID = symptomsCaseID;
    }
    public String getText() {
        return text;
    }
}
