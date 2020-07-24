package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;

public class Issue implements Parcelable {
    private int ID;
    private  String Name;
    private double Accuracy;
    private String Icd;
    private String IcdName;
    private String ProfName;
    private int Ranking;

    public Issue(int ID, String name, double accuracy, String icd, String icdName, String profName, int ranking) {
        this.ID = ID;
        Name = name;
        Accuracy = accuracy;
        Icd = icd;
        IcdName = icdName;
        ProfName = profName;
        Ranking = ranking;
    }

    public Issue(){}

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

    public double getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(int accuracy) {
        Accuracy = accuracy;
    }

    public String getIcd() {
        return Icd;
    }

    public void setIcd(String icd) {
        Icd = icd;
    }

    public String getIcdName() {
        return IcdName;
    }

    public void setIcdName(String icdName) {
        IcdName = icdName;
    }

    public String getProfName() {
        return ProfName;
    }

    public void setProfName(String profName) {
        ProfName = profName;
    }

    public int getRanking() {
        return Ranking;
    }

    public void setRanking(int ranking) {
        Ranking = ranking;
    }

    protected Issue(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        Accuracy = in.readDouble();
        Icd = in.readString();
        IcdName = in.readString();
        ProfName = in.readString();
        Ranking = in.readInt();
    }
    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        //Dev: For parceling purposes -  Issue is not array, but single object.
        //Dev: Research this creator function
        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
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
        dest.writeDouble(Accuracy);
        dest.writeString(Icd);
        dest.writeString(IcdName);
        dest.writeString(ProfName);
        dest.writeInt(Ranking);
    }

}
