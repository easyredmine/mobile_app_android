package cz.ackee.androidskeleton.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.NewTaskActivity;
import cz.ackee.androidskeleton.event.IssueDeletedEvent;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.model.Attachement;
import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.model.IssueRelation;
import cz.ackee.androidskeleton.model.Journal;
import cz.ackee.androidskeleton.model.response.CustomField;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.ui.CollapsibleView;
import cz.ackee.androidskeleton.utils.RedmineUtils;
import cz.ackee.androidskeleton.utils.Storage;
import cz.ackee.androidskeleton.utils.TimeUtils;
import cz.ackee.androidskeleton.utils.Utils;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Detail of task
 *
 * @author David Bilik[david.bilik@ackee.cz]
 * @since 18/02/2015
 */
public class TaskDetailFragment extends BaseFragment {
    public static final String TAG = TaskDetailFragment.class.getName();
    private static final String ISSUE_KEY = "issue";
    private static final String ISSUE_ID = "issue_id";
    private static final String PROJECT_ID = "project_id";
    private static final String PROJECT_PARCELABLE = "project_parcelable";
    private static final String INIT_KEY = "init";
    private static final String POSITION_KEY = "position";
    @InjectView(R.id.vName)
    TextView vName;

    private long enqueue;
    private DownloadManager dm;

    //    MenuItem mMenuJournal;
    private Issue mIssue;

    public static TaskDetailFragment newInstance(Issue i) {
        Bundle args = new Bundle();
        args.putParcelable(ISSUE_KEY, i);
        TaskDetailFragment tdf = new TaskDetailFragment();
        tdf.setArguments(args);
        return tdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIssue = getArguments().getParcelable(ISSUE_KEY);
    }

