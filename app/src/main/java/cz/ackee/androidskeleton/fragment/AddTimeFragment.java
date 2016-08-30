package cz.ackee.androidskeleton.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.model.TimeEntry;
import cz.ackee.androidskeleton.model.TimeEntryActivity;
import cz.ackee.androidskeleton.model.request.TimeEntryRequest;
import cz.ackee.androidskeleton.model.response.TimeEntryActivitiesResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.ui.GmailInputView;
import cz.ackee.androidskeleton.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class AddTimeFragment extends BaseFragment {
    public static final String TAG = AddTimeFragment.class.getName();

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy");
    private static final java.lang.String ISSUE_ID_KEY = "issueId";
    private static final String TITLE_KEY = "title";
    private static final java.lang.String SELECTION_KEY = "selectionKey";
    @InjectView(R.id.editDate)
    GmailInputView mEditDate;
    @InjectView(R.id.editHours)
    GmailInputView mEditTime;
    @InjectView(R.id.editComment)
    GmailInputView mEditComment;
    @InjectView(R.id.editActivity)
    GmailInputView mSpinner;
    private MaterialDialog mProgressDialog;


    public static AddTimeFragment newInstance(int issueId, String title) {
        Bundle args = new Bundle();
        args.putInt(ISSUE_ID_KEY, issueId);
        args.putString(TITLE_KEY, title);
        AddTimeFragment fragment = new AddTimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getArguments().getString(TITLE_KEY);
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
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_time, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        if (savedInstanceState == null) {
            mEditDate.onDateSelected(Calendar.getInstance());
        }

        RestServiceGenerator.getApiService().getTimeEntryActivities(new Callback<TimeEntryActivitiesResponse>() {
            @Override
            public void success(TimeEntryActivitiesResponse timeEntryActivitiesResponse, Response response) {
                if (getActivity() == null) {
                    return;
                }
                int defaultIndex = 0;
                for (int i = 0; i < timeEntryActivitiesResponse.getTimeEntryActivities().size(); i++) {
                    TimeEntryActivity activity = timeEntryActivitiesResponse.getTimeEntryActivities().get(i);
                    if (activity.isDefault()) {
                        defaultIndex = i;
                    }
                }

                mSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1, timeEntryActivitiesResponse.getTimeEntryActivities()));
                final int finalDefaultIndex = defaultIndex;
                mSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        mSpinner.setSelection(getArguments().getInt(SELECTION_KEY) > 0 ? getArguments().getInt(SELECTION_KEY) : finalDefaultIndex);
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mEditDate.setText(dateFormat.format(c.getTime()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add(R.string.save);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        item.setEnabled(mProgressDialog == null || !mProgressDialog.isShowing());
        item.setIcon(R.drawable.ic_check);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (validate()) {
                    sendTime();
                }
                return false;
            }
        });
    }

    private void sendTime() {
        mProgressDialog = new MaterialDialog.Builder(getFragmentActivity()).cancelable(false).content(R.string.dialog_message_adding_time).progress(true, 0)
                .build();
        mProgressDialog.show();

        invalidateOptionsMenu();

        TimeEntry te = new TimeEntry(getArguments().getInt(ISSUE_ID_KEY), getDateInFormat(), Double.parseDouble(mEditTime.getText()), mSpinner.getSelectedItem().getId(), mEditComment.getText());
        RestServiceGenerator.getApiService().addTimeEntry(new TimeEntryRequest(te), new Callback<Object>() {

            @Override
            public void success(Object o, Response response) {
                if (getActivity() == null) {
                    return;
                }

                invalidateOptionsMenu();

                getActivity().onBackPressed();
                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() == null) {
                    return;
                }

                invalidateOptionsMenu();

                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
                Utils.handleError(getChildFragmentManager(), error);

            }
        });
    }

    private void invalidateOptionsMenu() {
        if (getFragmentActivity().getSupportActionBar() != null) {
            getFragmentActivity().getSupportActionBar().invalidateOptionsMenu();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        getArguments().putString(mEditDate.editText().getText().toString());
        getArguments().putInt(SELECTION_KEY, mSpinner.getSpinner().getSelectedItemPosition());
    }

    private String getDateInFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = dateFormat.parse(mEditDate.getText());
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean validate() {
        if (mEditTime.getText().isEmpty()) {
            new MaterialDialog.Builder(getFragmentActivity())
                    .title(R.string.validation_title)
                    .content(R.string.add_time_validation_time)
                    .positiveText(R.string.validation_ok)
                    .autoDismiss(true)
                    .show();
            return false;
        }

        if (mSpinner.getSelectedItem() == null) {
            new MaterialDialog.Builder(getFragmentActivity())
                    .title(R.string.validation_title)
                    .content(R.string.add_time_validation_activity)
                    .positiveText(R.string.validation_ok)
                    .autoDismiss(true)
                    .show();
            return false;
        }

        return true;
    }
}
