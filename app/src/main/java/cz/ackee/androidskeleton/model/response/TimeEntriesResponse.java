package cz.ackee.androidskeleton.model.response;

import java.util.List;

import cz.ackee.androidskeleton.model.TimeEntry;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TimeEntriesResponse {
    public static final String TAG = TimeEntriesResponse.class.getName();

    List<TimeEntry> timeEntries;

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }
}
