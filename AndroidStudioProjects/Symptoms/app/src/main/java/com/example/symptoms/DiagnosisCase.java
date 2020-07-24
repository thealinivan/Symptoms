package com.example.symptoms;

public class DiagnosisCase {
    private String DiagnosisSymptoms;
    private String UserEmail;

    public DiagnosisCase(String diagnosisSymptoms, String userEmail) {
        DiagnosisSymptoms = diagnosisSymptoms;
        UserEmail = userEmail;
    }

    public DiagnosisCase(){}

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
}




