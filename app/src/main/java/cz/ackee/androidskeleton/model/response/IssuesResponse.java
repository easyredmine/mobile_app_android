package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.Issue;

/**
 * Api response for list of issues
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class IssuesResponse {
    public static final String TAG = IssuesResponse.class.getName();

    int totalCount;
    int offset;
    int limit;

    List<Issue> issues;

    public int getTotalCount() {
        return totalCount;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
