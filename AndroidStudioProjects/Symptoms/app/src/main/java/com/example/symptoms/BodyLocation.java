package com.example.symptoms;
import android.os.Parcel;
import android.os.Parcelable;

public class BodyLocation implements Parcelable {
    //@Expose(serialize = true, deserialize = true)
    private int ID;
    //@Expose(serialize = true, deserialize = true)
    private String Name;

    public BodyLocation(int ID, String name) {
        this.ID = ID;
        this.Name = name;
    }

    public BodyLocation(){};

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
        this.Name = name;
    }

    protected BodyLocation(Parcel in) {
        ID = in.readInt();
        Name = in.readString();
    }
    public static final Creator<BodyLocation> CREATOR = new Creator<BodyLocation>() {
        @Override
        public BodyLocation createFromParcel(Parcel in) {
            return new BodyLocation(in);
        }

        @Override
        public BodyLocation[] newArray(int size) {
            return new BodyLocation[size];
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


