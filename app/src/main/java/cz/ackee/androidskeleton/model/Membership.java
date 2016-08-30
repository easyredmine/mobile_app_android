package cz.ackee.androidskeleton.model;

import java.util.List;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 7.4.2015.
 */
public class Membership {
    int id;
    IdNameEntity project;
    IdNameEntity user;
    IdNameEntity group;
    List<IdNameEntity> roles;

    public IdNameEntity getUser() {
        return user;
    }
    public IdNameEntity getGroup() {
        return group;
    }
}
