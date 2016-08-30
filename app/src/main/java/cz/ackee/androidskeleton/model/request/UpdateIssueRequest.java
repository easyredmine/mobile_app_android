package cz.ackee.androidskeleton.model.request;

import cz.ackee.androidskeleton.model.IssueHash;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 4.4.2015.
 */
public class UpdateIssueRequest {
    public IssueHash issue;

    public UpdateIssueRequest(IssueHash issue) {
        this.issue = issue;
    }
}
