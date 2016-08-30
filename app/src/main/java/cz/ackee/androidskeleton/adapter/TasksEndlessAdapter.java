package cz.ackee.androidskeleton.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.model.response.IssuesResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.utils.StringUtils;
import retrofit.RetrofitError;

/**
 * Endless wrapper for issues list adapter
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TasksEndlessAdapter extends EndlessAdapter {
    public static final String TAG = TasksEndlessAdapter.class.getName();
    private static final int LIMIT = 10;
    private final TaskAdapter mTaskAdapter;
    private final Handler mHandler;

    public void setCallback(OnEndlessAdapterLoaded callback) {
        mCallback = callback;
    }

    public interface OnEndlessAdapterLoaded {
        public void onEmptyList();
    }


    private OnEndlessAdapterLoaded mCallback;
    int offset = 0;
    int totalSize = Integer.MAX_VALUE;
    private IssuesResponse mLoadedResponse;
    private Boolean mIsEnoughTasksInResponse = true;
    private String mQuery;

    public TasksEndlessAdapter(ListAdapter wrapped) {
        super(wrapped);
        mTaskAdapter = (TaskAdapter) wrapped;
        reset();
        mHandler = new Handler();
    }

    private void reset() {
        mIsEnoughTasksInResponse = true;
        offset = 0;
        totalSize = Integer.MAX_VALUE;
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        if (offset >= totalSize) {
            return false;
        }

        Map<String, String> map;
        if (mQuery != null && !mQuery.isEmpty()) {
            map = StringUtils.splitQuery(mQuery);
        } else {
            map = new HashMap<>();
        }

        map.put("offset", offset + "");
        map.put("limit", LIMIT + "");
        try {
            mLoadedResponse = RestServiceGenerator.getApiService().getIssues(map);
        } catch (RetrofitError err) {
            if (mCallback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onEmptyList();
                    }
                });

            }
            throw err;
        }

        totalSize = mLoadedResponse.getTotalCount();
        offset += LIMIT;
        return true;
    }

    @Override
    protected void appendCachedData() {
        //if (offset >= totalSize) {
        //    return;
        //}
        if (totalSize == 0 && mCallback != null) {
            mCallback.onEmptyList();
        }
        if (mIsEnoughTasksInResponse) {
            mTaskAdapter.appendData(mLoadedResponse.getIssues());

            if (mLoadedResponse.getIssues().size() < 10) {
                mIsEnoughTasksInResponse = false;
            }

        }
    }

    @Override
    protected View getPendingView(ViewGroup parent) {
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_progess, parent, false);
        MDTintHelper.setTint((ProgressBar) ((FrameLayout) v).getChildAt(0), parent.getContext().getResources().getColor(((BaseFragmentActivity) parent.getContext()).isEasyRedmine() ? R.color.er_primary : R.color.r_primary));
        return v;
    }

    public void setFilters() {
        reset();
        mTaskAdapter.clear();
    }

    public void setFilterQuery(String filter) {
        mQuery = filter;
    }
}
