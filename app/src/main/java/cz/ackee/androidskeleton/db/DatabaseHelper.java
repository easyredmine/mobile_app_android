package cz.ackee.androidskeleton.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.model.Query;

/**
 * Database helper for easier managing database lifecycle
 * Created by david.bilik@ackee.cz on 26. 6. 2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;
    private static final String TAG = DatabaseHelper.class.getName();
    private static final String DB_NAME = "database.db";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Project.CREATE_TABLE);
        db.execSQL(Query.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Deletes all rows in Project and Query tables
     */
    public void clearTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(Project.TABLE_NAME,null,null);
            db.delete(Query.TABLE_NAME,null,null);
            db.setTransactionSuccessful();
            Log.d(TAG, "All rows were deleted");
        } finally {
            db.endTransaction();
        }
    }
}
