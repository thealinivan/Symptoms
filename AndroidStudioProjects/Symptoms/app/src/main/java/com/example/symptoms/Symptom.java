package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;

import java.util.List;


public class Symptom implements Parcelable {
    @Expose(serialize = true, deserialize = true)
    private int ID;

    @Expose(serialize = true, deserialize = true)
    private String Name;

    @Expose(serialize = true, deserialize = true)
    private String HasRedFlag;

    @Expose(serialize = true, deserialize = true)
    private List<String> HealthSymptomLocationIDs;

    @Expose(serialize = true, deserialize = true)
    private String ProfName;

    @Expose(serialize = true, deserialize = true)
    private List<String> Synonyms;

    public Symptom(int ID,
                   String name,
                   String hasRedFlag,
                   List<String> healthSymptomLocationIDs,
                   String profName,
                   List<String> synonyms) {

        this.ID = ID;
        this.Name = name;
        this.HasRedFlag = hasRedFlag;
        this.HealthSymptomLocationIDs = healthSymptomLocationIDs;
        this.ProfName = profName;
        this.Synonyms = synonyms;
    }

    public Symptom(){}

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

    public String getHasRedFlag() {
        return HasRedFlag;
    }

    public void setHasRedFlag(String hasRedFlag) {
        HasRedFlag = hasRedFlag;
    }


    public List<String> getHealthSymptomLocationIDs() {
        return HealthSymptomLocationIDs;
    }

    public void setHealthSymptomLocationIDs(List<String> healthSymptomLocationIDs) {
        HealthSymptomLocationIDs = healthSymptomLocationIDs;
    }

    public String getProfName() {
        return ProfName;
    }

    public void setProfName(String profName) {
        ProfName = profName;
    }

    public List<String> getSynonyms() {
        return Synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        Synonyms = synonyms;
    }

    protected Symptom(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
        HasRedFlag = in.readString();
        HealthSymptomLocationIDs = in.readArrayList(HealthSymptomsLocationIDs.class.getClass().getClassLoader());
        ProfName = in.readString();
        Synonyms = in.readArrayList(Synonyms.class.getClassLoader());
    }
    public static final Creator<Symptom> CREATOR = new Creator<Symptom>() {
        @Override
        public Symptom createFromParcel(Parcel in) {
            return new Symptom(in);
        }

        @Override
        public Symptom[] newArray(int size) {
            return new Symptom[size];
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
        dest.writeString(HasRedFlag);
        dest.writeList(HealthSymptomLocationIDs);
        dest.writeString(ProfName);
        dest.writeList(Synonyms);
    }



}

