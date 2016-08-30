package cz.ackee.androidskeleton.model.base;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import cz.ackee.androidskeleton.model.TimeEntry;

/**
 * Custom gson serializer for time entry
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 16/12/15
 **/
public class TimeEntrySerializer implements JsonSerializer<TimeEntry> {
    public static final String TAG = TimeEntrySerializer.class.getName();

    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     * <p/>
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonSerializationContext#serialize(Object, Type)} method to create JsonElements for any
     * non-trivial field of the {@code src} object. However, you should never invoke it on the
     * {@code src} object itself since that will cause an infinite loop (Gson will call your
     * call-back method again).</p>
     *
     * @param src       the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @param context   a context
     * @return a JsonElement corresponding to the specified object.
     */
    @Override
    public JsonElement serialize(TimeEntry src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject rootObject = new JsonObject();

        if (!TextUtils.isEmpty(src.getComments())) {
            rootObject.addProperty("comments", src.getComments());
        }

        rootObject.addProperty("issue_id", src.getIssueId());
        rootObject.addProperty("activity_id", src.getActivityId());
        rootObject.addProperty("hours", src.getHours());

        if (!TextUtils.isEmpty(src.getSpentOn())) {
            rootObject.addProperty("spent_on", src.getSpentOn());
        }

        return rootObject;
    }
}
