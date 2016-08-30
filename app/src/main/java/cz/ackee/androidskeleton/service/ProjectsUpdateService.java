package cz.ackee.androidskeleton.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.model.ProjectDownloadedEvent;
import cz.ackee.androidskeleton.model.ProjectDownloadingEvent;
import cz.ackee.androidskeleton.model.Query;
import cz.ackee.androidskeleton.model.response.EasyQueriesResponse;
import cz.ackee.androidskeleton.model.response.ProjectsResponse;
import cz.ackee.androidskeleton.model.response.QueriesResponse;
import cz.ackee.androidskeleton.provider.DataProvider;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.utils.AccountType;
import cz.ackee.androidskeleton.utils.Storage;
import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;

/**
 * TODO add class description Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class ProjectsUpdateService extends IntentService {
    public static final String TAG = ProjectsUpdateService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ProjectsUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int limit = Integer.MAX_VALUE;
        int offset = 0;
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(ContentProviderOperation.newDelete(DataProvider.CONTENT_PROJECTS_URI).build());
        try {
            Cursor c = getContentResolver().query(DataProvider.CONTENT_PROJECTS_URI, null, null, null, null);
            boolean hasSomeProjects = c.moveToFirst();
            c.close();
            EventBus.getDefault().postSticky(new ProjectDownloadingEvent(hasSomeProjects));
            ArrayList<Project> allProjects = new ArrayList<>();

            while (true) {
                Map<String, String> map = new HashMap<>();
                map.put("offset", offset + "");
                map.put("limit", limit + "");
                ProjectsResponse response = RestServiceGenerator.getApiService().getProjects(map);
                allProjects.addAll(response.getProjects());

                offset += response.getLimit();
                limit = response.getLimit();
                if (offset >= response.getTotalCount()) {
                    break;
                }
            }
            for (Project p : allProjects) {
                operations.add(ContentProviderOperation.newInsert(DataProvider.CONTENT_PROJECTS_URI).withValues(p.getContentValues(allProjects)).build());
            }
            List<Query> queries;
            operations.add(ContentProviderOperation.newDelete(DataProvider.CONTENT_QUERIES_URI).build());
            if (Storage.getAccountType() == AccountType.EASY_REDMINE.value) {
                EasyQueriesResponse response = RestServiceGenerator.getApiService().getEasyQueries();
                queries = response.getQueries();
                List<Query> filteredQueries = new ArrayList<>();
                for (Query q : queries) {//filter only easy issue queries
                    if (q.getType().equals("EasyIssueQuery")) {
                        filteredQueries.add(q);
                    }
                }
                queries = filteredQueries;
            } else {
                QueriesResponse response = RestServiceGenerator.getApiService().getQueries();
                queries = response.getQueries();
            }

//            Log.d(TAG, "Queries downloaded " + response.getQueries().size());
            if (queries != null) {
                for (Query q : queries) {
                    operations.add(ContentProviderOperation.newInsert(DataProvider.CONTENT_QUERIES_URI)
                            .withValues(q.getContentValues()).build());
                }
            }

            getContentResolver().applyBatch(DataProvider.AUTHORITY, operations);
            EventBus.getDefault().postSticky(new ProjectDownloadedEvent(true));


        } catch (RetrofitError | RemoteException | OperationApplicationException err) {
            err.printStackTrace();
            EventBus.getDefault().postSticky(new ProjectDownloadedEvent(false));
        }

    }
}
