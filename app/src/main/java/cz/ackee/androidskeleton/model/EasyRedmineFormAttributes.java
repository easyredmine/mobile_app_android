package cz.ackee.androidskeleton.model;

import java.util.List;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 29.4.2015.
 */
public class EasyRedmineFormAttributes {
    public Issue issue;
    public List<IdNameEntity> availableProjects;
    public List<IdNameEntity> availableTrackers;
    public List<IssueStatus> availableStatuses;
    public List<IdNameEntity> availableAssignees;
    public List<IdNameEntity> availableCategories;
    public List<IdNameEntity> availableFixedVersions;
    public List<IdNameEntity> availableActivities;
    public List<IssuePriority> availablePriorities;
    public List<CustomFieldValues> availableCustomFieldsValues;
}
