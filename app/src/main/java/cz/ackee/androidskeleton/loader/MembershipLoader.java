package cz.ackee.androidskeleton.loader;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.loader.base.BaseLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.IdNameEntity;
import cz.ackee.androidskeleton.model.Membership;
import cz.ackee.androidskeleton.model.response.MembershipResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import retrofit.RetrofitError;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 7.4.2015.
 */
public class MembershipLoader extends BaseLoader<List<IdNameEntity>> {
    public static final String TAG = MembershipLoader.class.getName();
    String mProjectId;

    public MembershipLoader(Context context, String projectId) {
        super(context);
        mProjectId = projectId;
    }

    @Override
    protected BasicResponse<List<IdNameEntity>> loadData() {
        List<IdNameEntity> memberships = new ArrayList<>();
        BasicResponse<List<IdNameEntity>> response = new BasicResponse<>();
        int limit = 20;
        int offset = 0;
        try {
            while (true) {
                Map<String, String> map = new HashMap<>();
                map.put("offset", offset + "");
                map.put("limit", limit + "");
                MembershipResponse responseHttp = RestServiceGenerator.getApiService().getMemberships(String.valueOf(mProjectId), map);
                for (Membership m : responseHttp.getMemberShips()) {
                    if (m.getUser() != null) {
                        memberships.add(m.getUser());
                    } else if (m.getGroup() != null) {
                        m.getGroup().setName(getContext().getString(R.string.group, m.getGroup().getName()));
                        memberships.add(m.getGroup());
                    }
                }
                offset += responseHttp.getLimit();
                if (offset >= responseHttp.getTotalCount()) {
                    break;
                }
            }
        } catch (RetrofitError err) {
            if (err != null) {
                Log.d(TAG, "GET Memberships RetrofitError" + err.getLocalizedMessage());
            }
        }
        response.setData(memberships);
        return response;
    }
}