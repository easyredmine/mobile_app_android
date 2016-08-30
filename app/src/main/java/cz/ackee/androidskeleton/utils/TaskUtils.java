package cz.ackee.androidskeleton.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.ackee.androidskeleton.iface.Defaultable;
import cz.ackee.androidskeleton.model.IdNameEntity;
import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.model.IssueFieldsHash;
import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.ui.GmailInputView;

/**
 * Utils for creating/updating task
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 14/12/15
 **/
public class TaskUtils {
    public static final String TAG = TaskUtils.class.getName();

    public static final int EDIT_SUBJECT = 1;
    public static final int EDIT_DESCRIPTION = 2;
    public static final int EDIT_PARENT_TASK = 3;
    public static final int EDIT_START_DATE = 4;
    public static final int EDIT_DUE_DATE = 5;
    public static final int EDIT_ESTIMATED_TIME = 6;
    public static final int EDIT_COMMENT = 7;

    public static void setIssueHash(IssueHash issueHash, int projectId, Issue issue, boolean isEasyRedmine) {
        if (projectId == -1) {
            projectId = issue.getProject().getId();
        }
        issueHash.projectId = projectId;

        issueHash.subject = issue.getSubject();
        issueHash.description = issue.getDescription();
        if (issue.getCategory() != null) {
            issueHash.categoryId = issue.getCategory().getId();
        }
        issueHash.trackerId = issue.getTracker().getId();

        if (issue.getStatus() != null) {
            issueHash.statusId = issue.getStatus().getId();
        }
        if (issue.getAssignedTo() != null) {
            issueHash.assignedToId = issue.getAssignedTo().getId();
        }
        issueHash.doneRatio = Double.valueOf(issue.getDoneRatio()).intValue();
        if (Double.valueOf(issue.getEstimatedHours()).intValue() > 0) {
            issueHash.estimatedHours = String.valueOf(issue.getEstimatedHours());
        }

        if (issue.getFixedVersion() != null) {
            issueHash.fixedVersionId = issue.getFixedVersion().getId();
        }

        issueHash.startDate = issue.getStartDate();
        issueHash.dueDate = issue.getDueDate();
        if (issue.getParent() != null) {
            issueHash.parentIssueId = issue.getParent().getId();
        }
        issueHash.priorityId = issue.getPriority().getId();
        if (isEasyRedmine) {
            issueHash.customFields = issue.getCustomFields();
        }
    }

    public static String getIssueHashValue(IssueHash issueHash, int value) {
        switch (value) {
            case EDIT_SUBJECT:
                return issueHash.subject;
            case EDIT_DESCRIPTION:
                return issueHash.description;
            case EDIT_START_DATE:
                return issueHash.startDate;
            case EDIT_DUE_DATE:
                return issueHash.dueDate;
            case EDIT_PARENT_TASK:

                return "" + String.valueOf(issueHash.parentIssueId);
            case EDIT_ESTIMATED_TIME:
                return issueHash.estimatedHours;
            case EDIT_COMMENT:
                return issueHash.notes;
            default:
                break;
        }
        return "";
    }

    public static void setIssueHashValue(IssueHash issueHash, int value, String s) {
        switch (value) {
            case EDIT_SUBJECT:
                issueHash.subject = s;
                break;
            case EDIT_DESCRIPTION:
                issueHash.description = s;
                break;
            case EDIT_START_DATE:
                issueHash.startDate = s;
                break;
            case EDIT_DUE_DATE:
                issueHash.dueDate = s;
                break;
            case EDIT_PARENT_TASK:
                try {
                    issueHash.parentIssueId = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case EDIT_ESTIMATED_TIME:
                issueHash.estimatedHours = s;
                break;
            case EDIT_COMMENT:
                issueHash.notes = s;
            default:
                break;
        }
    }

    public static Map<String, String> getProjectQueryMap() {
        Map<String, String> map = new HashMap<>();
        map.put("include", "trackers,issue_categories");
        return map;
    }

    public static int getDefaultIndex(List<?> list) {
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

    public static int getSelectedIndex(List<IdNameEntity> data, Integer assignedToId) {
        if (assignedToId == null) {
            return -1;
        }
        for (int position = 0; position < data.size(); position++) {
            if (assignedToId.equals(data.get(position).getId())) {
                return position;
            }
        }
        return 0;
    }

    public static void initEditText(final IssueHash issueHash, GmailInputView view, final int value, boolean isEditing, GmailInputView startDateView) {
        String editValue = TaskUtils.getIssueHashValue(issueHash, value);
        if (value == TaskUtils.EDIT_DUE_DATE || value == TaskUtils.EDIT_START_DATE) {
            if (editValue != null) {
                view.setText(TimeUtils.getTimeFormatted(editValue, TimeUtils.ATOM_FORMAT_DATE, TimeUtils.DUE_DATE));

            }
            if (value == TaskUtils.EDIT_START_DATE && !isEditing && TextUtils.isEmpty(editValue)) {
                startDateView.onDateSelected(Calendar.getInstance());
                TaskUtils.setIssueHashValue(issueHash, value, TimeUtils.getTimeFormatted(startDateView.getText(), TimeUtils.DUE_DATE, TimeUtils.ATOM_FORMAT_DATE));
            }

        } else {
            if (editValue != null) {
                view.setText(editValue);
            }
        }

        view.editText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (value == TaskUtils.EDIT_DUE_DATE || value == TaskUtils.EDIT_START_DATE) {
                    setIssueHashValue(issueHash, value, TimeUtils.getTimeFormatted(s.toString(), TimeUtils.DUE_DATE, TimeUtils.ATOM_FORMAT_DATE));
                } else {
                    setIssueHashValue(issueHash, value, s.toString());
                }
            }
        });
    }

    @NonNull
    public static String getCFValueString(ArrayList<String> values) {
        if (values == null ||
                values.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int ii = 0; ii < values.size() - 1; ii++) {
            stringBuilder.append(values.get(ii));
            stringBuilder.append(", ");
        }
        stringBuilder.append(values.get(
                values.size() - 1
        ));
        return stringBuilder.toString().equals("null") ? "" : stringBuilder.toString();

    }

    @NonNull
    public static Uri handleImageUri(Uri remoteUri) {
        if (remoteUri.toString().startsWith("content://com.android.gallery3d.provider")) {
            // use the com.google provider, not the com.android provider.
            remoteUri = Uri.parse(remoteUri.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"));
        }
        if (remoteUri.toString().startsWith("content://com.google.android.apps.photos.content")) {
            return remoteUri;
        }

        return remoteUri;
    }

    public static void copyHash(@NonNull IssueHash src, @NonNull IssueFieldsHash dst) {
        dst.fixedVersionId = src.fixedVersionId;
        dst.notes = src.notes;
        dst.projectId = src.projectId;
        dst.assignedToId = src.assignedToId;
        dst.categoryId = src.categoryId;
        dst.customFields = src.customFields;
        dst.description = src.description;
        dst.doneRatio = src.doneRatio;
        dst.estimatedHours = src.estimatedHours;
        dst.parentIssueId = src.parentIssueId;
        dst.priorityId = src.priorityId;
        dst.dueDate = src.dueDate;
        dst.startDate = src.startDate;
        dst.subject = src.subject;
        dst.uploads = src.uploads;
        dst.statusId = src.statusId;
        dst.trackerId = src.trackerId;
    }
}
