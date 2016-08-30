package cz.ackee.androidskeleton.model.request;

import cz.ackee.androidskeleton.model.IssueFieldsHash;
import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.utils.TaskUtils;

/**
 * TODO add class description
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 17/12/15
 **/
public class CheckFieldIssueRequest {
    public static final String TAG = CheckFieldIssueRequest.class.getName();

    public IssueFieldsHash issue;

    public CheckFieldIssueRequest(IssueHash issue, boolean sendMilestone) {
        this.issue = new IssueFieldsHash();

        TaskUtils.copyHash(issue, this.issue);
        this.issue.alwaysSendMilestone = sendMilestone;

    }
}
