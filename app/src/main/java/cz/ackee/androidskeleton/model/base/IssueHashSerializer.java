package cz.ackee.androidskeleton.model.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import cz.ackee.androidskeleton.model.IssueHash;

/**
 * Custom gson serializer for issue hash
 * Created by David Bilik[david.bilik@ackee.cz] on {3. 7. 2015}
 **/
public class IssueHashSerializer implements JsonSerializer<IssueHash> {
    public static final String TAG = IssueHashSerializer.class.getName();

    @Override
    public JsonElement serialize(IssueHash src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject rootObject = new JsonObject();
        if (src.projectId != null) {
            rootObject.addProperty("project_id", src.projectId);
        }
        if (src.assignedToId != null) {
            rootObject.addProperty("assigned_to_id", src.assignedToId);
        }

        if (shouldAddCategoryAllTime(src)) {
            rootObject.addProperty("category_id", src.categoryId);
        } else {
            if (src.categoryId != null) {
                rootObject.addProperty("category_id", src.categoryId);
            }
        }
        if (src.description != null) {
            rootObject.addProperty("description", src.description);
        }
        if (src.doneRatio != null) {
            rootObject.addProperty("done_ratio", src.doneRatio);
        }
        if (src.dueDate != null) {
            rootObject.addProperty("due_date", src.dueDate);
        }
        if (src.startDate != null) {
            rootObject.addProperty("start_date", src.startDate);
        }
        if (src.estimatedHours != null) {
            rootObject.addProperty("estimated_hours", src.estimatedHours);
        }
        if (src.notes != null) {
            rootObject.addProperty("notes", src.notes);
        }

        if (shouldAddFixedVersionAllTime(src)) {
            rootObject.addProperty("fixed_version_id", src.fixedVersionId);
        } else {
            if (src.fixedVersionId != null) {
                rootObject.addProperty("fixed_version_id", src.fixedVersionId);
            }
        }

        if (src.parentIssueId != null) {
            rootObject.addProperty("parent_issue_id", src.parentIssueId);
        }
        if (src.priorityId != null) {
            rootObject.addProperty("priority_id", src.priorityId);
        }
        if (src.statusId != null) {
            rootObject.addProperty("status_id", src.statusId);
        }
        if (src.subject != null) {
            rootObject.addProperty("subject", src.subject);
        }
        if (src.trackerId != null) {
            rootObject.addProperty("tracker_id", src.trackerId);
        }
        if (src.uploads != null && src.uploads.size() > 0) {
            rootObject.add("uploads", context.serialize(src.uploads));
        }
        if (src.customFields != null && src.customFields.size() > 0) {
            rootObject.add("custom_fields", context.serialize(src.customFields));
        }

        return rootObject;
    }

    protected boolean shouldAddFixedVersionAllTime(IssueHash src) {
        return true;
    }

    protected boolean shouldAddCategoryAllTime(IssueHash src) {
        return true;
    }

}
