package cz.ackee.androidskeleton.loader;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.loader.base.BaseLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.IdNameEntity;
import cz.ackee.androidskeleton.model.ProjectVersion;
import cz.ackee.androidskeleton.model.response.ProjectVersionsResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import retrofit.RetrofitError;

/**
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 24/11/15
 **/
public class VersionsLoader extends BaseLoader<List<IdNameEntity>> {
    public static final String TAG = VersionsLoader.class.getName();

    private final String mProjectId;

    public VersionsLoader(Context context, String projectId) {
        super(context);
        mProjectId = projectId;
    }

    @Override
    protected BasicResponse<List<IdNameEntity>> loadData() {
        List<IdNameEntity> versions = new ArrayList<>();
        BasicResponse<List<IdNameEntity>> response = new BasicResponse<>();
        try {
            ProjectVersionsResponse responseHttp = RestServiceGenerator.getApiService().getTargetVersions(mProjectId);
            for (ProjectVersion m : responseHttp.getVersions()) {
                versions.add(new IdNameEntity(m.getId(), m.getName()));
            }
        } catch (RetrofitError err) {
            Log.d(TAG, "GET Memberships RetrofitError" + err.getLocalizedMessage());
        }
        response.setData(versions);
        return response;
    }
}
