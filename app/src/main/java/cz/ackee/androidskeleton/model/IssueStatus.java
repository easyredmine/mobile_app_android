package cz.ackee.androidskeleton.model;

import cz.ackee.androidskeleton.iface.Defaultable;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class IssueStatus implements Defaultable {
    public static final String TAG = IssueStatus.class.getName();
    private Integer id;
    public Integer value;
    String name;
    boolean isDefault;

    public Integer getId() {
        if(id!=null){
            return id;
        }else if(value!=null){
            return value;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }
}
