package cz.ackee.androidskeleton.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.Configuration;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.adapter.TaskAdapter;
import cz.ackee.androidskeleton.adapter.TasksEndlessAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.loader.QueriesLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.Query;
import cz.ackee.androidskeleton.ui.RadioButtonView;
import cz.ackee.androidskeleton.utils.Storage;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 16.3.2015.
 */
public class FiltersFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<BasicResponse<List<Query>>>, View.OnClickListener {
    public static final String TAG = TasksFragment.class.getName();
    private static final String ACTIVE_FILTERS_KEY = "mActiveFilters";
    private static final String QUERY_TYPE = "query_type";

    public static final int PRIVATE_LOADER = 1;
    public static final int PUBLIC_LOADER = 2;

    TasksEndlessAdapter mEndlessAdapter;
    TaskAdapter mAdapter;
    @InjectView(R.id.filter_default)
    RadioButtonView mRBDefault;


    @InjectView(R.id.publicFilterWrapper)
    LinearLayout publicFilterWrapper;
    @InjectView(R.id.privateFilterWrapper)
    LinearLayout privateFilterWrapper;

    private ArrayList<Integer> mActiveFilters;
    private HashMap<Integer, RadioButtonView> mRadios;
    private Integer mLastSelected;
    private Integer mRadioCount;
    private String mQuery;

    public static FiltersFragment newInstance() {
        Bundle args = new Bundle();
        FiltersFragment ff = new FiltersFragment();
        ff.setArguments(args);
        return ff;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRadios = new HashMap<>();
        mLastSelected = 0;
        mRadioCount = 0;
        mActiveFilters = getArguments().getIntegerArrayList(ACTIVE_FILTERS_KEY);
        if (mActiveFilters == null) {
            mActiveFilters = new ArrayList<>();
            getArguments().putIntegerArrayList(ACTIVE_FILTERS_KEY, mActiveFilters);
        }

        mQuery = Storage.getFilter();
    }

    private void initDefaultRadios(View view) {
        RadioButtonView rbv = null;
        for (int i = 1; i < 6; i++) {
            int id = getResources().getIdentifier("filter" + i, "id", getFragmentActivity().getPackageName());
            if (id > 0) {
                rbv = (RadioButtonView) view.findViewById(id);
                rbv.setPosition(++mRadioCount);
                rbv.setOnClickListener(this);
                checkIfSelected(rbv);
                mRadios.put(rbv.getPosition(), (RadioButtonView) view.findViewById(id));
            }
        }
        if (rbv != null) {
            rbv.setLast();
        }
    }

    @Override
    protected String getTitle() {
        return getString(R.string.filters);
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
        initDefaultRadios(getView());
    }

    @Override
    public void onPause() {
        super.onPause();
        //initDefaultRadios(getView());
        getArguments().putIntegerArrayList(ACTIVE_FILTERS_KEY, mActiveFilters);
    }

    @Override
    public int getPositionInMenu() {
        return Configuration.MENU_POS_TASKS;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        //initDefaultRadios(view);
        view.findViewById(R.id.filter_default).setOnClickListener(this);
        Bundle args = new Bundle();
        args.putBoolean(QUERY_TYPE, true);
        getLoaderManager().restartLoader(PUBLIC_LOADER, args, FiltersFragment.this).forceLoad();

        args = new Bundle();
        args.putBoolean(QUERY_TYPE, false);
        getLoaderManager().restartLoader(PRIVATE_LOADER, args, FiltersFragment.this).forceLoad();

        if (TextUtils.isEmpty(Storage.getFilterName())) {
            mRBDefault.setChecked(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filters, container, false);
    }


    @Override
    public Loader<BasicResponse<List<Query>>> onCreateLoader(int id, Bundle args) {
        return new QueriesLoader(getFragmentActivity(), args.getBoolean(QUERY_TYPE));
    }

    @Override
    public void onLoadFinished(Loader<BasicResponse<List<Query>>> loader, BasicResponse<List<Query>> data) {
        RadioButtonView rbv = null;
        switch (loader.getId()) {
            case PUBLIC_LOADER:
                publicFilterWrapper.removeAllViews();
                if (data.getData().size() == 0)
                    publicFilterWrapper.setVisibility(View.GONE);

                publicFilterWrapper.addView(addHeader(getResources().getString(R.string.public_filter)));
                for (Query query : data.getData()) {
                    rbv = new RadioButtonView(getFragmentActivity());
                    rbv.setTitle(query.getName());
                    rbv.setQueryId(query.getId());
                    rbv.setPosition(++mRadioCount);
                    rbv.setOnClickListener(this);
                    checkIfSelected(rbv);
                    mRadios.put(rbv.getPosition(), rbv);
                    publicFilterWrapper.addView(rbv);
                }
                if (rbv != null) {
                    rbv.setLast();
                }
                break;
            case PRIVATE_LOADER:
                privateFilterWrapper.removeAllViews();
                if (data.getData().size() == 0)
                    privateFilterWrapper.setVisibility(View.GONE);

                privateFilterWrapper.addView(addHeader(getResources().getString(R.string.private_filter)));
                for (Query query : data.getData()) {
                    rbv = new RadioButtonView(getFragmentActivity());
                    rbv.setTitle(query.getName());
                    rbv.setQueryId(query.getId());
                    rbv.setPosition(++mRadioCount);
                    rbv.setOnClickListener(this);
                    checkIfSelected(rbv);
                    mRadios.put(rbv.getPosition(), rbv);
                    privateFilterWrapper.addView(rbv);
                }
                if (rbv != null) {
                    rbv.setLast();
                }
                break;
        }
    }

    private void checkIfSelected(RadioButtonView rbv) {
        if (mQuery.equals(rbv.getQuery())) {
            mLastSelected = rbv.getPosition();
            rbv.setChecked(true);
        } else {
            rbv.setChecked(false);
        }
    }

    private View addHeader(String header) {
        TextView view = new TextView(getFragmentActivity());
        int margin = getResources().getDimensionPixelOffset(R.dimen.mediumGap);
        view.setText(header);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(margin, margin, margin, margin);
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void onLoaderReset(Loader<BasicResponse<List<Query>>> loader) {

    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButtonView) {

            // unselect last selected radiobutton
            if (mLastSelected > 0) {
                mRadios.get(mLastSelected).setChecked(false);
            }
            RadioButtonView rbv = (RadioButtonView) v;
            if (v.getId() == R.id.filter_default) {
                mLastSelected = 0;
                Storage.setFilter("");
                Storage.setFilterName(null);
                rbv.setChecked(true);
            } else {
                mRBDefault.setChecked(false);
                if (mLastSelected == rbv.getPosition()) {
                    rbv.setChecked(true);
//                    mLastSelected = 0;
//                    rbv.setChecked(false);
//                    Storage.setFilter("");
//                    Storage.setFilterName(null);
                } else {
                    mLastSelected = rbv.getPosition();
                    rbv.setChecked(true);
                    Storage.setFilter(rbv.getQuery());
                    Storage.setFilterName(rbv.getTitle());
                }
            }
        }
    }
}