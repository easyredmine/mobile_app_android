package cz.ackee.androidskeleton.model;

import java.util.List;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {11. 6. 2015}
 **/
public class CustomFieldValues {
    public static final String TAG = CustomFieldValues.class.getName();

    int id;
    String fieldFormat;
    List<NameValueEntity> values;

    public int getId() {
        return id;
    }

    public String getFieldFormat() {
        return fieldFormat;
    }

    public List<NameValueEntity> getValues() {
        return values;
    }
}