    @Override
    protected String getTitle() {
        if (getIssue() != null && getIssue().getProject() != null) {
            return getIssue().getProject().getName();
        }

        return getFragmentActivity().isEasyRedmine() ? getString(R.string.title_easy_redmine) : getString(R.string.title_redmine);
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
        inflater.inflate(R.menu.task_detail, menu);
//        mMenuJournal = menu.findItem(R.id.action_journal);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        reloadIssue();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_journal:
//                getFragmentActivity().replaceFragment(JournalFragment.newInstance(getIssue()));
//                break;
            case R.id.action_add_time:
                getFragmentActivity().replaceFragment(AddTimeFragment.newInstance(getIssue().getId(), getIssue().getSubject()));
                break;
            case R.id.action_edit:
                Bundle bundle = new Bundle();
                bundle.putInt(ISSUE_ID, getIssue().getId());
                if (getIssue().getProject() != null) {
                    bundle.putInt(PROJECT_ID, getIssue().getProject().getId());
                    //  if(getFragmentActivity().isEasyRedmine() != true) {
                    bundle.putParcelable(PROJECT_PARCELABLE, getIssue());
                    //}
                }
                NewTaskActivity.startForResult(getFragmentActivity(), bundle);
                break;
            case R.id.action_see_on_web:
                Utils.startBrowserApp(getFragmentActivity(), RedmineUtils.getRedmineWebLink(getIssue(),
                        Integer.toString(getIssue().getId())));
                break;
            case R.id.action_delete:
                new MaterialDialog.Builder(getFragmentActivity()).title(R.string.dialog_title_delete_task)
                        .content(R.string.dialog_content_delete_task).positiveText(R.string.agree).negativeText(
                        R.string.cancel).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteIssue();
                    }
                }).autoDismiss(true).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reloadIssue() {
        RestServiceGenerator.createApiDescription().getIssueDetail(String.valueOf(mIssue.getId()),
                getIssueQueryMap(), new Callback<IssueResponse>() {
                    @Override
                    public void success(IssueResponse issueResponse, Response response) {
                        if (getActivity() == null) {
                            return;
                        }
                        mIssue = issueResponse.getIssue();
                        if (mIssue.getProject() != null) {
                            setTitle(mIssue.getProject().getName());
                        }
                        initViews();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (getActivity() == null || getView() == null || getActivity().isFinishing()) {
                            return;
                        }
                        Utils.handleError(getChildFragmentManager(), error);
                    }
                });
    }

    private void deleteIssue() {
        RestServiceGenerator.createApiDescription().deleteIssue(String.valueOf(mIssue.getId()),
                new Callback<IssueResponse>() {
                    @Override
                    public void success(IssueResponse issueResponse, Response response) {
                        if (getFragmentActivity() == null) {
                            return;
                        }
                        EventBus.getDefault().post(new IssueDeletedEvent(mIssue.getId()));
                        getFragmentActivity().onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (getFragmentActivity() == null) {
                            return;
                        }
                        Utils.handleError(getChildFragmentManager(), error);
                    }
                });
    }

    public Issue getIssue() {
        if (mIssue == null) {
            return getArguments().getParcelable(ISSUE_KEY);
        } else {
            return mIssue;
        }
    }

    public void setIssue(Issue issue) {
        this.mIssue = issue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

    }

    @Override
    public void onResume() {
        super.onResume();
        reloadIssue();
    }

    private void initViews() {
        if (getActivity() == null || getView() == null) {
            return;
        }
        vName.setText(getIssue().getSubject());

        final CollapsibleView collapsibleInfo = (CollapsibleView) getView().findViewById(
                R.id.collapsibleInfo);
        CollapsibleView collapsibleAdditionalInfo = (CollapsibleView) getView().findViewById(
                R.id.collapsibleAdditionalInfo);
        final CollapsibleView collapsibleJournalTasks = (CollapsibleView) getView().findViewById(
                R.id.collapsibleJournalsTasks);
        final CollapsibleView collapsibleAttachments = (CollapsibleView) getView().findViewById(
                R.id.collapsibleAttachements);
        final CollapsibleView collapsibleChildren = (CollapsibleView) getView().findViewById(
                R.id.collapsibleChildren);
        final CollapsibleView collapsibleRelated = (CollapsibleView) getView().findViewById(
                R.id.collapsibleRelatedTasks);
        final CollapsibleView collapsibleDescription = (CollapsibleView) getView().findViewById(
                R.id.collapsibleDescription);
        final CollapsibleView collapsibleCustomItems = (CollapsibleView) getView().findViewById(
                R.id.collapsibleCustomItems);

        RestServiceGenerator.getApiService().getIssueDetail(getIssue().getId() + "", getIssueQueryMap(),
                new Callback<IssueResponse>() {
                    @Override
                    public void success(IssueResponse issuesResponse, Response response) {
                        setIssue(issuesResponse.getIssue());

                        if (getFragmentActivity() == null || getFragmentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) == null) {
                            return;
                        }

                        initDescription(collapsibleDescription);
                        initInfo(getIssue(), collapsibleInfo);

                        if (issuesResponse.getIssue().getAttachments() == null || issuesResponse.getIssue().getAttachments().size() == 0) {
                            collapsibleAttachments.setVisibility(View.GONE);
                        } else {
                            collapsibleAttachments.setVisibility(View.VISIBLE);

                            initAttachments(issuesResponse.getIssue().getAttachments(), collapsibleAttachments);
                        }

                        if (issuesResponse.getIssue().getRelations() == null ||
                                issuesResponse.getIssue().getRelations().size() == 0) {
                            collapsibleRelated.setVisibility(View.GONE);
                        } else {
                            collapsibleRelated.setVisibility(View.VISIBLE);

                            initRelated(issuesResponse.getIssue().getRelations(), collapsibleRelated);
                        }

                        if (issuesResponse.getIssue().getChildren() == null ||
                                issuesResponse.getIssue().getChildren().size() == 0) {
                            collapsibleChildren.setVisibility(View.GONE);
                        } else {
                            collapsibleChildren.setVisibility(View.VISIBLE);

                            initChildren(issuesResponse.getIssue().getChildren(), collapsibleChildren);
                        }

                        if (issuesResponse.getIssue().getJournals() == null ||
                                issuesResponse.getIssue().getJournals().size() == 0) {
                            collapsibleJournalTasks.setVisibility(View.GONE);
                        } else {
                            collapsibleJournalTasks.setVisibility(View.VISIBLE);

                            initJournals(issuesResponse.getIssue().getJournals(), collapsibleJournalTasks);
                        }

                        if (issuesResponse.getIssue().getCustomFields() == null ||
                                issuesResponse.getIssue().getCustomFields().size() == 0) {
                            collapsibleCustomItems.setVisibility(View.GONE);
                        } else {
                            collapsibleCustomItems.setVisibility(View.VISIBLE);

                            initCustomFields(issuesResponse.getIssue().getCustomFields(), collapsibleCustomItems);
                        }
//                        mMenuJournal.setEnabled(true);

                        getArguments().putBoolean(INIT_KEY, true);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        if (error != null) {
                            Log.d("t", "FAILURE issue response " + error.getResponse() + " " +
                                    error.getLocalizedMessage());
                            error.printStackTrace();
                        }
                    }
                });

    }

    public void initCollapsibleOpened(CollapsibleView view, boolean initState) {
        if (!getArguments().getBoolean(INIT_KEY, false)) {
            view.setOpened(initState);
        } else {
            view.setOpened(view.isOpened());
        }
    }

    private void initDescription(CollapsibleView collapsibleDescription) {
        TextView txtDescription = (TextView) ((LayoutInflater) getFragmentActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.widget_task_description, null);
        txtDescription.setText(getIssue().getDescription());
        if (TextUtils.isEmpty(getIssue().getDescription())) {
            collapsibleDescription.setVisibility(View.GONE);
        } else {
            collapsibleDescription.setVisibility(View.VISIBLE);
        }
        collapsibleDescription.setCollapsibleView(txtDescription);
        initCollapsibleOpened(collapsibleDescription, false);
    }

    private void initJournals(List<Journal> journals, CollapsibleView collapsibleJournal) {
        Log.d("journal", "Init journal " + journals.size());
        LinearLayout layout = new LinearLayout(getFragmentActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        View separator = null;
        boolean empty = true;

        for (Journal j : journals) {
            if (j.getNotes() == null || j.getNotes().isEmpty()) {
                continue;
            }
            empty = false;
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.widget_task_detail_journal,
                    layout, false);
            TextView txt = (TextView) row.findViewById(R.id.txtauthor);
            TextView txtNote = (TextView) row.findViewById(R.id.txtnote);
            separator = row.findViewById(R.id.separator);

            String preAuthor = getString(R.string.prefix_author);
            String author = j.getUser().getName();
            String postAuthor = TimeUtils.getTimeFormatted(j.getCreatedOn(), TimeUtils.ATOM_FORMAT,
                    TimeUtils.COMMENT_TIME_FORMAT);
            Spannable span = new SpannableString(preAuthor + " " + author + " " + postAuthor);
            span.setSpan(new ForegroundColorSpan(
                            getFragmentActivity().isEasyRedmine() ? getResources().getColor(R.color.er_primary) :
                                    getResources().getColor(R.color.r_primary)), preAuthor.length(),
                    (preAuthor + author + 1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt.setText(span);
            txtNote.setText(j.getNotes());
            layout.addView(row);
        }
        if (separator != null) {
            separator.setVisibility(View.GONE);
        }

        if (layout.getChildCount() == 0) {
            collapsibleJournal.setVisibility(View.GONE);
        }

        collapsibleJournal.setCollapsibleView(layout);
        initCollapsibleOpened(collapsibleJournal, !empty);

    }

    private void initInfo(Issue issue, CollapsibleView collapsibleInfo) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.widget_task_detail_info,
                collapsibleInfo, false);
        TextView txtId = (TextView) layout.findViewById(R.id.txtIssueId);
        TextView txtProject = (TextView) layout.findViewById(R.id.txtProject);
        TextView txtTracker = (TextView) layout.findViewById(R.id.txtTracker);
        TextView txtStatus = (TextView) layout.findViewById(R.id.txtStatus);
        TextView txtPriority = (TextView) layout.findViewById(R.id.txtPriority);
        TextView txtAuthor = (TextView) layout.findViewById(R.id.txtAuthor);
        TextView txtDueDate = (TextView) layout.findViewById(R.id.txtDueDate);
        TextView txtEstimatedTime = (TextView) layout.findViewById(R.id.txtEstimatedTime);
        TextView txtAssignee = (TextView) layout.findViewById(R.id.txtAssignee);
        TextView txtStartDate = (TextView) layout.findViewById(R.id.txtStartDate);
        TextView txtCategory = (TextView) layout.findViewById(R.id.txtCategory);
        TextView txtMilestone = (TextView) layout.findViewById(R.id.txtMilestone);
        final TextView txtSpentTime = (TextView) layout.findViewById(R.id.txtSpentTime);

        txtId.setText(String.valueOf(issue.getId()));
        if (issue.getCategory() != null && !TextUtils.isEmpty(issue.getCategory().getName())) {
            txtCategory.setText(issue.getCategory().getName());
        } else {
            layout.findViewById(R.id.vCategoryParent).setVisibility(View.GONE);
            layout.findViewById(R.id.vCategorySeparator).setVisibility(View.GONE);
        }

        if (issue.getFixedVersion() != null && !TextUtils.isEmpty(issue.getFixedVersion().getName())) {
            txtMilestone.setText(issue.getFixedVersion().getName());
        } else {
            layout.findViewById(R.id.vMilestoneParent).setVisibility(View.GONE);
            layout.findViewById(R.id.vMilestoneSeparator).setVisibility(View.GONE);
        }
        String startDateString = TimeUtils.getTimeFormatted(issue.getStartDate(), TimeUtils.ATOM_FORMAT_DATE, DateFormat.getDateFormat(getContext()));

        txtStartDate.setText(startDateString);
        txtProject.setText(issue.getProject().getName());
        txtTracker.setText(issue.getTracker().getName());
        txtStatus.setText(issue.getStatus().getName() + " (" + ((int) (issue.getDoneRatio())) + "%)");
        txtPriority.setText(issue.getPriority().getName());
        txtAuthor.setText(issue.getAuthor().getName());
        String dueDateString = TimeUtils.getTimeFormatted(issue.getDueDate(), TimeUtils.ATOM_FORMAT_DATE, DateFormat.getDateFormat(getContext()));
        if (TextUtils.isEmpty(dueDateString)) {
            txtDueDate.setText(R.string.no_value);
        } else {
            txtDueDate.setText(dueDateString);
        }

        txtEstimatedTime.setText(String.format("%.2f", issue.getEstimatedHours()));
        if (issue.getAssignedTo() != null) {
            txtAssignee.setText(issue.getAssignedTo().getName());
        }
        txtSpentTime.setText(String.format("%.2f", issue.getSpentHours()));
        collapsibleInfo.setCollapsibleView(layout);


        collapsibleInfo.setVisibility(View.VISIBLE);
        initCollapsibleOpened(collapsibleInfo, true);

    }

    private void initChildren(List<Issue> children, CollapsibleView collapsibleChildren) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        View img = null;
        for (final Issue i : children) {
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.widget_task_detail_attachment,
                    layout, false);
            TextView txt = (TextView) row.findViewById(R.id.txtAttachment);
            img = (View) row.findViewById(R.id.separator);
            txt.setText(i.getSubject());
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentActivity().replaceFragment(TaskDetailFragment.newInstance(i));
                }
            });
            layout.addView(row);
        }
        if (img != null) {
            img.setVisibility(View.GONE);
        }
        collapsibleChildren.setCollapsibleView(layout);
    }

    private void initAttachments(List<Attachement> attachments,
                                 CollapsibleView collapsibleAttachments) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        View img = null;
        View download = null;
        for (final Attachement a : attachments) {
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.widget_task_detail_attachment,
                    layout, false);
            TextView txt = (TextView) row.findViewById(R.id.txtAttachment);
            img = row.findViewById(R.id.separator);
            download = row.findViewById(R.id.vDownload);
            download.setVisibility(View.VISIBLE);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadAttachment(a);
                }
            });
            txt.setText(a.getFilename());
            layout.addView(row);
        }
        if (img != null) {
            img.setVisibility(View.GONE);
        }
        collapsibleAttachments.setCollapsibleView(layout);
        initCollapsibleOpened(collapsibleAttachments, false);
    }

    private void initCustomFields(List<CustomField> customFields, CollapsibleView collapsibleCustomFields) {
        LinearLayout linearLayout = new LinearLayout(getActivity()); //????
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < customFields.size(); i++) {
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.widget_task_detail_custom_field,
                    linearLayout, false);
            TextView txtTest = (TextView) row.findViewById(R.id.valueName);
            TextView txtValueTest = (TextView) row.findViewById(R.id.valueRight);

            txtTest.setText(customFields.get(i).getName());

            if (customFields.get(i).getValues() == null ||
                    customFields.get(i).getValues().isEmpty()) {
                txtValueTest.setText(R.string.ticket_detail_none);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int ii = 0; ii < customFields.get(i).getValues().size() - 1; ii++) {
                    stringBuilder.append(customFields.get(i).getValues().get(ii));
                    stringBuilder.append(", ");
                }
                stringBuilder.append(customFields.get(i).getValues().get(
                        customFields.get(i).getValues().size() - 1
                ));
                txtValueTest.setText(stringBuilder.equals("null") ? "" : stringBuilder.toString());
            }
            linearLayout.addView(row);
        }
        collapsibleCustomFields.setCollapsibleView(linearLayout);
        initCollapsibleOpened(collapsibleCustomFields, true);
    }

    private void downloadAttachment(Attachement a) {
        dm = (DownloadManager) getFragmentActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(a.getContentUrl()));
        String string = "Basic " + Storage.getCredentials();
        request.addRequestHeader("Authorization", string);
        request.setTitle(a.getFilename());
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, a.getFilename());
        request.allowScanningByMediaScanner();

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        enqueue = dm.enqueue(request);
    }

    private void initRelated(List<IssueRelation> related, CollapsibleView collapsibleRelated) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        View img = null;
        for (final IssueRelation ir : related) {
            int issueId = ir.getIssueId();
            if (issueId == getIssue().getId()) { //some relation has opposite definition of issue ids
                issueId = ir.getIssueToId();
            }
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.widget_task_detail_attachment,
                    layout, false);
            TextView txt = (TextView) row.findViewById(R.id.txtAttachment);
            img = row.findViewById(R.id.separator);

            txt.setText(issueId + "");
            final int finalIssueId = issueId;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Issue i = new Issue();
                    i.setId(finalIssueId);
                    getFragmentActivity().replaceFragment(TaskDetailFragment.newInstance(i));
                }
            });
            layout.addView(row);
        }
        if (img != null) {
            img.setVisibility(View.GONE);
        }
        collapsibleRelated.setCollapsibleView(layout);
        initCollapsibleOpened(collapsibleRelated, false);
    }

    private Map<String, String> getIssueQueryMap() {
        Map<String, String> map = new HashMap<>();
        map.put("include", "children,attachments,relations,journals");
        return map;
    }
}
