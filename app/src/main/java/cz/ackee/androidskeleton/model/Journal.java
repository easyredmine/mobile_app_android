package cz.ackee.androidskeleton.model;

import java.util.List;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 24.3.2015.
 */
public class Journal {
    public static final String TAG = Journal.class.getName();

    int id;
    IdNameEntity user;
    String notes;
    String createdOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IdNameEntity getUser() {
        return user;
    }

    public void setUser(IdNameEntity user) {
        this.user = user;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
