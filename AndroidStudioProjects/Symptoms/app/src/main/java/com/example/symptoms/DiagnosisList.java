package com.example.symptoms;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisList {
        private List<Diagnosis> diagnosisList;

    public DiagnosisList(List<Diagnosis> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public DiagnosisList(){}

    public List<Diagnosis> getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(List<Diagnosis> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public Diagnosis getSingleDiagnosis (int pos){
        return this.diagnosisList.get(pos);
    }
}
