package cz.ackee.androidskeleton.utils;

import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.model.Project;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 16.3.2015.
 */
public class RedmineUtils {


    public static String getRedmineWebLink(Object entity, String identifier) {
        String resource = "unknown";
        if (entity instanceof Project) {
            resource = "projects";
        }else if(entity instanceof Issue){
            resource = "issues";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Storage.getURL());
        sb.append("/" + resource);
        sb.append("/" + identifier);
        return sb.toString();
    }
}
