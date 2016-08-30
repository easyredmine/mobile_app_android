package cz.ackee.androidskeleton.rest;

import cz.ackee.androidskeleton.model.request.CreateCMRRequest;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Original project name : easyredmine-android-aplikace
 * Created by Petr Lorenc[petr.lorenc@ackee.cz] on 11.6.2015.
 */
public interface ApiDescriptionCollectInfo {

    @POST("/projects/1719/easy_crm_cases.json?key=50d441e2eef45f7f138d8e91afb1e298e07f9f70")
    void sendInformation(@Body CreateCMRRequest request ,Callback<IssueResponse> callback);
}
