package cz.ackee.androidskeleton.model;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 19.3.2015.
 */
public class Query {
    public static final String TAG = Project.class.getName();

    public static final String TABLE_NAME = "queries";

    public static final String COL_QUERY_ID = "query_id";
    public static final String COL_NAME = "name";
    public static final String COL_IS_PUBLIC = "is_public";


    public static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (" +
            COL_QUERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME + " TEXT," +
            COL_IS_PUBLIC + " INTEGER);";

    int id;
    String name;
    boolean isPublic;
    String type;

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Query(Cursor c) {
        id = c.getInt(c.getColumnIndex(COL_QUERY_ID));
        name = c.getString(c.getColumnIndex(COL_NAME));
        isPublic = c.getInt(c.getColumnIndex(COL_IS_PUBLIC)) > 0;
    }

    public Query() {
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_IS_PUBLIC, isPublic);
        values.put(COL_QUERY_ID, id);
        values.put(COL_NAME, name);
        return values;
    }

    public void setName(String name) {
        this.name = name;
    }
}