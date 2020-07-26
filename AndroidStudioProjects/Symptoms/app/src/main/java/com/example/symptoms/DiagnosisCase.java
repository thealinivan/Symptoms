package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;

public class DiagnosisCase implements Parcelable {
    private String DiagnosisSymptomsID;
    private String DiagnosisSymptoms;
    private String UserEmail;


    public DiagnosisCase(String diagnosisSymptomsID, String diagnosisSymptoms, String userEmail) {
        DiagnosisSymptomsID = diagnosisSymptomsID;
        DiagnosisSymptoms = diagnosisSymptoms;
        UserEmail = userEmail;
    }

    public DiagnosisCase() {}

    public String getDiagnosisSymptomsID() {
        return DiagnosisSymptomsID;
    }

    public void setDiagnosisSymptomsID(String diagnosisSymptomsID) {
        DiagnosisSymptomsID = diagnosisSymptomsID;
    }

    public String getDiagnosisSymptoms() {
        return DiagnosisSymptoms;
    }

    public void setDiagnosisSymptoms(String diagnosisSymptoms) {
        DiagnosisSymptoms = diagnosisSymptoms;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }


    protected DiagnosisCase(Parcel in) {
        DiagnosisSymptomsID = in.readString();
        DiagnosisSymptoms = in.readString();
        UserEmail = in.readString();
    }
    public static final Creator<DiagnosisCase> CREATOR = new Creator<DiagnosisCase>() {
        @Override
        public DiagnosisCase createFromParcel(Parcel in) {
            return new DiagnosisCase(in);
        }

        @Override
        public DiagnosisCase[] newArray(int size) {
            return new DiagnosisCase[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DiagnosisSymptomsID);
        dest.writeString(DiagnosisSymptoms);
        dest.writeString(UserEmail);
    }
}




