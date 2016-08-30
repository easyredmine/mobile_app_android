package cz.ackee.androidskeleton.model.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.BoringLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.ackee.androidskeleton.utils.Utils;

/**
 * Created by Petr Lorenc[petr.lorenc@ackee.cz] on 4.6.2015.
 */
public class CustomField implements Parcelable {
    public static final String TAG = CustomField.class.getName();

    int id;
    String name;
    String internalName;
    String fieldFormat;

    public boolean getMultiple() {
        return multiple;
    }

    boolean multiple;

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {

        return value;
    }

    Object value;
    List<String> listOfValues;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    /**
     * value is a object of strings or a string
     * @return ArrayList - if object is only one string
     */
    public ArrayList<String> getValues() {
        if( multiple){
            if(value instanceof String) {
                value = new ArrayList<>();
            }
            return ((ArrayList<String>) value);
        }else{
            ArrayList a = new  ArrayList<String>();
            if(value != null){
                a.add((String) value);
            }else{
                return null;
            }
            return a;
        }
    }

    public String getFieldFormat() {
        return fieldFormat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(multiple  ? 1 : 0);
        dest.writeString(this.internalName);
        dest.writeString(this.fieldFormat);

        listOfValues = getValues();
        dest.writeStringList(listOfValues);
    }

    public CustomField() {
        listOfValues = new ArrayList<>();
    }

    private CustomField(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.multiple = in.readInt() == 1;
        this.internalName = in.readString();
        this.fieldFormat = in.readString();

        listOfValues = new ArrayList<>();
        in.readStringList(listOfValues);
    }

    public static final Parcelable.Creator<CustomField> CREATOR = new Parcelable.Creator<CustomField>() {
        public CustomField createFromParcel(Parcel source) {
            return new CustomField(source);
        }

        public CustomField[] newArray(int size) {
            return new CustomField[size];
        }
    };

}
