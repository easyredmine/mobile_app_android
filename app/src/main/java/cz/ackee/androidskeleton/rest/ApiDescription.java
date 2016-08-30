package cz.ackee.androidskeleton.rest;

import java.util.Map;

import cz.ackee.androidskeleton.model.request.CheckFieldIssueRequest;
import cz.ackee.androidskeleton.model.request.CreateIssueRequest;
import cz.ackee.androidskeleton.model.request.TimeEntryRequest;
import cz.ackee.androidskeleton.model.request.UpdateIssueRequest;
import cz.ackee.androidskeleton.model.response.EasyQueriesResponse;
import cz.ackee.androidskeleton.model.response.EasyRedmineValidationResponse;
import cz.ackee.androidskeleton.model.response.IssuePriorityResponse;
import cz.ackee.androidskeleton.model.response.IssueRelationsResponse;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import cz.ackee.androidskeleton.model.response.IssueStatusesResponse;
import cz.ackee.androidskeleton.model.response.IssuesResponse;
import cz.ackee.androidskeleton.model.response.MembershipResponse;
import cz.ackee.androidskeleton.model.response.ProjectResponse;
import cz.ackee.androidskeleton.model.response.ProjectVersionsResponse;
import cz.ackee.androidskeleton.model.response.ProjectsResponse;
import cz.ackee.androidskeleton.model.response.QueriesResponse;
import cz.ackee.androidskeleton.model.response.TimeEntriesResponse;
import cz.ackee.androidskeleton.model.response.TimeEntryActivitiesResponse;
import cz.ackee.androidskeleton.model.response.TrackersResponse;
import cz.ackee.androidskeleton.model.response.UploadAttachmentResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Description of Redmine API
 * Created by David Bilik[david.bilik@ackee.cz] on {16. 2. 2015}
 */
public interface ApiDescription {

    @GET("/projects.json")
    public ProjectsResponse getProjects(@QueryMap Map<String, String> queries);

    @GET("/projects.json")
    public void pingServer(Callback<ProjectsResponse> callback);

    @GET("/projects/{id}.json")
    public void getProject(@Path("id") String id, @QueryMap Map<String, String> queries, Callback<ProjectResponse> callback);

    @GET("/issues.json")
    public IssuesResponse getIssues(@QueryMap Map<String, String> queries);

    @GET("/issues/{id}.json")
    public void getIssueDetail(@Path("id") String id, @QueryMap Map<String, String> queries, Callback<IssueResponse> callback);

    @POST("/issues.json")
    public void createIssue(@Body CreateIssueRequest request, Callback<IssueResponse> callback);

    @PUT("/issues/{id}.json")
    public void updateIssue(@Path("id") String id, @Body UpdateIssueRequest request, Callback<IssueResponse> callback);

    @DELETE("/issues/{id}.json")
    public void deleteIssue(@Path("id") String id, Callback<IssueResponse> callback);

    @GET("/issue_statuses.json")
    public void getIssueStatuses(Callback<IssueStatusesResponse> callback);

    @GET("/projects/{id}/versions.json")
    public ProjectVersionsResponse getTargetVersions(@Path("id") String id);

    @GET("/enumerations/issue_priorities.json")
    public void getIssuePriorities(Callback<IssuePriorityResponse> callback);

    @GET("/issues/{id}/relations.json")
    public void getIssueRelations(@Path("id") String id, Callback<IssueRelationsResponse> callback);

    @POST("/time_entries.json")
    public void addTimeEntry(@Body TimeEntryRequest request, Callback<Object> callback);

    @GET("/enumerations/time_entry_activities.json")
    public void getTimeEntryActivities(Callback<TimeEntryActivitiesResponse> callback);

    @GET("/time_entries.json")
    public void getTimeEntriesForIssue(@Query("issue_id") String issueId, Callback<TimeEntriesResponse> callback);

    @GET("/queries.json")
    public QueriesResponse getQueries();

    @GET("/easy_queries.json")
    public EasyQueriesResponse getEasyQueries();

    @GET("/trackers.json")
    public void getTrackers(Callback<TrackersResponse> callback);

    @GET("/projects/{id}/memberships.json")
    public MembershipResponse getMemberships(@Path("id") String projectId, @QueryMap Map<String, String> queries);

    @POST("/uploads.json")
    UploadAttachmentResponse uploadFile(@Body TypedFile resource);

    @POST("/easy_issues/{id}/fields.json")
    void getAvailableEasyIssue(@Body CheckFieldIssueRequest request, @Path("id") String projectId, Callback<EasyRedmineValidationResponse> callback);

    @POST("/easy_issues/fields/{id}.json")
    void getAvailableEasyIssueForTask(@Body CheckFieldIssueRequest request, @Path("id") String taskId, Callback<EasyRedmineValidationResponse> callback);
}
