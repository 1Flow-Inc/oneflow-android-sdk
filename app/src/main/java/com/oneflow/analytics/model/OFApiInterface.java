package com.oneflow.analytics.model;

import com.oneflow.analytics.model.adduser.OFAddUserRequest;
import com.oneflow.analytics.model.adduser.OFAddUserResultResponse;
import com.oneflow.analytics.model.createsession.OFCreateSessionRequest;
import com.oneflow.analytics.model.createsession.OFCreateSessionResponse;
import com.oneflow.analytics.model.events.OFEventAPIRequest;
import com.oneflow.analytics.model.events.OFEventSubmitResponse;
import com.oneflow.analytics.model.location.OFLocationResponse;
import com.oneflow.analytics.model.loguser.OFLogUserRequest;
import com.oneflow.analytics.model.loguser.OFLogUserResponse;
import com.oneflow.analytics.model.survey.OFSurveyUserInput;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface OFApiInterface {

    @POST//("v1/2021-06-15/project_users")
    Call<OFGenericResponse<OFAddUserResultResponse>> addUserComman(@Header("one_flow_key") String headerKey, @Body OFAddUserRequest aur, @Url String url);

    @POST//("v1/2021-06-15/sessions")
    Call<OFGenericResponse<OFCreateSessionResponse>> createSession(@Header("one_flow_key") String headerKey, @Body OFCreateSessionRequest aur, @Url String url);

    @GET//("v1/2021-06-15/survey")
    Call<OFGenericResponse<ArrayList<OFGetSurveyListResponse>>> getSurvey(@Header("one_flow_key") String headerKey, @Url String url, @Query("platform") String platform, @Query("mode") String mode);

    @GET("v1/2021-06-15/location")
    Call<OFLocationResponse> getLocation(@Header("one_flow_key") String headerKey);

    @POST//("v1/2021-06-15/survey-response")
    Call<OFGenericResponse<String>> submitSurveyUserResponse(@Header("one_flow_key") String headerKey, @Body OFSurveyUserInput aur, @Url String url);

    ///@POST("v1/2021-06-15/events/bulk")
    @POST  //("https://us-west-2.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/1flow-wslxs/service/events-bulk/incoming_webhook/insert-events")
    Call<OFGenericResponse<OFEventSubmitResponse>> uploadAllUnSyncedEvents(@Header("one_flow_key") String headerKey, @Body OFEventAPIRequest ear, @Url String url);

    @GET("v1/2021-06-15/keys/{project_id}")
    Call<String> fetchProjectDetails(@Header("one_flow_key") String headerKey,@Path("project_id") String projectKey);

    @POST//("v1/2021-06-15/project_users/log_user")
    Call<OFGenericResponse<OFLogUserResponse>> logUser(@Header("one_flow_key") String headerKey, @Body OFLogUserRequest request, @Url String url);



   /* @POST("v1/2021-06-15/json")
    Call<GenericResponse<AddUserResponse>> uploadFile(@Body AddUserRequest aur);*/


}