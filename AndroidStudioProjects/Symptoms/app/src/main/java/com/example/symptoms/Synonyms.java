package com.example.symptoms;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Synonyms {
    @Expose(serialize = true, deserialize = true)
    private List<String> synonym;


    public Synonyms(){}

    public Synonyms(List<String> synonym) {
        this.synonym = synonym;
    }

    public List<String> getSynonym() {
        return synonym;
    }

    public void setSynonym(List<String> synonym) {
        this.synonym = synonym;
    }
}
