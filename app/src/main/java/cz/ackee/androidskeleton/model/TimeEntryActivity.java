package cz.ackee.androidskeleton.model;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TimeEntryActivity {
    public static final String TAG = TimeEntryActivity.class.getName();
    int id;
    String name;
    boolean isDefault;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String toString() {
        return name;
    }
}
