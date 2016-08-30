package cz.ackee.androidskeleton.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.text.Normalizer;
import java.util.List;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {16. 2. 2015}
 */
public class Project implements BaseColumns {
    public static final String TAG = Project.class.getName();

    public static final String TABLE_NAME = "projects";

    public static final String COL_SERVER_ID = "server_id";
    public static final String COL_NAME = "name";
    public static final String COL_IDENTIFIER = "identifier";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STATUS = "status";
    public static final String COL_CREATED_ON = "created_on";
    public static final String COL_UPDATED_ON = "updated_on";
    public static final String COL_PARENT_ID = "parent_id";
    public static final String COL_EASY_DUE_DATE = "easy_due_date";
    public static final String COL_SEARCH = "search_column";
    public static final String COL_SHOW_NAME = "show_name";

    private static final String COL_SUM_TIME = "sum_time";
    private static final String COL_SUM_ESTIMATED_HOURS = "sum_estimated_hours";
    public static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_SERVER_ID + " NUMBER, " +
            COL_NAME + " TEXT," +
            COL_IDENTIFIER + " TEXT," +
            COL_DESCRIPTION + " TEXT," +
            COL_STATUS + " NUMBER," +
            COL_CREATED_ON + " TEXT," +
            COL_SEARCH + " TEXT," +
            COL_UPDATED_ON + " TEXT," +
            COL_SHOW_NAME + " TEXT," +
            COL_SUM_TIME + " NUMBER, " +
            COL_SUM_ESTIMATED_HOURS + " NUMBER, " +
            COL_EASY_DUE_DATE + " TEXT," +
            COL_PARENT_ID + " NUMBER);";
    String showName;

    int id;
    String name;
    String identifier;
    String description;
    int status;
    String createdOn;
    String updatedOn;
    String easyDueDate;
    Double sumTimeEntries;
    Double sumEstimatedHours;

    public Double getSumTimeEntries() {
        return sumTimeEntries;
    }

    public Double getSumEstimatedHours() {
        return sumEstimatedHours;
    }

    IdNameEntity parent;
    List<IdNameEntity> trackers;
    List<IdNameEntity> issueCategories;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {
        return status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getEasyDueDate() {
        return easyDueDate;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public IdNameEntity getParent() {
        return parent;
    }

    public List<IdNameEntity> getTrackers() {
        return trackers;
    }

    public List<IdNameEntity> getIssueCategories() {
        return issueCategories;
    }


    public Project(Cursor c) {
        createdOn = c.getString(c.getColumnIndex(COL_CREATED_ON));
        description = c.getString(c.getColumnIndex(COL_DESCRIPTION));
        identifier = c.getString(c.getColumnIndex(COL_IDENTIFIER));
        name = c.getString(c.getColumnIndex(COL_NAME));
        showName = c.getString(c.getColumnIndex(COL_SHOW_NAME));
        updatedOn = c.getString(c.getColumnIndex(COL_UPDATED_ON));
        easyDueDate = c.getString(c.getColumnIndex(COL_EASY_DUE_DATE));
        id = c.getInt(c.getColumnIndex(COL_SERVER_ID));
        int parentId = c.getInt(c.getColumnIndex(COL_PARENT_ID));
        if (parentId > 0) {
            IdNameEntity parent = new IdNameEntity();
            parent.setId(parentId);
            this.parent = parent;
        }
        if (!c.isNull(c.getColumnIndex(COL_SUM_ESTIMATED_HOURS))) {
            sumEstimatedHours = c.getDouble(c.getColumnIndex(COL_SUM_ESTIMATED_HOURS));
        }
        if (!c.isNull(c.getColumnIndex(COL_SUM_TIME))) {
            sumTimeEntries = c.getDouble(c.getColumnIndex(COL_SUM_TIME));
        }
        status = c.getInt(c.getColumnIndex(COL_STATUS));
    }

    public Project() {
    }


    public ContentValues getContentValues(List<Project> otherProjects) {
        ContentValues values = new ContentValues();
        values.put(COL_CREATED_ON, createdOn);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_EASY_DUE_DATE, easyDueDate);
        values.put(COL_IDENTIFIER, identifier);
        values.put(COL_NAME, name);
        if (getParent() != null) {
            values.put(COL_PARENT_ID, getParent().getId());

        }
        values.put(COL_SERVER_ID, id);
        values.put(COL_STATUS, status);
        values.put(COL_UPDATED_ON, updatedOn);
        values.put(COL_SEARCH, generateSearchString(otherProjects));
        values.put(COL_SHOW_NAME, generateShowName(otherProjects));
        if (sumTimeEntries != null) {
            values.put(COL_SUM_TIME, sumTimeEntries);
        }
        if (sumEstimatedHours != null) {
            values.put(COL_SUM_ESTIMATED_HOURS, sumEstimatedHours);
        }
        return values;
    }

    private String generateShowName(List<Project> otherProjects) {
        String ret = name;
        Project p = this;
        while (true) {
            p = findParent(otherProjects, p.getParent());
            if (p != null) {
                ret = p.getName() + "/" + ret;
            } else {
                break;
            }

        }
        return ret;

    }

    private String generateSearchString(List<Project> otherProjects) {
        String ret = "";
        Project p = this;
        while (p != null) {
            ret = Normalizer.normalize(p.getName(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") + " " + ret;
            p = findParent(otherProjects, p.getParent());
        }
        return ret;
    }

    private Project findParent(List<Project> otherProjects, IdNameEntity parent) {
        if (parent == null) {
            return null;
        }
        for (Project p : otherProjects) {
            if (p.getId() == parent.getId()) return p;
        }

        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return showName;
    }

    public String getShowName() {
        return showName;
    }
}
