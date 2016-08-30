package cz.ackee.androidskeleton.model.request;

import cz.ackee.androidskeleton.model.TimeEntry;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TimeEntryRequest {
    public static final String TAG = TimeEntryRequest.class.getName();

    TimeEntry timeEntry;

    public TimeEntryRequest(TimeEntry timeEntry) {
        this.timeEntry = timeEntry;
    }
}
