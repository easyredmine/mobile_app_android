package cz.ackee.androidskeleton.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.adapter.AssigneesAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.iface.Defaultable;
import cz.ackee.androidskeleton.loader.MembershipLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.IdNameEntity;
import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.model.IssueStatus;
import cz.ackee.androidskeleton.model.request.UpdateIssueRequest;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import cz.ackee.androidskeleton.model.response.IssueStatusesResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.ui.GmailInputView;
import cz.ackee.androidskeleton.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Create new comment screen
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 19/11/15
 **/
public class NewCommentFragment extends BaseFragment {
    public static final String TAG = NewCommentFragment.class.getName();

    public static final int LOADER_MEMBERSHIPS = 1;

    @InjectView(R.id.assignee)
    GmailInputView vAssignee;

    @InjectView(R.id.status)
    GmailInputView vStatus;

    @InjectView(R.id.comment)
    EditText vComment;

    private boolean mIsSendingMessageEnabled = false;

    private Integer mStatusId;
    private Issue mIssue;
    private Integer mAssigneeId;

    private LoaderManager.LoaderCallbacks<BasicResponse<List<IdNameEntity>>> membershipLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<BasicResponse<List<IdNameEntity>>>() {
                @Override
                public Loader<BasicResponse<List<IdNameEntity>>> onCreateLoader(int id, Bundle args) {
                    return new MembershipLoader(getFragmentActivity(), String.valueOf(mIssue.getProject().getId()));
                }

                @Override
                public void onLoadFinished(Loader<BasicResponse<List<IdNameEntity>>> loader, BasicResponse<List<IdNameEntity>> data) {
                    if (getActivity() != null) {
                        populateAssignees(data.getData());
                    }
                }

                @Override
                public void onLoaderReset(Loader<BasicResponse<List<IdNameEntity>>> loader) {

                }
            };

    @Override
    protected String getTitle() {
        return getString(R.string.addComment);
    }

