package cz.ackee.androidskeleton.model.response;

import cz.ackee.androidskeleton.model.Issue;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class IssueResponse {
    public static final String TAG = IssueResponse.class.getName();

    public Issue getIssue() {
        return issue;
    }

    Issue issue;
}
