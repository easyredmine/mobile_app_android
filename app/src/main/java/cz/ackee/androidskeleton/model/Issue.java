package cz.ackee.androidskeleton.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.model.response.CustomField;

/**
 * Class entity representing issue ticket
 * Created by David Bilik[david.bilik@ackee.cz] on {16. 2. 2015}
 */
public class Issue implements Parcelable {
    public static final String TAG = Issue.class.getName();
    int id;
    IdNameEntity project;
    IdNameEntity tracker;
    IdNameEntity status;
    IdNameEntity priority;
    IdNameEntity author;
    IdNameEntity assignedTo;
    IdNameEntity category;
    IdNameEntity parent;
    IdNameEntity fixedVersion;

    String subject;
    String description;

    double estimatedHours;
    String startDate;
    double doneRatio;
    String createdOn;
    String updatedOn;
    String closedOn;
    boolean isFavorited;
    double spentHours;

    List<Attachement> attachments;
    List<IssueRelation> relations;
    List<Issue> children;
    List<Journal> journals;

    List<CustomField> customFields;

    private String dueDate;

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public List<Journal> getJournals() {
        return journals;
    }

    public List<Issue> getChildren() {
        return children;
    }

    public List<Attachement> getAttachments() {
        return attachments;
    }

    public List<IssueRelation> getRelations() {
        return relations;
    }

    public int getId() {
        return id;
    }

    public IdNameEntity getProject() {
        return project;
    }

    public IdNameEntity getTracker() {
        return tracker;
    }

    public IdNameEntity getStatus() {
        return status;
    }

    public IdNameEntity getPriority() {
        return priority;
    }

    public IdNameEntity getAuthor() {
        return author;
    }

    public IdNameEntity getAssignedTo() {
        return assignedTo;
    }

    public IdNameEntity getFixedVersion() {
        return fixedVersion;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public double getDoneRatio() {
        return doneRatio;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public String getClosedOn() {
        return closedOn;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public double getSpentHours() {
        return spentHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.project, 0);
        dest.writeParcelable(this.tracker, 0);
        dest.writeParcelable(this.status, 0);
        dest.writeParcelable(this.priority, 0);
        dest.writeParcelable(this.author, 0);
        dest.writeParcelable(this.assignedTo, 0);
        dest.writeString(this.subject);
        dest.writeString(this.description);
        dest.writeString(this.startDate);
        dest.writeDouble(this.doneRatio);
        dest.writeString(this.createdOn);
        dest.writeString(this.updatedOn);
        dest.writeString(this.closedOn);
        dest.writeInt(isFavorited ? 1 : 0);
        dest.writeDouble(spentHours);
        dest.writeString(dueDate);
        dest.writeParcelable(category, flags);
        dest.writeParcelable(parent, flags);
        dest.writeParcelable(this.fixedVersion, 0);
        dest.writeTypedList(customFields);
    }

    public Issue() {
        customFields = new ArrayList<CustomField>();
    }

    private Issue(Parcel in) {
        this.id = in.readInt();
        this.project = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.tracker = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.status = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.priority = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.author = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.assignedTo = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.subject = in.readString();
        this.description = in.readString();
        this.startDate = in.readString();
        this.doneRatio = in.readDouble();
        this.createdOn = in.readString();
        this.updatedOn = in.readString();
        this.closedOn = in.readString();
        this.isFavorited = in.readInt() > 0;
        this.spentHours = in.readDouble();
        this.dueDate = in.readString();
        this.category = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.parent = in.readParcelable(IdNameEntity.class.getClassLoader());
        this.fixedVersion = in.readParcelable(IdNameEntity.class.getClassLoader());
        customFields = new ArrayList<>();
        in.readTypedList(customFields, CustomField.CREATOR);

    }

    public static final Parcelable.Creator<Issue> CREATOR = new Parcelable.Creator<Issue>() {
        public Issue createFromParcel(Parcel source) {
            return new Issue(source);
        }

        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public void setCustomFields(List<CustomField> customFields) {
        this.customFields = customFields;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setJournals(List<Journal> journals) {
        this.journals = journals;
    }

    public IdNameEntity getCategory() {
        return category;
    }


    public void setId(int id) {
        this.id = id;
    }

    public IdNameEntity getParent() {
        return parent;
    }
}
