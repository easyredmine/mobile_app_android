package cz.ackee.androidskeleton.loader;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.loader.base.BaseLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.provider.DataProvider;

/**
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 16.3.2015.
 */
public class ProjectLoader extends BaseLoader<Project> {
    public Long mProjectId;

    public ProjectLoader(Context context, Long projectId) {
        super(context);
        this.mProjectId = projectId;
    }

    @Override
    protected BasicResponse<Project> loadData() {
        Log.d("", "PROJECT LOAD projectId = " + mProjectId);
        Cursor c = getContext().getContentResolver().query(DataProvider.CONTENT_PROJECTS_URI, null, Project.COL_SERVER_ID + " = ?", new String[]{String.valueOf(mProjectId)}, null);
        BasicResponse<Project> response = new BasicResponse<>();
        Project p = null;
        if (c.moveToFirst()) {
            p = new Project(c);
        }
        c.close();
        response.setData(p);
        Log.d("", "PROJECT LOAD projectId  p = " + p);
        return response;
    }
}