    @Override
    protected void initAB() {
        //noinspection ConstantConditions
        getFragmentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public String getGAName() {
        return null;
    }

    @Override
    public int getPositionInMenu() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment_new, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        mIssue = getArguments().getParcelable(JournalFragment.ISSUE_KEY);
        if (mIssue == null) {
            finish();
            return;
        }

        if (mIssue.getStatus() != null) {
            mStatusId = mIssue.getStatus().getId();
        }

        if (mIssue.getAssignedTo() != null) {
            mAssigneeId = mIssue.getAssignedTo().getId();
        }

        vComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mIsSendingMessageEnabled = s.length() > 0;
                getFragmentActivity().invalidateOptionsMenu();
            }
        });

        vComment.requestFocus();

        populateForms();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_new_comment, menu);

        MenuItem item = menu.findItem(R.id.action_new_comment);
        Drawable icon = item.getIcon();
        if (mIsSendingMessageEnabled) {
            icon.setAlpha(255);
        } else {
            icon.setAlpha(200);
        }
        item.setEnabled(mIsSendingMessageEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_comment) {
            performAddNewComment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performAddNewComment() {
        enableComment(false);

        MaterialDialog progressDialog = new MaterialDialog.Builder(getFragmentActivity()).title(
                R.string.progress_dialog).cancelable(false).content(R.string.journal_dialog_sending_message).progress(true, 0)
                .build();

        progressDialog.show();


        UpdateIssueRequest request = new UpdateIssueRequest(new IssueHash());
        request.issue.notes = String.valueOf(vComment.getText());
        if ((mIssue.getAssignedTo() == null && mAssigneeId != null) || (mIssue.getAssignedTo() != null && !mIssue.getAssignedTo().getId().equals(mAssigneeId))) {
            request.issue.assignedToId = mAssigneeId;
        }

        if ((mIssue.getStatus() == null && mStatusId != null) || (mIssue.getStatus() != null && !mIssue.getStatus().getId().equals(mStatusId)) ) {
            request.issue.statusId = mStatusId;
        }
        sendRequest(request, progressDialog);
    }

    private void sendRequest(UpdateIssueRequest request, final MaterialDialog dialog) {
        RestServiceGenerator.getApiService().updateIssue(String.valueOf(mIssue.getId()), request, new Callback<IssueResponse>() {
            @Override
            public void success(IssueResponse issueResponse, Response response) {
                if (getActivity() == null) {
                    return;
                }
                dialog.hide();
                enableComment(true);

                getFragmentActivity().setResult(Activity.RESULT_OK);
                getFragmentActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() == null) {
                    return;
                }

                enableComment(true);
                dialog.hide();
                Utils.handleError(getChildFragmentManager(), error);
            }
        });
    }

    private void enableComment(boolean sendingMessageEnabled) {
        mIsSendingMessageEnabled = sendingMessageEnabled;
        getFragmentActivity().invalidateOptionsMenu();
    }

    private void initAssignees() {
        Log.d("t", "Init available values assignees");
        getLoaderManager().initLoader(LOADER_MEMBERSHIPS, null, membershipLoaderCallbacks);
    }

    private void initStatus() {
        // we dont have values for statuses - download from api (Redmine)
        RestServiceGenerator.getApiService().getIssueStatuses(new Callback<IssueStatusesResponse>() {
            @Override
            public void success(final IssueStatusesResponse issueResponse, Response response) {
                if (getActivity() == null) {
                    return;
                }
                populateStatus(issueResponse.issueStatuses);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void populateStatus(final List<IssueStatus> entities) {
        vStatus.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1,
                entities));
        if (mStatusId != null) {
            int position = 0;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getId().equals(mStatusId)) {
                    position = i;
                    break;
                }
            }

            // previously selected status was not present in list - set to default
            if (position == 0) {
                vStatus.setSelection(getDefaultIndex(entities));
            }
            vStatus.setSelection(position);
        } else {
            // no status value set - select default value
            vStatus.setSelection(getDefaultIndex(entities));
        }
        vStatus.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStatusId = entities.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateAssignees(final List<IdNameEntity> entities) {
        final List<IdNameEntity> assignees = new ArrayList<>();
//        IdNameEntity emptyEntity = new IdNameEntity();
//        emptyEntity.setName("");
//        emptyEntity.setId(0);
//        assignees.add(emptyEntity);
        // assignees are separated into users and groups
        for (IdNameEntity entity : entities) {
            if (entity.getValues() != null) {
                assignees.add(new IdNameEntity(AssigneesAdapter.GROUP_ID, entity.getName()));
                assignees.addAll(entity.getValues());
            } else {
                IdNameEntity assignee = new IdNameEntity(AssigneesAdapter.ITEM_VALUE, entity.getName());
                assignee.setId(entity.getId());
                assignees.add(assignee);
            }
        }

        vAssignee.setAutoCompleteAdapter(new AssigneesAdapter(getActivity(), assignees));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vAssignee.getAutoComplete().setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                @Override
                public void onDismiss() {
                    AutoCompleteTextView view = vAssignee.getAutoComplete();
                    if (view.getText().length() > 0) {
                        int position = getSelectedIndex(assignees, mAssigneeId);
                        view.setText(assignees.get(position).getName());
                        view.clearFocus();
                    }
                }
            });
        }
        vAssignee.getAutoComplete().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (view.getText().length() == 0) {
                    if (hasFocus) {
                        view.showDropDown();
                    }
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !hasFocus) {
                    int position = getSelectedIndex(assignees, mAssigneeId);
                    view.setText(assignees.get(position).getName());
                    view.clearFocus();
                }
            }
        });
        if (mAssigneeId != null) {
            int position = 0;
            for (int i = 0; i < assignees.size(); i++) {
                if (assignees.get(i).getId() != null && assignees.get(i).getId().equals(mAssigneeId)) {
                    position = i;
                    break;
                }
            }
            vAssignee.setSelection(position);
        }
        onAssigneeSelected(assignees);
    }

    private void onAssigneeSelected(final List<IdNameEntity> assignees) {
        final boolean[] mFirst = {true};
        vAssignee.getAutoComplete().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence name = vAssignee.getAutoComplete().getText();

                if (TextUtils.isEmpty(name)) {
                    return;
                }

                for (IdNameEntity entity : assignees) {
                    if (entity.getName().contentEquals(name)) {
                        mAssigneeId = entity.getId();
                        if (getFragmentActivity().isEasyRedmine() && !mFirst[0]) {
                            populateForms();
                        }
                        mFirst[0] = false;
                    }
                }
            }
        });
    }

    private void populateForms() {
        initAssignees();
        initStatus();
    }

    private int getSelectedIndex(List<IdNameEntity> data, Integer assignedToId) {
        for (int position = 0; position < data.size(); position++) {
            if (assignedToId.equals(data.get(position).getId())) {
                return position;
            }
        }
        return 0;
    }

    private int getDefaultIndex(List<?> list) {
        int defaultIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            Defaultable item = (Defaultable) list.get(i);
            if (item.isDefault()) {
                defaultIndex = i;
                return defaultIndex;
            }
        }
        return defaultIndex;
    }
}