package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.ProjectVersion;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class ProjectVersionsResponse {
    public static final String TAG = ProjectVersionsResponse.class.getName();
    List<ProjectVersion> versions;
    int totalCount;

    public List<ProjectVersion> getVersions() {
        return versions;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
