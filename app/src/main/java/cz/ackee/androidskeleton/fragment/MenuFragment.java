package cz.ackee.androidskeleton.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.Configuration;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.adapter.SearchProjectAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.iface.OnMenuItemClickListener;
import cz.ackee.androidskeleton.loader.SearchProjectLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.utils.AccountType;
import cz.ackee.androidskeleton.utils.Storage;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class MenuFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<BasicResponse<List<Project>>> {
    public static final String TAG = MenuFragment.class.getName();
    private static final String QUERY_KEY = "query";

    OnMenuItemClickListener mMenuListener;

    @InjectView(R.id.vLogoDrawer)
    ImageView vLogoDrawer;

    @InjectView(R.id.drawer_layout)
    LinearLayout drawer_layout;

    @InjectView(R.id.searchProject)
    SearchView sv;
    @InjectView(R.id.listProjects)
    ListView listProjects;

    @InjectView(R.id.vUsername)
    TextView vName;

    @InjectView(R.id.scrollView)
    ScrollView mScrollView;

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void initAB() {

    }

    @Override
    public String getGAName() {
        return null;
    }

    @Override
    public int getPositionInMenu() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public Loader<BasicResponse<List<Project>>> onCreateLoader(int id, Bundle args) {
        return new SearchProjectLoader(getActivity(), args.getString(QUERY_KEY));
    }

    @Override
    public void onLoadFinished(Loader<BasicResponse<List<Project>>> loader, BasicResponse<List<Project>> data) {
        if (data.getData().size() > 0) {
            listProjects.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        } else {
            listProjects.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }

        ((SearchProjectAdapter) listProjects.getAdapter()).clear();
        ((SearchProjectAdapter) listProjects.getAdapter()).appendData(data.getData());
    }

    @Override
    public void onLoaderReset(Loader<BasicResponse<List<Project>>> loader) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        vName.setText(Storage.getName());

        SearchProjectAdapter adapter = new SearchProjectAdapter(getFragmentActivity());
        listProjects.setAdapter(adapter);

        listProjects.setDivider(null);

        if (getFragmentActivity().isEasyRedmine()) {
            vLogoDrawer.setImageResource(R.drawable.er_logo_drawer);
            listProjects.setBackgroundColor(getResources().getColor(R.color.er_drawer_searchview_background));
        } else {
            vLogoDrawer.setImageResource(R.drawable.r_logo_drawer);
            listProjects.setBackgroundColor(getResources().getColor(R.color.r_drawer_searchview_background));
        }

        initGUI();
        initSearchView();

        View newIssue = view.findViewById(R.id.txtNewIssues);
        newIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuListener.onItemClicked(Configuration.MENU_POS_NEW_ISSUE);
            }
        });

        View tasks = view.findViewById(R.id.txtTasks);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuListener.onItemClicked(Configuration.MENU_POS_TASKS);
            }
        });

        View logout = view.findViewById(R.id.txtLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuListener.onItemClicked(Configuration.MENU_POS_LOGOUT);
            }
        });
    }

    private void initGUI() {
        if (Storage.getAccountType() == AccountType.REDMINE.value) {
            drawer_layout.setBackgroundColor(getResources().getColor(R.color.r_drawer_background));
        } else {
            drawer_layout.setBackgroundColor(getResources().getColor(R.color.er_drawer_background));
        }
    }

    private void initSearchView() {
        sv.onActionViewExpanded();
        TextView txt = (TextView) sv.findViewById(R.id.search_src_text);
        if (txt != null) {
            txt.setTextColor(Color.WHITE);
        }
        sv.setQueryHint(getString(R.string.searchProject));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    listProjects.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);
                    return false;
                }
                Bundle args = new Bundle();
                args.putString(QUERY_KEY, s);
                getLoaderManager().restartLoader(0, args, MenuFragment.this).forceLoad();
                return false;
            }
        });

        sv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "searchview has focus : " + hasFocus);
            }
        });
        sv.clearFocus();

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) sv.findViewById(R.id.search_src_text);
        theTextArea.setHintTextColor(getResources().getColor(R.color.hint_color));

        int searchPlateId = sv.getContext().getResources().getIdentifier("id/search_plate", null, null);
        // Getting the 'search_plate' LinearLayout.
        View searchPlate = sv.findViewById(searchPlateId);
        // Setting background of 'search_plate' to earlier defined drawable.


        int textId = sv.getContext().getResources().getIdentifier("id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(textId);

        //searchPlate.setBackgroundResource(R.color.r_drawer_searchview_background);
        // textView.setTextColor(Color.WHITE);

        if (Storage.getAccountType() == AccountType.REDMINE.value) {
            sv.setBackgroundColor(getResources().getColor(R.color.r_drawer_searchview_background));
        } else {
            sv.setBackgroundColor(getResources().getColor(R.color.er_drawer_searchview_background));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMenuListener = (OnMenuItemClickListener) activity;
    }
}
