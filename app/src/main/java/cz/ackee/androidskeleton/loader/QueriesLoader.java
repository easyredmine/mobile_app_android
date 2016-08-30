package cz.ackee.androidskeleton.loader;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.loader.base.BaseLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.model.Query;
import cz.ackee.androidskeleton.provider.DataProvider;

/**
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 16.3.2015.
 */
public class QueriesLoader extends BaseLoader<List<Query>> {
    public boolean isPublic;

    public QueriesLoader(Context context, boolean isPublic) {
        super(context);
        this.isPublic = isPublic;
    }

    @Override
    protected BasicResponse<List<Query>> loadData() {
 Cursor c = getContext().getContentResolver().query(DataProvider.CONTENT_QUERIES_URI, null, Query.COL_IS_PUBLIC + " = ?", new String[]{String.valueOf( isPublic  ?  1: 0)}, null);
        BasicResponse<List<Query>> response = new BasicResponse<>();
        List<Query> queries = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Query q = new Query(c);
                queries.add(q);
            } while (c.moveToNext());
        }
        c.close();
        response.setData(queries);
        return response;
    }
}
