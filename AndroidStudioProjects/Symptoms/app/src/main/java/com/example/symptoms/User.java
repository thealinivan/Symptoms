package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class User implements Parcelable {
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

    protected User(Parcel in) {
        gender = in.readString();
        YOB = in.readInt();
        email = in.readString();
    }
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gender);
        dest.writeInt(YOB);
        dest.writeString(email);
    }
}
