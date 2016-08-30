package cz.ackee.androidskeleton.model;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {22. 6. 2015}
 **/
public class ProjectDownloadingEvent {
    public static final String TAG = ProjectDownloadingEvent.class.getName();

    public boolean isHasSomeProjects() {
        return hasSomeProjects;
    }

    private final boolean hasSomeProjects;

    public ProjectDownloadingEvent(boolean hasSomeProjects) {
        this.hasSomeProjects = hasSomeProjects;
    }
}
