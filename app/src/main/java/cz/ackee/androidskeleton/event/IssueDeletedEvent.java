package cz.ackee.androidskeleton.event;

/**
 * Event that is fired when issue is deleted
 * Created by David Bilik[david.bilik@ackee.cz] on {3. 7. 2015}
 **/
public class IssueDeletedEvent {
    public static final String TAG = IssueDeletedEvent.class.getName();
    private final int mIssueId;

    public IssueDeletedEvent(int id) {
        this.mIssueId = id;
    }

    public int getIssueId() {
        return mIssueId;
    }
}
