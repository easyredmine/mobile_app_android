package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.Query;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 19.3.2015.
 */
public class EasyQueriesResponse {
    public static final String TAG = EasyQueriesResponse.class.getName();
    List<Query> easyQueries;
    int totalCount;
    int offset;
    int limit;

    public List<Query> getQueries() {
        return easyQueries;
    }

    public void setQueries(List<Query> queries) {
        this.easyQueries = queries;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
