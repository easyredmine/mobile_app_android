package cz.ackee.androidskeleton.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.model.response.CustomField;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 8.4.2015.
 */
public class IssueHash implements Parcelable {

    public Integer projectId;
    public String subject;
    public Integer statusId;
    public Integer priorityId;
    public Integer trackerId;
    public String description;
    public Integer assignedToId;
    public Integer categoryId;
    public Integer parentIssueId;
    public Integer fixedVersionId;
    public String startDate;
    public String dueDate;
    public String estimatedHours;
    public List<Upload> uploads;
    public Integer doneRatio;
    public String notes;
    public List<CustomField> customFields;

    public boolean alwaysSendMilestone;

    @Override
    public String toString() {
        return "Issue [projectId="+projectId+",subject="+subject+"]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.projectId);
        dest.writeString(this.subject);
        dest.writeValue(this.statusId);
        dest.writeValue(this.priorityId);
        dest.writeValue(this.trackerId);
        dest.writeString(this.description);
        dest.writeValue(this.assignedToId);
        dest.writeValue(this.categoryId);
        dest.writeValue(this.parentIssueId);
        dest.writeValue(this.fixedVersionId);
        dest.writeString(this.startDate);
        dest.writeString(this.dueDate);
        dest.writeString(this.estimatedHours);
        dest.writeList(this.uploads);
        dest.writeValue(this.doneRatio);
        dest.writeString(this.notes);
        dest.writeTypedList(customFields);
        dest.writeByte(alwaysSendMilestone ? (byte) 1 : (byte) 0);
    }

    public IssueHash() {
    }

    protected IssueHash(Parcel in) {
        this.projectId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.subject = in.readString();
        this.statusId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.priorityId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.trackerId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.description = in.readString();
        this.assignedToId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.categoryId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.parentIssueId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fixedVersionId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.startDate = in.readString();
        this.dueDate = in.readString();
        this.estimatedHours = in.readString();
        this.uploads = new ArrayList<Upload>();
        in.readList(this.uploads, List.class.getClassLoader());
        this.doneRatio = (Integer) in.readValue(Integer.class.getClassLoader());
        this.notes = in.readString();
        this.customFields = in.createTypedArrayList(CustomField.CREATOR);
        this.alwaysSendMilestone = in.readByte() != 0;
    }

    public static final Parcelable.Creator<IssueHash> CREATOR = new Parcelable.Creator<IssueHash>() {
        public IssueHash createFromParcel(Parcel source) {
            return new IssueHash(source);
        }

        public IssueHash[] newArray(int size) {
            return new IssueHash[size];
        }
    };
}
