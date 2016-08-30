package cz.ackee.androidskeleton.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.Configuration;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.adapter.JournalAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseListFragment;
import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.model.Journal;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * TODO add class description
 * Created by Petr Schneider[petr.schneider@ackee.cz] on {17. 2. 2015}
 */
public class JournalFragment extends BaseListFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = JournalFragment.class.getName();
    public static final String ISSUE_KEY = "issue";

    private static final String ACTIVE_FILTERS_KEY = "mActiveFilters";
    private static final int NEW_COMMENT_REQUEST_CODE = 12;

    JournalAdapter mAdapter;

    private ArrayList<Integer> mActiveFilters;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefresh;
    @InjectView(R.id.vSendComment)
    FloatingActionButton vSendComment;
    @InjectView(android.R.id.empty)
    TextView mTxtEmpty;
    Issue mIssue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getTitle() {
        return getIssue().getSubject();
    }

    @Override
    protected void initAB() {
        baseSettingsAB();
    }

    @Override
    public String getGAName() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActiveFilters = getArguments().getIntegerArrayList(ACTIVE_FILTERS_KEY);
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putIntegerArrayList(ACTIVE_FILTERS_KEY, mActiveFilters);
    }

    @Override
    public int getPositionInMenu() {
        return Configuration.MENU_POS_TASKS;
    }

    @Override
    public boolean hasHamburgerMenu() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_COMMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            updateJournal();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initGUI();
        setData();


        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(getFragmentActivity().isEasyRedmine() ? R.color.er_primary : R.color.r_primary);

        vSendComment.attachToListView(getListView());
        vSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSendComment();
            }
        });
    }

    private void performSendComment() {
        Bundle args = new Bundle();
        args.putParcelable(ISSUE_KEY, mIssue);
        Intent intent = BaseFragmentActivity.generateIntent(getContext(), NewCommentFragment.class.getName(), args);
        startActivityForResult(intent, NEW_COMMENT_REQUEST_CODE);
    }

    private void initGUI() {
//        if (!getFragmentActivity().isEasyRedmine()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                vSendComment.setBackground(getResources().getDrawable(R.drawable.r_image_button_comments_send_active));
//            } else {
//                vSendComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.r_image_button_comments_send_active));
//            }
//        }
    }

    private void setData() {

        mAdapter = new JournalAdapter(getActivity(), filterJournals(getIssue().getJournals()));
        setListAdapter(mAdapter);
        if (mAdapter.getCount() == 0) {
            mTxtEmpty.setVisibility(View.VISIBLE);
        } else {
            mTxtEmpty.setVisibility(View.GONE);
        }
    }

    private List<Journal> filterJournals(List<Journal> journals) {
        List<Journal> filteredJournal = new ArrayList<>();
        for (Journal j : journals) {
            if (j.getNotes() != null && !j.getNotes().isEmpty()) {
                filteredJournal.add(j);
            }
        }
        return filteredJournal;
    }

    @Override
    public void onRefresh() {
        updateJournal();
    }

    public void updateJournal() {
        RestServiceGenerator.getApiService().getIssueDetail(getIssue().getId() + "", getIssueQueryMap(), new Callback<IssueResponse>() {
            @Override
            public void success(IssueResponse issueResponse, Response response) {
                if (getActivity() == null || getView() == null) {
                    return;
                }
                getIssue().setJournals(issueResponse.getIssue().getJournals());
                mIssue.setJournals(issueResponse.getIssue().getJournals());
                mSwipeRefresh.setRefreshing(false);
                setData();
            }

            @Override
            public void failure(RetrofitError error) {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_journal, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static Fragment newInstance(Issue issue) {
        Bundle args = new Bundle();
        args.putParcelable(ISSUE_KEY, issue);
        JournalFragment jf = new JournalFragment();
        jf.setArguments(args);
        return jf;
    }

    public Issue getIssue() {
        if (mIssue == null) {
            mIssue = (Issue) getArguments().getParcelable(ISSUE_KEY);
        }
        return mIssue;
    }

    private Map<String, String> getIssueQueryMap() {
        Map<String, String> map = new HashMap<>();
        map.put("include", "journals");
        return map;
    }

    private void scrollMyListViewToBottom() {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                getListView().setSelection(mAdapter.getCount() - 1);
            }
        });
    }
}
