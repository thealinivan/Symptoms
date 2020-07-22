package com.example.symptoms;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class BodySubLocation implements Parcelable{
    @Expose(serialize = true, deserialize = true)
    private int ID;

    @Expose(serialize = true, deserialize = true)
    private String Name;

    //for deserialization purposes
    public BodySubLocation(){};

    public BodySubLocation(int ID, String name) {
        this.ID = ID;
        Name = name;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    protected BodySubLocation(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
    }
    public static final Parcelable.Creator<BodySubLocation> CREATOR = new Parcelable.Creator<BodySubLocation>() {
        @Override
        public BodySubLocation createFromParcel(Parcel in) {
            return new BodySubLocation(in);
        }

        @Override
        public BodySubLocation[] newArray(int size) {
            return new BodySubLocation[size];
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
    }
}
