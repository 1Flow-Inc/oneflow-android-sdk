/*
 *  Copyright 2021 1Flow, Inc.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.oneflow.analytics.model;

import com.oneflow.analytics.model.adduser.OFAddUserReq;
import com.oneflow.analytics.model.adduser.OFAddUserResponse;
import com.oneflow.analytics.model.adduser.OFFirebaseTokenRequest;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementResponse;
import com.oneflow.analytics.model.events.OFEventAPIRequest;
import com.oneflow.analytics.model.loguser.OFLogUserRequest;
import com.oneflow.analytics.model.loguser.OFLogUserResponse;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.model.survey.OFSurveyUserInput;

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

   // @POST("add-user")
    @POST("v3/user")
    Call<OFGenericResponse<OFAddUserResponse>> addUserComman(@Header("one_flow_key") String headerKey, @Body OFAddUserReq aur);

    @GET("v3/survey")
    Call<OFGenericResponse<ArrayList<OFGetSurveyListResponse>>> getSurvey(@Header("one_flow_key") String headerKey,
                                                                          @Query("user_id") String userId,
                                                                          @Query("language_code") String languageCode,
                                                                          @Query("platform") String platform,
                                                                          @Query("min_version") String minVersion




    );



   // @POST("add-responses")
    @POST("v3/response")
    Call<OFGenericResponse> submitSurveyUserResponse(@Header("one_flow_key") String headerKey, @Body OFSurveyUserInput aur);

    //@POST("v3/identify")
    @GET("v3/survey/{survey_id}")
    Call<OFGenericResponse<OFGetSurveyListResponse>> getSurveyWithoutCondition(@Header("one_flow_key") String headerKey,
                                                                               @Path("survey_id") String surveyId,
                                                                               @Query("user_id") String userId,
                                                                               @Query("language_code") String languageCode,
                                                                               @Query("platform") String platform,
                                                                               @Query("min_version") String minVersion


    );

    ///@POST("v1/2021-06-15/events/bulk")
    //@POST("events")
    @POST("v3/track")
    Call<OFGenericResponse> uploadAllUnSyncedEvents(@Header("one_flow_key") String headerKey, @Body OFEventAPIRequest ear);

   // @POST("log-user")
    @POST("v3/identify")
    Call<OFGenericResponse<OFLogUserResponse>> logUser(@Header("one_flow_key") String headerKey, @Body OFLogUserRequest request);


    @GET
    Call<String> getJSMethod(@Url String url);

    @GET("v3/announcements")
    Call<OFGenericResponse<OFGetAnnouncementResponse>> getAnnouncement(@Header("one_flow_key") String headerKey,
                                                                       @Query("user_id") String userId,
                                                                       @Query("platform") String platform);

    @GET("v3/announcements/inbox")
    Call<OFGenericResponse<ArrayList<OFGetAnnouncementDetailResponse>>> getAnnouncementDetail(@Header("one_flow_key") String headerKey,
                                                                                              @Query("ids") String userId);


    @POST("details/added")
    Call<OFGenericResponse> pushToken(@Body OFFirebaseTokenRequest aur);

}
