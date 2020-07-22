package com.example.symptoms;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class HealthSymptomsLocationIDs {
    @Expose(serialize = true, deserialize = true)
    private List<String> ID;

    public HealthSymptomsLocationIDs(){}

    public HealthSymptomsLocationIDs(List<String> ID) {
        this.ID = ID;
    }

    public List<String> getID() {
        return ID;
    }

    public void setID(List<String> ID) {
        this.ID = ID;
    }
}
