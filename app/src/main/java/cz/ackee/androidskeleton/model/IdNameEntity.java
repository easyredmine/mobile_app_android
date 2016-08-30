package cz.ackee.androidskeleton.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

/**
 * Basic entity that has only id and name parameters
 * Created by David Bilik[david.bilik@ackee.cz] on {16. 2. 2015}
 */
public class IdNameEntity implements Parcelable {
    public static final String TAG = IdNameEntity.class.getName();

    private Integer id;
    private Integer value;
    String name;
    List<IdNameEntity> values;

    public Integer getId() {
        if(id!=null){
            return id;
        }else if(value!=null){
            return value;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    public IdNameEntity() {

    }

    public IdNameEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private IdNameEntity(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<IdNameEntity> CREATOR = new Parcelable.Creator<IdNameEntity>() {
        public IdNameEntity createFromParcel(Parcel source) {
            return new IdNameEntity(source);
        }

        public IdNameEntity[] newArray(int size) {
            return new IdNameEntity[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<IdNameEntity> getValues() {
        return values;
    }
}
