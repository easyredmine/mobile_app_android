package cz.ackee.androidskeleton.loader;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import cz.ackee.androidskeleton.loader.base.BaseLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.provider.DataProvider;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class SearchProjectLoader extends BaseLoader<List<Project>> {
    public static final String TAG = SearchProjectLoader.class.getName();
    String mQuery;

    public SearchProjectLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    protected BasicResponse<List<Project>> loadData() {
        String selection = null;
        String[] selectionArgs = null;

        if (mQuery != null) {
            selection = Project.COL_SEARCH + " LIKE ?";
            selectionArgs = new String[]{"%" + mQuery + "%"};
        }
        Log.d(TAG, "Search mQuery " + mQuery + " selection=" + selection + " selectionArgs=" + selectionArgs);

        Cursor c = getContext().getContentResolver().query(DataProvider.CONTENT_PROJECTS_URI, null, selection, selectionArgs, null);
        List<Project> projects = new ArrayList<>();
        BasicResponse<List<Project>> response = new BasicResponse<>();
        if (c.moveToFirst()) {
            do {
                Project p = new Project(c);
                projects.add(p);
            } while (c.moveToNext());
        }
        final Collator coll = Collator.getInstance(Locale.getDefault());
        coll.setStrength(Collator.PRIMARY);
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project lhs, Project rhs) {
                return coll.compare(lhs.getShowName(), rhs.getShowName());
            }
        });


        c.close();
        response.setData(projects);
        return response;
    }
}
