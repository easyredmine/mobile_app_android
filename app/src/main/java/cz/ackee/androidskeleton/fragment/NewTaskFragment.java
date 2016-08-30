package cz.ackee.androidskeleton.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.Configuration;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.adapter.AssigneesAdapter;
import cz.ackee.androidskeleton.adapter.AttachmentAdapter;
import cz.ackee.androidskeleton.adapter.ProjectAdapter;
import cz.ackee.androidskeleton.adapter.VersionsAdapter;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.iface.UploadTaskCallbacks;
import cz.ackee.androidskeleton.loader.MembershipLoader;
import cz.ackee.androidskeleton.loader.SearchProjectLoader;
import cz.ackee.androidskeleton.loader.VersionsLoader;
import cz.ackee.androidskeleton.loader.base.BasicResponse;
import cz.ackee.androidskeleton.model.CustomFieldValues;
import cz.ackee.androidskeleton.model.IdNameEntity;
import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.model.IssuePriority;
import cz.ackee.androidskeleton.model.IssueStatus;
import cz.ackee.androidskeleton.model.NameValueEntity;
import cz.ackee.androidskeleton.model.Project;
import cz.ackee.androidskeleton.model.Upload;
import cz.ackee.androidskeleton.model.request.CheckFieldIssueRequest;
import cz.ackee.androidskeleton.model.request.CreateIssueRequest;
import cz.ackee.androidskeleton.model.request.UpdateIssueRequest;
import cz.ackee.androidskeleton.model.response.CustomField;
import cz.ackee.androidskeleton.model.response.EasyRedmineValidationResponse;
import cz.ackee.androidskeleton.model.response.IssuePriorityResponse;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import cz.ackee.androidskeleton.model.response.IssueStatusesResponse;
import cz.ackee.androidskeleton.model.response.ProjectResponse;
import cz.ackee.androidskeleton.model.response.UploadAttachmentResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.ui.GmailInputView;
import cz.ackee.androidskeleton.ui.GmailInputView.OnCustomValueChangeListener;
import cz.ackee.androidskeleton.utils.TaskUtils;
import cz.ackee.androidskeleton.utils.TimeUtils;
import cz.ackee.androidskeleton.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment for new task
 *
 * @author David Bilik [david.bilik@ackee.cz]
 * @since 18. 2. 2015
 */
public class NewTaskFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<BasicResponse<List<Project>>>, OnCustomValueChangeListener, UploadProgressListener {
    public static final String TAG = NewTaskFragment.class.getName();
    public static final int PICK_IMAGE_ATTACHMENT = 1;
    public static final int LOADER_SEARCH_PROJECTS = 0;
    public static final int LOADER_MEMBERSHIPS = 1;
    public static final String IS_SAVED_KEY = "is_saved";
    private static final String PROJECT_ID = "project_id";
    private static final int LOADER_VERSIONS = 2;
    private static final String ISSUE_ID = "issue_id";
    public static int attachmentCounter = 0;

    // Boolean for checking whether project view adapter
    // has been initialized to avoid onItemSelect signal
    boolean areProjectValuesInited = false;

    IssueHash mIssueHash;
    Issue mIssue;
    AttachmentAdapter mUploadAdapter;

    int mProjectId;
    int mIssueId;

    List<LinearLayout> progressBars;

