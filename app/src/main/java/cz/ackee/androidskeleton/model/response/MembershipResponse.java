package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.Membership;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 7.4.2015.
 */
public class MembershipResponse {
    List<Membership> memberships;

    int totalCount;
    int offset;
    int limit;

    public int getLimit() {
        return limit;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<Membership> getMemberShips() {
        return memberships;
    }
}
