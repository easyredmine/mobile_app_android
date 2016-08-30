package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.IssuePriority;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class IssuePriorityResponse {
    public static final String TAG = IssuePriorityResponse.class.getName();
    List<IssuePriority> issuePriorities;

    public List<IssuePriority> getIssuePriorities() {
        return issuePriorities;
    }
}
