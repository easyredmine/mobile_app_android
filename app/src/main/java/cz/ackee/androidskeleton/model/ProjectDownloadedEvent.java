package cz.ackee.androidskeleton.model;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {22. 6. 2015}
 **/
public class ProjectDownloadedEvent {
    public static final String TAG = ProjectDownloadedEvent.class.getName();
    private final boolean success;

    public ProjectDownloadedEvent(boolean success) {
        this.success = success;
    }
}
