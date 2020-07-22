package com.example.symptoms;

public class User {
    //object parameters
    private String gender, email;
    int YOB;

    public User(String gender, int YOB, String email) {
        this.gender = gender;
        this.YOB = YOB;
        this.email = email;
    }

    public User(){};

    public String getGender() {
        return gender;
    }

    public int getYOB() {
        return YOB;
    }

    public String getEmail() {
        return email;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setYOB(int YOB) {
        this.YOB = YOB;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
