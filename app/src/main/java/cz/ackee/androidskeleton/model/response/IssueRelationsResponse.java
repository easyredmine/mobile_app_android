package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.IssueRelation;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class IssueRelationsResponse {
    public static final String TAG = IssueRelationsResponse.class.getName();
    List<IssueRelation> relations;

    public List<IssueRelation> getRelations() {
        return relations;
    }
}
