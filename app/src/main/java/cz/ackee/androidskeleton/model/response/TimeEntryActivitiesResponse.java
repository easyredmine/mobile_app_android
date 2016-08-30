package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.TimeEntryActivity;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TimeEntryActivitiesResponse {
    public static final String TAG = TimeEntryActivitiesResponse.class.getName();

    List<TimeEntryActivity> timeEntryActivities;

    public List<TimeEntryActivity> getTimeEntryActivities() {
        return timeEntryActivities;
    }
}
