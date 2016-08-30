package cz.ackee.androidskeleton.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 16.3.2015.
 */
public class TimeUtils {
    public static final SimpleDateFormat ATOM_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat ATOM_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat COMMENT_TIME_FORMAT = new SimpleDateFormat("HH:mm, dd.MM. yyyy");
    public static final SimpleDateFormat ADDITIONAL_INFO_FORMAT = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
    public static final SimpleDateFormat DUE_DATE = new SimpleDateFormat("dd.MM.yyyy");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy");

     public static String getTimeFormatted(String time, DateFormat inputFormat, DateFormat format) {
        if(time==null){
            return "";
        }
        Date date;
        try {
            date = inputFormat.parse(time);
            return format.format(date);
        } catch (ParseException e) {
            Log.e(TimeUtils.class.getSimpleName(), "Unparsable date " + time);
            return "";
        }
    }

    public static boolean expired(String dueDate) {
        if (dueDate == null)
            return false;

        Date date, current;
        try {
            date = ATOM_FORMAT_DATE.parse(dueDate);
            current = new Date();

            if (current.after(date) || date.equals(current)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            Log.e(TimeUtils.class.getSimpleName(), "Unparsable due date " + dueDate);
            return false;
        }
    }
}

