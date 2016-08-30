package cz.ackee.androidskeleton.model;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TimeEntry {
    public static final String TAG = TimeEntry.class.getName();
    int issueId;// or project_id (only one is required): the issue id or project id to log time on
    String spentOn;//: the date the time was spent (default to the current date)
    double hours;// (required): the number of spent hours
    int activityId;//: the id of the time activity. This parameter is required unless a default activity is defined in Redmine.
    String comments;//: short description for the entry (255 characters max)

    public TimeEntry(int issueId, String spentOn, double hours, int activityId, String comments) {
        this.issueId = issueId;
        this.spentOn = spentOn;
        this.hours = hours;
        this.activityId = activityId;
        this.comments = comments;
    }

    public TimeEntry() {
    }

    public int getIssueId() {
        return issueId;
    }

    public String getSpentOn() {
        return spentOn;
    }

    public double getHours() {
        return hours;
    }

    public int getActivityId() {
        return activityId;
    }

    public String getComments() {
        return comments;
    }
}
