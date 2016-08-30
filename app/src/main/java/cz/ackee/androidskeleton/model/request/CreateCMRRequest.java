package cz.ackee.androidskeleton.model.request;

import cz.ackee.androidskeleton.model.CMRHash;

/**
 * Original project name : easyredmine-android-aplikace
 * Created by Petr Lorenc[petr.lorenc@ackee.cz] on 11.6.2015.
 */
public class CreateCMRRequest {
    public CMRHash easyCrmCase;

    public CreateCMRRequest(CMRHash easyCrmCase) {
        this.easyCrmCase = easyCrmCase;
    }
}