    UploadTaskCallbacks mCallback;
    @InjectView(R.id.project)
    GmailInputView vProject;
    @InjectView(R.id.priority)
    GmailInputView vPriority;
    @InjectView(R.id.status)
    GmailInputView vStatus;
    @InjectView(R.id.tracker)
    GmailInputView vTracker;
    @InjectView(R.id.category)
    GmailInputView vCategory;
    @InjectView(R.id.assignee)
    GmailInputView vAssignee;
    @InjectView(R.id.done)
    GmailInputView vDone;
    @InjectView(R.id.milestone)
    GmailInputView vMilestone;
    @InjectView(R.id.subject)
    GmailInputView vSubject;
    @InjectView(R.id.description)
    GmailInputView vDescription;
    @InjectView(R.id.startDate)
    GmailInputView vStartDate;
    @InjectView(R.id.dueDate)
    GmailInputView vDueDate;
    @InjectView(R.id.estimatedTime)
    GmailInputView vEstimatedTime;
    @InjectView(R.id.progressWrapper)
    LinearLayout progressWrapper;
    @InjectView(R.id.newAttachmentsWrapper)
    LinearLayout newAttachmentsWrapper;
    MaterialDialog progressDialog;
    MaterialDialog waitDialog;
    private boolean mShouldSave;
    private LoaderManager.LoaderCallbacks<BasicResponse<List<IdNameEntity>>> membershipLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<BasicResponse<List<IdNameEntity>>>() {
                @Override
                public Loader<BasicResponse<List<IdNameEntity>>> onCreateLoader(int id, Bundle args) {
                    return new MembershipLoader(getFragmentActivity(), String.valueOf(mProjectId));
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
    private LoaderManager.LoaderCallbacks<BasicResponse<List<IdNameEntity>>> versionsLoaderCallbacks = new LoaderManager.LoaderCallbacks<BasicResponse<List<IdNameEntity>>>() {
        @Override
        public Loader<BasicResponse<List<IdNameEntity>>> onCreateLoader(int id, Bundle args) {
            return new VersionsLoader(getContext(), String.valueOf(mProjectId));
        }

        @Override
        public void onLoadFinished(Loader<BasicResponse<List<IdNameEntity>>> loader, BasicResponse<List<IdNameEntity>> data) {
            if (getActivity() != null) {
                populateVersions(data.getData());
            }
        }

        @Override
        public void onLoaderReset(Loader<BasicResponse<List<IdNameEntity>>> loader) {

        }
    };
    private boolean isInitialLoadingCompleted = false;

    public static NewTaskFragment newInstance(Bundle args) {
        NewTaskFragment ntf = new NewTaskFragment();
        ntf.setArguments(args);
        return ntf;
    }

    @Nullable
    public static String getRealPathFromURI(Context context, Uri contentURI) {
        Cursor cursor = null;
        try {
            Uri newUri = TaskUtils.handleImageUri(contentURI);
            cursor = context.getContentResolver().query(newUri, null, null, null, null);
            //noinspection ConstantConditions
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (contentURI.toString().startsWith("content://com.google.android.gallery3d")) {
                columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            }
            return cursor.getString(columnIndex);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI: ", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (UploadTaskCallbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement UploadTaskCallbacks");
        }
    }

    /**
     * Set the callback to null so we don't accidentally leak the Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.newTask);
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
        return Configuration.MENU_POS_NEW_ISSUE;
    }

    @Override
    public boolean hasHamburgerMenu() {
        return false;
    }

    public boolean isEditing() {
        return mIssueId > 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mShouldSave = true;
        if (getArguments() != null) {
            mProjectId = getArguments().getInt(PROJECT_ID);
            mIssueId = getArguments().getInt(ISSUE_ID);
        }
        mIssueHash = new IssueHash();
        if (getArguments() != null && getArguments().getBoolean(IS_SAVED_KEY)) {
            mIssueHash = fillIssueHashFromFile();
        }
        mUploadAdapter = new AttachmentAdapter(getFragmentActivity());
    }

    private IssueHash fillIssueHashFromFile() {
        return Utils.getIssueHashFromFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_new, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        initProjects();
        initAttachments();
    }

    @SuppressWarnings("ConstantConditions")
    private void initCustomFields(List<CustomFieldValues> availableCustomFieldsValues) {
        if (availableCustomFieldsValues == null) {
            getView().findViewById(R.id.txtCustomFieldsTitle).setVisibility(View.GONE);
            return;
        }
        LinearLayout layoutCustomFields = ButterKnife.findById(getView(), R.id.layoutCustomFields);
        layoutCustomFields.removeAllViews();
        ArrayMap<Integer, CustomFieldValues> map = new ArrayMap<>();
        for (CustomFieldValues cv : availableCustomFieldsValues) {
            map.put(cv.getId(), cv);
        }
        for (CustomField cf : mIssue.getCustomFields()) {
            CustomFieldValues cfv = map.get(cf.getId());
            GmailInputView giv = null;
            switch (cf.getFieldFormat()) {

                case "link":
                case "text":
                case "string":
                case "easy_google_map_address":
                case "email":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    giv.setText(TaskUtils.getCFValueString(cf.getValues()));

                    break;
                case "int":
                case "amount":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    giv.setText(TaskUtils.getCFValueString(cf.getValues()));
                    giv.initInputStyle(GmailInputView.INPUT_TYPE_NUMBER);
                    break;
                case "float":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    giv.setText(TaskUtils.getCFValueString(cf.getValues()));
                    giv.initInputStyle(GmailInputView.INPUT_TYPE_DECIMAL);
                    break;

                case "date":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    String value = "";
                    if (cf.getValues() != null && cf.getValues().size() > 0) {
                        value = cf.getValues().get(0);
                    }
                    giv.setText(TimeUtils.getTimeFormatted(value, TimeUtils.ATOM_FORMAT_DATE, TimeUtils.DUE_DATE));
//                    giv.setText(getCFValueString(cf.getValues()));
                    giv.setInputCompontent(GmailInputView.COMPONENT_DATE);
                    break;
                case "bool":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    giv.setInputCompontent(GmailInputView.COMPONENT_SWITCH);
                    String val = TaskUtils.getCFValueString(cf.getValues());
                    boolean boolValue = val.equals("1");
                    giv.setSwitchValue(boolValue);
                    break;
                case "list":
                case "user":
                case "version":
                case "value_tree":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    if (cf.getMultiple()) {

                        giv.setText(TaskUtils.getCFValueString(cf.getValues()));
                        giv.setItems(cfv.getValues());
                        giv.setInputCompontent(GmailInputView.COMPONENT_MULTIPLE_ITEMS);
//                        giv.showCheckedValues();
                    } else {
                        giv.setInputCompontent(GmailInputView.COMPONENT_SPINNER);
                        List<String> items = Utils.getStringList((ArrayList<NameValueEntity>) cfv.getValues());
                        items.add(0, "");
                        giv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1, items));
                        for (int i = 0; i < cfv.getValues().size(); i++) {
                            if (cfv.getValues().get(i).getValue().equals(TaskUtils.getCFValueString(cf.getValues()))) {
                                giv.getSpinner().setSelection(i + 1);
                                break;
                            }
                        }
                    }
                    break;
                case "easy_percent":
                    giv = new GmailInputView(getActivity());
                    giv.setTitle(cf.getName());
                    giv.setText(TaskUtils.getCFValueString(cf.getValues()));
                    giv.setInputCompontent(GmailInputView.COMPONENT_SPINNER);
                    giv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1, getResources().getStringArray(R.array.doneRatio)));
                    int percents = 0;
                    String valInt = TaskUtils.getCFValueString(cf.getValues());
                    try {
                        percents = (int) (Double.parseDouble(valInt) / 10);
                    } catch (Exception ignored) {
                    }

                    giv.getSpinner().setSelection(percents);
                    break;
            }
            if (giv != null) {
                giv.setCustomField(cf, cfv);
                giv.setValueChangeListener(this);
                if (cf.getMultiple()) {
                    giv.showCheckedValues();
                }
                layoutCustomFields.addView(giv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

        }
    }

    private void initAvailableValues() {
        progressDialog = new MaterialDialog.Builder(getFragmentActivity()).title(
                R.string.progress_dialog).cancelable(false).content(R.string.please_wait).progress(true, 0)
                .build();

        if (getFragmentActivity().isEasyRedmine()) {
            progressDialog.show();
            getAvailableValuesForNewTask();

        } else {
            Log.d(TAG, "we don't have any valid options");
            populateForms(null); // we don't have any valid options
        }
    }

    private void getAvailableValuesForNewTask() {
        // user is signed into Easy redmine, available values from Easy redmine api will be
        // populated
        RestServiceGenerator.createApiDescription().getAvailableEasyIssue(new CheckFieldIssueRequest(mIssueHash, isInitialLoadingCompleted),
                String.valueOf(mProjectId), new Callback<EasyRedmineValidationResponse>() {
                    @Override
                    public void success(EasyRedmineValidationResponse easyRedmineValidationResponse,
                                        Response response) {
                        isInitialLoadingCompleted = true;
                        progressDialog.hide();
                        mIssue = easyRedmineValidationResponse.formAttributes.issue;
                        TaskUtils.setIssueHash(mIssueHash, mProjectId, mIssue, getFragmentActivity().isEasyRedmine());
                        mProjectId = mIssueHash.projectId;
                        populateForms(easyRedmineValidationResponse); // we have valid options for this form
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Utils.handleError(getChildFragmentManager(), error);
                        isInitialLoadingCompleted = true;
                        progressDialog.hide();
                    }
                });
    }

    @SuppressWarnings("ConstantConditions")
    private void populateForms(EasyRedmineValidationResponse easyRedmineValidationResponse) {

        initTrackers(easyRedmineValidationResponse == null ? null :
                easyRedmineValidationResponse.formAttributes.availableTrackers);
        initCategories(easyRedmineValidationResponse == null ? null :
                easyRedmineValidationResponse.formAttributes.availableCategories);
        Log.d("t", "Init available values");

        initDone();
        initPriority(easyRedmineValidationResponse == null ? null :
                easyRedmineValidationResponse.formAttributes.availablePriorities);
        initStatus(easyRedmineValidationResponse == null ? null :
                easyRedmineValidationResponse.formAttributes.availableStatuses);
        initAssignees(easyRedmineValidationResponse == null ? null :
                easyRedmineValidationResponse.formAttributes.availableAssignees);
        initVersions(easyRedmineValidationResponse == null ? null :
                easyRedmineValidationResponse.formAttributes.availableFixedVersions);

        // Sets value to project
        if (mIssueHash.projectId != null) {
            ProjectAdapter adapter = (ProjectAdapter) vProject.getSpinner().getAdapter();
            if (adapter != null) {
                int pos = adapter.getPositionByProjectId(mIssueHash.projectId);
                if (pos != -1) {
                    vProject.setSelection(pos);
                }
            }
        }

        TaskUtils.initEditText(mIssueHash, vSubject, TaskUtils.EDIT_SUBJECT, false, vStartDate);
        TaskUtils.initEditText(mIssueHash, vDescription, TaskUtils.EDIT_DESCRIPTION, false, vStartDate);
        TaskUtils.initEditText(mIssueHash, vEstimatedTime, TaskUtils.EDIT_ESTIMATED_TIME, false, vStartDate);
        TaskUtils.initEditText(mIssueHash, vStartDate, TaskUtils.EDIT_START_DATE, false, vStartDate);
        TaskUtils.initEditText(mIssueHash, vDueDate, TaskUtils.EDIT_DUE_DATE, false, vStartDate);
        if (getFragmentActivity().isEasyRedmine()) {
            initCustomFields(easyRedmineValidationResponse.formAttributes.availableCustomFieldsValues);
        } else {
            getView().findViewById(R.id.txtCustomFieldsTitle).setVisibility(View.GONE);
            getView().findViewById(R.id.layoutCustomFields).setVisibility(View.GONE);

        }
    }

    private void initAssignees(List<IdNameEntity> entities) {
        if (entities != null) {
            // we have values for available assignees (EasyRedmine)
            populateAssignees(entities);
        } else {
            Log.d("t", "Init available values assignees");
            getLoaderManager().restartLoader(LOADER_MEMBERSHIPS, null, membershipLoaderCallbacks);
        }
    }

    private void initVersions(List<IdNameEntity> versions) {
        if (versions != null) {
            populateVersions(versions);
        } else {
            getLoaderManager().restartLoader(LOADER_VERSIONS, null, versionsLoaderCallbacks);
        }
    }

    @Override
    public void setShouldSend(boolean b) {
        mShouldSave = b;
    }

    @Override
    public void showError(RetrofitError error, String tag) {
        LinearLayout progress = (LinearLayout) progressWrapper.findViewWithTag(tag);
        if (progress != null) {
            ((ViewGroup) progress.getParent()).removeView(progress);
        }
        if (mIssueHash.uploads == null || mIssueHash.uploads.size() == 0) {
            //noinspection ConstantConditions
            getView().findViewById(R.id.txtAttachmentsTitle).setVisibility(View.GONE);
        }
        Utils.handleError(getChildFragmentManager(), error);
    }

    private void populateVersions(List<IdNameEntity> data) {
        if (data.size() == 0) {
            vMilestone.setVisibility(View.GONE);
        } else {
            vMilestone.setVisibility(View.VISIBLE);
        }

        final List<IdNameEntity> versions = new ArrayList<>();
        // Empty one :*
        versions.add(new IdNameEntity());
        versions.addAll(data);

        vMilestone.setAdapter(new VersionsAdapter(getActivity(), versions));
        vMilestone.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IdNameEntity entity = (IdNameEntity) vMilestone.getSpinner().getAdapter().getItem(position);
                if (entity.getId() != null) {
                    mIssueHash.fixedVersionId = entity.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (mIssueHash.fixedVersionId != null) {
            for (IdNameEntity entity : versions) {
                if (mIssueHash.fixedVersionId.equals(entity.getId())) {
                    vMilestone.setSelection(versions.indexOf(entity));
                }
            }
        }
    }

    private void populateAssignees(final List<IdNameEntity> entities) {
        final List<IdNameEntity> assignees = new ArrayList<>();

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
                        int position = TaskUtils.getSelectedIndex(assignees, mIssueHash.assignedToId);
                        if (position >= 0) {
                            view.setText(assignees.get(position).getName());
                            view.clearFocus();
                        }
                    }
                }
            });
        }
        vAssignee.getAutoComplete().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (view.getText().length() == 0) {
                    if (hasFocus) {
                        view.showDropDown();
                    }
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !hasFocus) {
                    int position = TaskUtils.getSelectedIndex(assignees, mIssueHash.assignedToId);
                    if (position >= 0) {
                        view.setText(assignees.get(position).getName());
                        view.clearFocus();
                    }
                }
                if (hasFocus) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            int len = view.getText().length();
                            view.setSelection(len);
                        }
                    });

                }
            }
        });
        if (mIssueHash.assignedToId != null) {
            int position = 0;
            for (int i = 0; i < assignees.size(); i++) {
                if (assignees.get(i).getId() != null && assignees.get(i).getId().equals(mIssueHash.assignedToId)) {
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
                        mIssueHash.assignedToId = entity.getId();
                        if (getFragmentActivity().isEasyRedmine() && !mFirst[0]) {
                            initAvailableValues();
                        }
                        mFirst[0] = false;
                    }
                }
            }
        });
    }

    private List<CustomField> getFilledCustomFields() {
        List<CustomField> cf = new ArrayList<>();
        //noinspection ConstantConditions
        LinearLayout layoutCustomFields = ButterKnife.findById(getView(), R.id.layoutCustomFields);
        for (int i = 0; i < layoutCustomFields.getChildCount(); i++) {
            GmailInputView giv = (GmailInputView) layoutCustomFields.getChildAt(i);
            cf.add(giv.getCustomField());
        }
        return cf;
    }

    @SuppressWarnings("ConstantConditions")
    private void initAttachments() {
        newAttachmentsWrapper.removeAllViews();
        progressWrapper.removeAllViews();

        for (LinearLayout progress : getProgressBars()) {
            try {
                ((LinearLayout) progress.getParent()).removeView(progress);
                progressWrapper.addView(progress);
            } catch (NullPointerException ignored) {

            }
        }

        int attachmentCount = 0;
        for (Upload upload : getUploads()) {
            addAttachmentToList(upload, attachmentCount++);
        }
        if (attachmentCount == 0) {
            getView().findViewById(R.id.txtAttachmentsTitle).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.txtAttachmentsTitle).setVisibility(View.VISIBLE);
        }
    }

    private List<LinearLayout> getProgressBars() {
        if (progressBars == null) {
            progressBars = new ArrayList<>();
        }
        return progressBars;
    }

    private void addAttachmentToList(final Upload upload, final int count) {
        LinearLayout item = (LinearLayout) getLayoutInflater(null).inflate(
                R.layout.list_item_attachment, newAttachmentsWrapper, false);
        item.setTag(count);
        ((TextView) item.findViewById(R.id.attachmentTitle)).setText(upload.filename);
        item.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getFragmentActivity())
                        .title(R.string.confirm_remove_attachment)
                        .content(R.string.confirm_remove_attachment_content)
                        .positiveText(R.string.action_remove_attachment).negativeText(R.string.cancel).
                        onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                getUploads().remove(upload);
                                View view = newAttachmentsWrapper.findViewWithTag(count);
                                newAttachmentsWrapper.removeView(view);
                            }
                        }).show();
            }
        });
        newAttachmentsWrapper.addView(item);
    }

    private void initPriority(List<IssuePriority> entities) {
        if (entities != null) {
            // we have values for statuses (EasyRedmine)
            populatePriorities(entities);
        } else {
            // we don't have values for statuses - download from api (Redmine)
            RestServiceGenerator.getApiService().getIssuePriorities(
                    new Callback<IssuePriorityResponse>() {
                        @Override
                        public void success(final IssuePriorityResponse priorityResponse, Response response) {
                            if (getActivity() == null) {
                                return;
                            }
                            populatePriorities(priorityResponse.getIssuePriorities());
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        }
    }

    private void populatePriorities(final List<IssuePriority> entities) {
        vPriority.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1,
                entities));
        if (mIssueHash.priorityId != null) {
            int position = 0;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getId().equals(mIssueHash.priorityId)) {
                    position = i;
                    break;
                }
            }

            // previously selected status was not present in list - set to default
            if (position == 0) {
                vPriority.setSelection(TaskUtils.getDefaultIndex(entities));
            }
            vPriority.setSelection(position);
        } else {
            // no status value set - select default value
            vPriority.setSelection(TaskUtils.getDefaultIndex(entities));
        }
        final boolean[] mFirst = {true};
        vPriority.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIssueHash.priorityId = entities.get(position).getId();
                if (getFragmentActivity().isEasyRedmine() && !mFirst[0]) {
                    initAvailableValues();
                }
                mFirst[0] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initProjects() {
        getLoaderManager().restartLoader(LOADER_SEARCH_PROJECTS, null, NewTaskFragment.this)
                .forceLoad();
    }

    private void initDone() {
        vDone.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.doneRatio)));
        if (mIssueHash.doneRatio != null) {

            int position = 0;
            for (String s : getResources().getStringArray(R.array.doneRatio)) {
                if (s.equals(String.valueOf(mIssueHash.doneRatio + "%"))) {
                    break;
                }
                position++;
            }
            vDone.setSelection(position);
        }
        vDone.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String percent = getResources().getStringArray(R.array.doneRatio)[position];
                mIssueHash.doneRatio = Integer.parseInt(percent.substring(0, percent.length() - 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initStatus(List<IssueStatus> entities) {
        if (entities != null) {
            // we have values for statuses (EasyRedmine)
            populateStatus(entities);
        } else {
            // we don't have values for statuses - download from api (Redmine)

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

    }

    private void populateStatus(final List<IssueStatus> entities) {
        vStatus.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1,
                entities));
        if (mIssueHash.statusId != null) {
            int position = 0;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getId().equals(mIssueHash.statusId)) {
                    position = i;
                    break;
                }
            }

            // previously selected status was not present in list - set to default
            if (position == 0) {
                vStatus.setSelection(TaskUtils.getDefaultIndex(entities));
            }
            vStatus.setSelection(position);
        } else {
            // no status value set - select default value
            vStatus.setSelection(TaskUtils.getDefaultIndex(entities));
        }
        vStatus.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIssueHash.statusId = entities.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_task:

                String title = getString(R.string.creating_task);

                if (isEditing()) {
                    title = getString(R.string.edit_task);
                }
                if (getFragmentActivity().isEasyRedmine()) {
                    mIssueHash.customFields = getFilledCustomFields();
                }
                waitDialog = new MaterialDialog.Builder(getFragmentActivity()).title(title).cancelable(
                        false).content(R.string.please_wait).progress(true, 0).build();

                if (isEditing()) {
                    requestEditTask();
                } else {
                    requestNewTask();
                }
                break;
            case R.id.action_add_attachment:
                pickImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNewTask() {
        waitDialog.show();
        RestServiceGenerator.getApiService().createIssue(new CreateIssueRequest(mIssueHash),
                new Callback<IssueResponse>() {
                    @Override
                    public void success(IssueResponse o, Response response) {
                        waitDialog.dismiss();
                        mShouldSave = false;
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        waitDialog.dismiss();

                        Utils.handleError(getChildFragmentManager(), error);
                    }
                });
    }

    private void requestEditTask() {
        waitDialog.show();
        RestServiceGenerator.getApiService().updateIssue(String.valueOf(mIssueId),
                new UpdateIssueRequest(mIssueHash), new Callback<IssueResponse>() {
                    @Override
                    public void success(IssueResponse o, Response response) {
                        waitDialog.dismiss();
                        mShouldSave = false;
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        waitDialog.dismiss();
                        Utils.handleError(getChildFragmentManager(), error);
                    }
                });
    }

    @Override
    public Loader<BasicResponse<List<Project>>> onCreateLoader(int id, Bundle args) {
        return new SearchProjectLoader(getFragmentActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<BasicResponse<List<Project>>> loader,
                               final BasicResponse<List<Project>> data) {
        if (getActivity().isFinishing()) {
            return;
        }
        if (!data.getData().isEmpty()) {
            vProject.setAutoCompleteAdapter(new ProjectAdapter(getActivity(), data.getData()));
            if (mProjectId > 0) {
                int defaultIndex = getDefaultProject(data.getData());
                vProject.setSelection(defaultIndex);
            } else {
                mProjectId = data.getData().get(0).getId();
                vProject.setSelection(0);
            }
            mIssueHash.projectId = mProjectId;
            vProject.getAutoComplete().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mProjectId = ((Project) view.getTag()).getId();
                    mIssueHash.projectId = mProjectId;
                    initAvailableValues();
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                vAssignee.getAutoComplete().setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        AutoCompleteTextView view = vAssignee.getAutoComplete();
                        if (view.getText().length() > 0) {
                            int defaultIndex = getDefaultProject(data.getData());
                            vProject.setSelection(defaultIndex);
                        }
                    }
                });
            }

            vProject.getAutoComplete().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    final AutoCompleteTextView view = (AutoCompleteTextView) v;
                    if (view.getText().length() == 0) {
                        if (hasFocus) {
                            view.showDropDown();
                        }
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !hasFocus) {
                        int defaultIndex = getDefaultProject(data.getData());
                        int objectsCount = vProject.getAutoComplete().getAdapter().getCount();
                        if (objectsCount < defaultIndex || (objectsCount == 0 && defaultIndex == 0)) {
                            for (int i = 0; i < data.getData().size(); i++) {
                                if (data.getData().get(i).getId() == mIssueHash.projectId) {
                                    vProject.getAutoComplete().setText(data.getData().get(i).getName());
                                    break;
                                }
                            }
                        } else {
                            vProject.setSelection(defaultIndex);
                        }
                    }
                    if (hasFocus) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                int len = view.getText().length();
                                view.setSelection(len);
                            }
                        });

                    }
                }
            });

            vProject.clearFocus();

        } else {
            // NO PROJECTS
            Log.d(TAG, "NO PROJECTS");
        }
        initAvailableValues();
        areProjectValuesInited = false;

        Log.d(TAG, "on load finished");
    }

    private void initTrackers(List<IdNameEntity> entities) {
        if (entities != null) {
            // we have values for statuses (EasyRedmine)
            populateTrackers(entities);
        } else {
            // we don't have values for statuses - download from api (Redmine)
            RestServiceGenerator.getApiService().getProject(String.valueOf(mProjectId),
                    TaskUtils.getProjectQueryMap(), new Callback<ProjectResponse>() {
                        @Override
                        public void success(final ProjectResponse projectResponse, Response response) {
                            if (getActivity() == null) {
                                return;
                            }
                            populateTrackers(projectResponse.getProject().getTrackers());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "Failure");
                        }
                    });
        }
    }

    private void populateTrackers(final List<IdNameEntity> entities) {
        vTracker.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1,
                entities));
        if (mIssueHash.trackerId != null) {
            int position = 0;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getId().equals(mIssueHash.trackerId)) {
                    position = i;
                    break;
                }
            }

            vTracker.setSelection(position);
        }
        final boolean[] mFirst = {true};

        vTracker.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIssueHash.trackerId = entities.get(position).getId();
                if (getFragmentActivity().isEasyRedmine() && !mFirst[0]) {
                    initAvailableValues();
                }
                mFirst[0] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCategories(List<IdNameEntity> entities) {
        if (entities != null) {
            // we have values for categories (EasyRedmine)
            populateCategories(entities);
        } else {
            // we don't have values for categories - download from api (Redmine)
            RestServiceGenerator.getApiService().getProject(String.valueOf(mProjectId),
                    TaskUtils.getProjectQueryMap(), new Callback<ProjectResponse>() {
                        @Override
                        public void success(final ProjectResponse projectResponse, Response response) {
                            if (getActivity() == null) {
                                return;
                            }

                            populateCategories(projectResponse.getProject().getIssueCategories());
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }

                    });
        }
    }

    private void populateCategories(final List<IdNameEntity> entities) {
        if (entities.size() > 0) {
            vCategory.setVisibility(View.VISIBLE);
        } else {
            vCategory.setVisibility(View.GONE);
        }
        if (entities.size() > 0 && entities.get(0).getId() != null) {
            entities.add(0, new IdNameEntity());
        }

        vCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_1,
                entities));
        if (mIssueHash.categoryId != null) {
            int position = 0;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getId().equals(mIssueHash.categoryId)) {
                    position = i;
                    break;
                }
            }

            vCategory.setSelection(position);
        }

        vCategory.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIssueHash.categoryId = entities.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getDefaultProject(List<Project> data) {
        int index = 0;
        for (Project p : data) {
            if (p.getId() == mProjectId) {
                return index;
            }
            index++;
        }
        return index;
    }

    @Override
    public void onLoaderReset(Loader<BasicResponse<List<Project>>> loader) {

    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_ATTACHMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_ATTACHMENT && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            String fileUri = getRealPathFromURI(data.getData());
            File file = new File(fileUri);
            ContentResolver cR = getFragmentActivity().getContentResolver();
            String contentType = cR.getType(data.getData());
            String tag = file.getName() + (++attachmentCounter);
            addProgressBar(file.getName(), tag, 0);
            mCallback.startUpload(file, tag, contentType);
            //noinspection ConstantConditions
            getView().findViewById(R.id.txtAttachmentsTitle).setVisibility(View.VISIBLE);
            getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getView().getViewTreeObserver().removeOnPreDrawListener(this);
                    final ScrollView sv = (ScrollView) getView().findViewById(R.id.scrollView);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });


                    return false;
                }
            });
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        return getRealPathFromURI(getActivity(), contentURI);
    }

    private void addProgressBar(String filename, String tag, int progressValue) {
        Log.d(TAG, "UPLOAD - adding progressbar with tag " + tag);
        LinearLayout progress = (LinearLayout) LayoutInflater.from(getFragmentActivity()).inflate(
                R.layout.widget_progressbar_upload, progressWrapper, false);
        TextView progressTitle = (TextView) progress.findViewById(R.id.progressValue);
        TextView title = (TextView) progress.findViewById(R.id.filename);
        title.setText(filename);
        progressTitle.setText(progressValue + "%");
        progress.setTag(tag);
        progressWrapper.addView(progress);
        progressBars.add(progress);
    }

    public List<Upload> getUploads() {
        if (mIssueHash.uploads == null) {
            mIssueHash.uploads = new ArrayList<>();
        }
        return mIssueHash.uploads;
    }

    @Override
    public void onProgressUpdate(String filename, String tag, int progressValue) {
        LinearLayout progress = (LinearLayout) progressWrapper.findViewWithTag(tag);

        if (progress == null) {
            Log.d(TAG, "UPLOAD - progressbar add for tag " + tag + " null=" + progressWrapper);
            addProgressBar(filename, tag, progressValue);
        } else {
            Log.d(TAG,
                    "UPLOAD - progressbar add for tag " + tag + " ch=" + progressWrapper.getChildCount());
        }
    }

    @Override
    public void onUploadComplete(String filename, String tag, Object response) {
        UploadAttachmentResponse uploadAttachmentResponse;
        try {
            uploadAttachmentResponse = (UploadAttachmentResponse) response;
        } catch (ClassCastException e) {
            return;
        }
        LinearLayout progress = (LinearLayout) progressWrapper.findViewWithTag(tag);
        if (progress != null) {
            ((LinearLayout) progress.getParent()).removeView(progress);
        }
        Log.d(TAG, "UPLOAD - UI fragment update COMPLETE " + tag + " ");
        if (mIssueHash != null) {
            getUploads().add(uploadAttachmentResponse.upload);
            addAttachmentToList(uploadAttachmentResponse.upload, getUploads().size() + 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getFragmentActivity().isEasyRedmine() && mIssue != null) {
            mIssue.setCustomFields(getFilledCustomFields());
        }
        if (mShouldSave) {
            Utils.saveIssueHashToFile(mIssueHash);
        } else {
            //noinspection ResultOfMethodCallIgnored
            Utils.getTempIssueHashFile().delete();
        }
    }

    @Override
    public void onValueChanged() {
        if (mIssue != null && mIssueHash != null) {
            mIssue.setCustomFields(getFilledCustomFields());
            mIssueHash.customFields = getFilledCustomFields();
        }
    }
}