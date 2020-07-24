package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;


public class Diagnosis implements Parcelable {
    private Issue Issue;
    private List<Specialisation> Specialisation;

    public Diagnosis(com.example.symptoms.Issue issue, List<com.example.symptoms.Specialisation> specialisation) {
        Issue = issue;
        Specialisation = specialisation;
    }

    public Diagnosis() {
    }

    public com.example.symptoms.Issue getIssue() {
        return Issue;
    }

    public void setIssue(com.example.symptoms.Issue issue) {
        Issue = issue;
    }

    public List<com.example.symptoms.Specialisation> getSpecialisation() {
        return Specialisation;
    }

    public void setSpecialisation(List<com.example.symptoms.Specialisation> specialisation) {
        Specialisation = specialisation;
    }

    protected Diagnosis(Parcel in) {
        Issue = in.readParcelable(getClass().getClassLoader());
        Specialisation = in.readArrayList(Specialisation.class.getClassLoader());
    }

    public static final Creator<Diagnosis> CREATOR = new Creator<Diagnosis>() {
        @Override
        public Diagnosis createFromParcel(Parcel in) {
            return new Diagnosis(in);
        }

        @Override
        public Diagnosis[] newArray(int size) {
            return new Diagnosis[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(Issue, flags);
        dest.writeList(Specialisation);
    }

}
