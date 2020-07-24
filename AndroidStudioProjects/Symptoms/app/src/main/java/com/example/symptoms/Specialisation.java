package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;

public class Specialisation implements Parcelable {
    private int ID;
    private String Name;
    private int SpecialistID;

    public Specialisation(int ID, String name, int specialistID) {
        this.ID = ID;
        Name = name;
        SpecialistID = specialistID;
    }

    public Specialisation(){}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getSpecialistID() {
        return SpecialistID;
    }

    public void setSpecialistID(int specialistID) {
        SpecialistID = specialistID;
    }

    protected Specialisation(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        SpecialistID = in.readInt();

    }
    public static final Creator<Specialisation> CREATOR = new Creator<Specialisation>() {
        @Override
        public Specialisation createFromParcel(Parcel in) {
            return new Specialisation(in);
        }

        @Override
        public Specialisation[] newArray(int size) {
            return new Specialisation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Name);
        dest.writeInt(SpecialistID);
    }
}
