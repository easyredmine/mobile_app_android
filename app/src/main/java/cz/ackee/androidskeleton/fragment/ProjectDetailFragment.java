package cz.ackee.androidskeleton.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.melnykov.fab.FloatingActionButton;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.NewTaskActivity;
import cz.ackee.androidskeleton.adapter.TaskAdapter;
import cz.ackee.androidskeleton.adapter.TasksEndlessAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseListFragment;
import cz.ackee.androidskeleton.loader.ProjectLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.ui.CollapsibleView;
import cz.ackee.androidskeleton.utils.RedmineUtils;
import cz.ackee.androidskeleton.utils.TimeUtils;
import cz.ackee.androidskeleton.utils.Utils;

/**
 * Fragment displaying detail of redmine/easyredmine project description, statistics, tasks <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on {18. 2. 2015}
 */
public class ProjectDetailFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<BasicResponse<Project>>, TasksEndlessAdapter.OnEndlessAdapterLoaded {
    public static final String TAG = ProjectDetailFragment.class.getName();
    private static final String PROJECT_ID = "project_id";

    TasksEndlessAdapter mEndlessAdapter;
    TaskAdapter mAdapter;

    private ArrayList<Integer> mActiveFilters;

    Project mProject;

    CollapsibleView collapsibleDescription;
    CollapsibleView collapsibleStatistics;
    CollapsibleView collapsibleIssues;

    ListView list;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    ListView.FixedViewInfo headerInfo;

    public static ProjectDetailFragment newInstance(int projectId) {
        Bundle args = new Bundle();
        Log.d(TAG, "PROJECT_ID in ProjectDetailFragment " + projectId);
        args.putLong(PROJECT_ID, projectId);
        //args.putParcelable(PROJECT_ID, project.getId());
        ProjectDetailFragment tdf = new ProjectDetailFragment();
        tdf.setArguments(args);
        return tdf;
    }

    @Override
    protected String getTitle() {
        if(getProject() != null) {
            return getProject().getName();
        }
        if (getFragmentActivity().isEasyRedmine()) {
            return getResources().getString(R.string.title_easy_redmine);
        } else {
            return getResources().getString(R.string.title_redmine);
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
    public int getPositionInMenu() {
        return -1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.project_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(getFragmentActivity(), "EDIT", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_see_on_web:
                Utils.startBrowserApp(getFragmentActivity(), RedmineUtils.getRedmineWebLink(mProject,
                        mProject.getIdentifier()));
                break;
            case R.id.action_delete:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public Project getProject() {
        return mProject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);
        list = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        getLoaderManager().restartLoader(0, null, ProjectDetailFragment.this).forceLoad();

        fab.attachToListView(list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(PROJECT_ID, getProject().getId());
                NewTaskActivity.start(getFragmentActivity(), bundle);
            }
        });
    }

    private void initViews() {
        if (getProject() != null) {
            if (!getProject().getName().isEmpty()) {
                setTitle(getProject().getName());
            }

            TextView txtDescription = (TextView) LayoutInflater.from(getActivity()).inflate(
                    R.layout.widget_task_description, null);
            txtDescription.setText(Html.fromHtml(getProject().getDescription()));

            collapsibleDescription = (CollapsibleView) headerInfo.view.findViewById(
                    R.id.collapsibleDescription);
            collapsibleStatistics = (CollapsibleView) headerInfo.view.findViewById(
                    R.id.collapsibleStatistics);
            collapsibleIssues = (CollapsibleView) headerInfo.view.findViewById(R.id.collapsibleIssues);
            collapsibleIssues.hideUnderline(true);

            if (TextUtils.isEmpty(getProject().getDescription())) {
                collapsibleDescription.setVisibility(View.GONE);
            }
            collapsibleDescription.setCollapsibleView(txtDescription);
            collapsibleDescription.setOpened(true);

            initStatistics(getProject(), collapsibleStatistics);
            //initIssues(getProject(), collapsibleIssues);
        }
    }

    private void initStatistics(Project project, CollapsibleView collapsibleStatistics) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        View row = null;
        TextView txt;
        String EMPTY = "--";
        String dueDate = EMPTY;
        if (!TextUtils.isEmpty(project.getEasyDueDate())) {
            dueDate = TimeUtils.getTimeFormatted(project.getEasyDueDate(), TimeUtils.ATOM_FORMAT_DATE, TimeUtils.DUE_DATE);
        }
        addStatisticsRowToLayout(getResources().getString(R.string.project_detail_due_date), dueDate, layout, false);
        addStatisticsRowToLayout(getResources().getString(R.string.project_detail_sum_of_spent_time), project.getSumTimeEntries() + "", layout, false);
        addStatisticsRowToLayout(getResources().getString(R.string.project_detail_estimated_time), project.getSumEstimatedHours() + "", layout, true);
        if (TextUtils.isEmpty(project.getEasyDueDate()) && project.getSumEstimatedHours() == null && project.getSumTimeEntries() == null) {
            collapsibleStatistics.setVisibility(View.GONE);
        } else {
            collapsibleStatistics.setCollapsibleView(layout);
        }
    }

    @NonNull
    private int addStatisticsRowToLayout(String title, String value, LinearLayout layout, boolean isLast) {

        View row;
        TextView txt;
        row = LayoutInflater.from(getActivity()).inflate(R.layout.widget_project_detail_statistics,
                layout, false);
        txt = (TextView) row.findViewById(R.id.txtTitle);
        txt.setText(title);
        txt = (TextView) row.findViewById(R.id.txtValue);
        txt.setText(value);
        if (isLast) {
            row.findViewById(R.id.separator).setVisibility(View.GONE);
        }
        layout.addView(row);
        return title.equals("--") ? 0 : 1;
    }


    @Override
    public Loader<BasicResponse<Project>> onCreateLoader(int id, Bundle args) {
        return new ProjectLoader(getActivity(), getArguments().getLong(PROJECT_ID));
    }

    @Override
    public void onLoadFinished(Loader<BasicResponse<Project>> loader, BasicResponse<Project> data) {
        mProject = data.getData();
        if (mProject != null) {
            setTitle(mProject.getName());
            initIssues();
            initViews();
        }
    }

    private void initIssues() {
        if (mAdapter == null) {
            mAdapter = new TaskAdapter(getActivity());

        }
        if (mEndlessAdapter == null) {
            mEndlessAdapter = new TasksEndlessAdapter(mAdapter);
            mEndlessAdapter.setCallback(this);
            mEndlessAdapter.setFilterQuery("project_id=" + getProject().getId());
        }
        setListAdapter(mEndlessAdapter);
    }

    @Override
    public void onLoaderReset(Loader<BasicResponse<Project>> loader) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        getFragmentActivity().replaceFragment(TaskDetailFragment.newInstance(
                (cz.ackee.androidskeleton.model.Issue) l.getItemAtPosition(position)));
    }

    public void setListAdapter(ListAdapter adapter) {
        headerInfo = list.new FixedViewInfo();
        headerInfo.view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_header_project,
                null);
        headerInfo.isSelectable = false;
        ArrayList<ListView.FixedViewInfo> headers = new ArrayList<>(1);
        ArrayList<ListView.FixedViewInfo> footers = new ArrayList<>(0);
        headers.add(headerInfo);
        HeaderViewListAdapter wrapper = new HeaderViewListAdapter(headers, footers, adapter) {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }
        };
        list.setAdapter(wrapper);
    }

    @Override
    public void onEmptyList() {
        headerInfo.view.findViewById(R.id.collapsibleIssues).setVisibility(View.GONE);
    }
}