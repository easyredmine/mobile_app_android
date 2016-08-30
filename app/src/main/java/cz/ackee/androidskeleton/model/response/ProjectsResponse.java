package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.Project;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {16. 2. 2015}
 */
public class ProjectsResponse {
    public static final String TAG = ProjectsResponse.class.getName();
    private List<Project> projects;
    int totalCount;
    int offset;
    int limit;

    public List<Project> getProjects() {
        return projects;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
