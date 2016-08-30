package cz.ackee.androidskeleton.model.response;

import com.google.gson.annotations.SerializedName;

import cz.ackee.androidskeleton.model.EasyRedmineFormAttributes;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 29.4.2015.
 */
public class EasyRedmineValidationResponse {
    @SerializedName("form_attributes")
    public EasyRedmineFormAttributes formAttributes;
}
