package cz.ackee.androidskeleton.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.Configuration;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.NewTaskActivity;
import cz.ackee.androidskeleton.adapter.TaskAdapter;
import cz.ackee.androidskeleton.adapter.TasksEndlessAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseListFragment;
import cz.ackee.androidskeleton.utils.Storage;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class TasksFragment extends BaseListFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = TasksFragment.class.getName();
    private static final String ACTIVE_FILTERS_KEY = "mActiveFilters";

    TasksEndlessAdapter mEndlessAdapter;
    TaskAdapter mAdapter;

    private ArrayList<Integer> mActiveFilters;
    private String mLastFilter;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActiveFilters = getArguments().getIntegerArrayList(ACTIVE_FILTERS_KEY);
        if (mActiveFilters == null) {
            mActiveFilters = new ArrayList<>();
            getArguments().putIntegerArrayList(ACTIVE_FILTERS_KEY, mActiveFilters);
        }
        mLastFilter = Storage.getFilter();
    }

    @Override
    protected String getTitle() {
        if (!Storage.getFilterName().isEmpty()) {
            return Storage.getFilterName();
        } else {
            return getString(R.string.tasks);
        }
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public int getPositionInMenu() {
        return Configuration.MENU_POS_TASKS;
    }

    @Override
    public boolean hasHamburgerMenu() {
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(getFragmentActivity().isEasyRedmine() ? R.color.er_primary : R.color.r_primary);

        if (!mLastFilter.equals(Storage.getFilter()) || mAdapter == null) {
            setFilteredAdapter(!mLastFilter.equals(Storage.getFilter()));
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToListView(getListView());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskActivity.start(getFragmentActivity());
            }
        });
    }

    private void setFilteredAdapter(boolean filterChanged) {
        mAdapter = new TaskAdapter(getActivity());

        if (mEndlessAdapter == null || filterChanged) {
            mLastFilter = Storage.getFilter();
            mEndlessAdapter = new TasksEndlessAdapter(mAdapter);
            mEndlessAdapter.setFilterQuery(Storage.getFilter());
            setListAdapter(mEndlessAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_tasks, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Fragment f = TaskDetailFragment.newInstance((cz.ackee.androidskeleton.model.Issue) l.getItemAtPosition(position));
        f.setTargetFragment(this, 0);
        getFragmentActivity().replaceFragment(f);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                getFragmentActivity().replaceFragment(FiltersFragment.newInstance());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mAdapter = new TaskAdapter(getActivity());
        mLastFilter = Storage.getFilter();
        mEndlessAdapter = new TasksEndlessAdapter(mAdapter);
        mEndlessAdapter.setFilterQuery(Storage.getFilter());
        setListAdapter(mEndlessAdapter);
        mSwipeRefresh.setRefreshing(false);
    }
}
