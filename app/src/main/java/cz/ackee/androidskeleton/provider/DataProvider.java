package cz.ackee.androidskeleton.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import cz.ackee.androidskeleton.App;
import cz.ackee.androidskeleton.db.DatabaseHelper;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.model.Query;

/**
 * Basic content provider
 * Created by david.bilik@ackee.cz on 26. 6. 2014.
 */
public class DataProvider extends ContentProvider {

    public static final String AUTHORITY = App.PACKAGE_NAME;
    private static final String PROJECTS_BASE_PATH = "projects";
    private static final String QUERIES_BASE_PATH = "queries";

    private static final String QUERIES_PUBLIC_PATH = "/public";
    private static final String QUERIES_PRIVATE_PATH = "/private";

    public static final int PROJECTS_ALL = 1;
    public static final int QUERIES_PRIVATE = 2;
    public static final int QUERIES_PUBLIC = 3;
    public static final int QUERIES_ALL = 4;

    public static final Uri CONTENT_PROJECTS_URI = Uri.parse("content://" + AUTHORITY + "/" + PROJECTS_BASE_PATH);
    public static final Uri CONTENT_QUERIES_URI = Uri.parse("content://" + AUTHORITY + "/" + QUERIES_BASE_PATH);

    private DatabaseHelper mHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PROJECTS_BASE_PATH, PROJECTS_ALL);
        uriMatcher.addURI(AUTHORITY, QUERIES_BASE_PATH, QUERIES_ALL);
        uriMatcher.addURI(AUTHORITY, QUERIES_BASE_PATH + QUERIES_PUBLIC_PATH, QUERIES_PUBLIC);
        uriMatcher.addURI(AUTHORITY, QUERIES_BASE_PATH + QUERIES_PRIVATE_PATH, QUERIES_PRIVATE);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PROJECTS_ALL:
                return Project.TABLE_NAME;
            case QUERIES_ALL:
                return Query.TABLE_NAME;
            case QUERIES_PRIVATE:
                return Query.TABLE_NAME;
            case QUERIES_PUBLIC:
                return Query.TABLE_NAME;
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("TAG", "CO JE NULL uri"+uri.toString()+ " proj " + projection + " sel " + selection + " selArgs " + selectionArgs + " sort " + sortOrder);
        return mHelper.getWritableDatabase().query(getType(uri), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mHelper.getWritableDatabase().insert(getType(uri), null, values);
        return Uri.withAppendedPath(uri, id + "");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mHelper.getWritableDatabase().delete(getType(uri), selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mHelper.getWritableDatabase().update(getType(uri), values, selection, selectionArgs);
    }
}
