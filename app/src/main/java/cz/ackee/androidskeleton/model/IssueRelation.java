package cz.ackee.androidskeleton.model;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class IssueRelation {
    public static final String TAG = IssueRelation.class.getName();

    int id;
    int issueId;
    int issueToId;
    String relationType;

    public int getId() {
        return id;
    }

    public int getIssueId() {
        return issueId;
    }

    public int getIssueToId() {
        return issueToId;
    }

    public String getRelationType() {
        return relationType;
    }
}
