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

package com.oneflow.analytics.controller;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oneflow.analytics.OFSDKBaseActivity;
import com.oneflow.analytics.OFSurveyActivityBannerBottom;
import com.oneflow.analytics.OFSurveyActivityBannerTop;
import com.oneflow.analytics.OFSurveyActivityBottom;
import com.oneflow.analytics.OFSurveyActivityCenter;
import com.oneflow.analytics.OFSurveyActivityFullScreen;
import com.oneflow.analytics.OFSurveyActivityTop;
import com.oneflow.analytics.OFSurveyLanderService;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.model.survey.OFThrottlingConfig;
import com.oneflow.analytics.repositories.OFEventDBRepoKT;
import com.oneflow.analytics.repositories.OFSurvey;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OFSurveyController implements OFMyResponseHandlerOneFlow {

    Context mContext;
    static OFSurveyController sc;

    private OFSurveyController(Context context) {
        this.mContext = context;
    }

    public static OFSurveyController getInstance(Context context) {
        OFHelper.v("SurveyController", "OneFlow reached SurveyController ["+sc+"]");
        if (sc == null) {
            sc = new OFSurveyController(context);
        }
        return sc;
    }

    public void getSurveyFromAPI() {
        OFHelper.v("SurveyController", "OneFlow reached SurveyController 0");
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        OFSurvey.getSurvey(shp.getStringValue(OFConstants.APPIDSHP), this, OFConstants.ApiHitType.fetchSurveysFromAPI,shp.getUserDetails().getAnalytic_user_id(),OFConstants.currentVersion);
    }

    HashMap<String, Class> activityName;
    public void setUpHashForActivity() {
        activityName = new HashMap<>();

        activityName.put("top-banner", OFSurveyActivityBannerTop.class);
        activityName.put("bottom-banner", OFSurveyActivityBannerBottom.class);

        activityName.put("fullscreen", OFSurveyActivityFullScreen.class);

        activityName.put("top-left", OFSurveyActivityTop.class);
        activityName.put("top-center", OFSurveyActivityTop.class);
        activityName.put("top-right", OFSurveyActivityTop.class);

        activityName.put("middle-left", OFSurveyActivityCenter.class); //name changed
        activityName.put("middle-center", OFSurveyActivityCenter.class); //name changed
        activityName.put("middle-right", OFSurveyActivityCenter.class); //name changed

        activityName.put("bottom-left", OFSurveyActivityBottom.class);
        activityName.put("bottom-center", OFSurveyActivityBottom.class); //default one
        activityName.put("bottom-right", OFSurveyActivityBottom.class);


    }
    JSONArray eventMapArray;
    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj, Long reserve, String reserved, Object obj2, Object obj3) {

        OFHelper.v("SurveyController","OneFlow onReceived called type["+hitType+"]");

        switch (hitType) {
            case fetchSurveysFromAPI:
                OFHelper.v("SurveyController", "OneFlow survey received throttling[" + reserved + "]");
                if (obj != null) {

                    ArrayList<OFGetSurveyListResponse> surveyListResponse = (ArrayList<OFGetSurveyListResponse>) obj;
                    OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
                    if (!OFHelper.validateString(reserved).equalsIgnoreCase("NA")) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.serializeNulls();
                        Gson gson = builder.setPrettyPrinting().create();


                        OFThrottlingConfig ofThrottlingConfig = gson.fromJson(reserved, OFThrottlingConfig.class);
                        shp.setThrottlingConfig(ofThrottlingConfig);
                    }
                    setupGlobalTimerToDeactivateThrottlingLocally();

                    if (surveyListResponse != null) {

                        shp.setSurveyList(surveyListResponse);
                        shp.storeValue(OFConstants.SHP_SURVEY_FETCH_TIME,System.currentTimeMillis());
                        Intent intent = new Intent("survey_list_fetched");
                        mContext.sendBroadcast(intent);

                    } else {
                        Intent intent = new Intent("survey_list_fetched");
                        intent.putExtra("msg", "No survey received");
                        mContext.sendBroadcast(intent);
                        if (OFConstants.MODE.equalsIgnoreCase("dev")) {
                            OFHelper.makeText(mContext, reserved, 1);
                        }
                    }
                    //Enabled again on 13/June/22
                    new OFEventDBRepoKT().fetchEventsBeforeSurvey(mContext, this, OFConstants.ApiHitType.fetchEventsBeforSurveyFetched);
                }
                break;
            case fetchEventsBeforSurveyFetched:
                try {
                    if (obj != null) {
                        String[] name = (String[]) obj;
                        OFHelper.v("SurveyController", "OneFlow events before survey found[" + Arrays.asList(name) + "]length[" + name.length + "]");
                        if (name.length > 0) {
                            HashMap<String,Object> eventMapLocal;
                            eventMapArray = new JSONArray();


                            for(String nameLocal: name){
                                OFHelper.v("SurveyController", "OneFlow events for["+nameLocal+"]");
                                eventMapLocal = new HashMap<>();
                                eventMapLocal.put("name", nameLocal);
                                eventMapLocal.put("timestamp", System.currentTimeMillis() / 1000);
                                eventMapArray.put(new JSONObject(eventMapLocal));

                            }
                            triggerSurveyNew();
                        }
                    }
                }
               catch(Exception ex){
                    // ex
               }
                break;
            default:
                break;
        }
    }

    private void triggerSurveyNew(){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");

        boolean isRunning = OFHelper.isServiceRunning(mContext, OFSurveyLanderService.class);
        if(isRunning){
            return;
        }

        Intent intent = new Intent(mContext, OFSurveyLanderService.class);
        intent.putExtra("eventName", "");
        intent.putExtra("eventData", eventMapArray.toString());
        mContext.startService(intent);

//        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFFirstLanderActivity.class);
//
//        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        surveyIntent.putExtra("eventData", eventMapArray.toString());
//
//        OFHelper.v("1Flow", "1Flow activity running 4[" + OFSDKBaseActivity.isActive + "]");
//
//        if (!OFSDKBaseActivity.isActive) {
//            mContext.getApplicationContext().startActivity(surveyIntent);
//        }
    }
    private void setupGlobalTimerToDeactivateThrottlingLocally() {


        OFHelper.v("OFSurveyController", "1Flow deactivate called ");
        OFThrottlingConfig config = OFOneFlowSHP.getInstance(mContext).getThrottlingConfig();
        OFHelper.v("OFSurveyController", "1Flow deactivate called config activated[" + config.isActivated() + "]globalTime[" + config.getGlobalTime() + "]activatedBy[" + config.getActivatedById() + "]");
        if (config.getGlobalTime() != null && config.getGlobalTime() > 0) {
            setThrottlingAlarm(config);
        } else {
            OFHelper.v("OFSurveyController", "1Flow deactivate called at else");
            config.setActivated(false);
            config.setActivatedById(null);
            OFOneFlowSHP.getInstance(mContext).setThrottlingConfig(config);
        }



    }
    public void setThrottlingAlarm(long throttlingLifeTime) {


        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        shp.storeValue(OFConstants.SHP_THROTTLING_TIME, throttlingLifeTime);


    }
    public void setThrottlingAlarm(OFThrottlingConfig config) {
        OFHelper.v("OFSurveyController", "1Flow Setting ThrottlingAlarm [" + config.getGlobalTime() + "]");

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        shp.storeValue(OFConstants.SHP_THROTTLING_TIME, config.getGlobalTime() * 1000 + System.currentTimeMillis());
    }
}
