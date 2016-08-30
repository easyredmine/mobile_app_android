package cz.ackee.androidskeleton.model.request;

/**
 * Object class for update issue request
 * <p/>
 * SimpleIssue class to ensure, that we do not set any value of Issue class by accident
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 26.3.2015.
 */
public class JournalIssueRequest {
    public JournalIssueUpdate issue;

    public static class JournalIssueUpdate {

        public JournalIssueUpdate(String notes) {
            this.notes = notes;
        }

        public String notes;
    }
}
