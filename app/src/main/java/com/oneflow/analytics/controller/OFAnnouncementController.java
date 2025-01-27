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
import com.oneflow.analytics.OFAnnouncementActivityBannerBottom;
import com.oneflow.analytics.OFAnnouncementActivityBannerTop;
import com.oneflow.analytics.OFAnnouncementActivityFullScreen;
import com.oneflow.analytics.OFAnnouncementActivityModel;
import com.oneflow.analytics.OFAnnouncementActivitySlideBottom;
import com.oneflow.analytics.OFAnnouncementActivitySlideTop;
import com.oneflow.analytics.OFAnnouncementLanderActivity;
import com.oneflow.analytics.OFAnnouncementLanderService;
import com.oneflow.analytics.OFSDKBaseActivity;
import com.oneflow.analytics.OFSurveyLanderService;
import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.model.announcement.OFAnnouncementIndex;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementResponse;
import com.oneflow.analytics.repositories.OFAnnouncementRepo;
import com.oneflow.analytics.repositories.OFEventDBRepoKT;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OFAnnouncementController implements OFMyResponseHandlerOneFlow {

    Context mContext;
    static OFAnnouncementController sc;
    JSONArray eventMapArray;

    private OFAnnouncementController(Context context) {
        this.mContext = context;
    }

    public static OFAnnouncementController getInstance(Context context) {
        OFHelper.v("AnnouncementController", "OneFlow reached AnnouncementController ["+sc+"]");
        if (sc == null) {
            sc = new OFAnnouncementController(context);
        }
        return sc;
    }

    public void getAnnouncementFromAPI() {
        OFHelper.v("AnnouncementController", "OneFlow reached AnnouncementController 0");
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        String userId = shp.getUserDetails().getAnalytic_user_id();
        OFHelper.v("AnnouncementController userId : ", userId);
        OFHelper.v("AnnouncementController project Key : ", shp.getStringValue(OFConstants.APPIDSHP));
        OFAnnouncementRepo.getAnnouncement(shp.getStringValue(OFConstants.APPIDSHP), this, OFConstants.ApiHitType.fetchAnnouncementFromAPI,shp.getUserDetails().getAnalytic_user_id());
    }

    public void getAnnouncementDetailFromAPI(String ids, String reserved) {
        OFHelper.v("AnnouncementController", "OneFlow reached AnnouncementController 1");
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        OFAnnouncementRepo.getAnnouncementDetail(shp.getStringValue(OFConstants.APPIDSHP), this, OFConstants.ApiHitType.fetchAnnouncementDetailFromAPI,ids,reserved);
    }

    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj, Long reserve, String reserved, Object obj2, Object obj3) {

        OFHelper.v("AnnouncementController","OneFlow onReceived called type["+hitType+"]");

        switch (hitType) {
            case fetchAnnouncementFromAPI:
                OFHelper.v("AnnouncementController", "OneFlow announcement received [" + reserved + "]");
                if (obj != null) {
                    OFHelper.v("AnnouncementController", "OneFlow announcement received1 [" + new Gson().toJson(obj) + "]");
                    OFGetAnnouncementResponse getAnnouncementList = (OFGetAnnouncementResponse) obj;
                    OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
                    shp.setAnnouncementResponse(getAnnouncementList);

                    ArrayList<String> inAppIdList = new ArrayList<>();
                    ArrayList<String> inBoxIdList = new ArrayList<>();

                    if(getAnnouncementList.getAnnouncements() != null){

                        for (int i = 0; i < getAnnouncementList.getAnnouncements().getInApp().size(); i++) {
                            boolean isSeen = getAnnouncementList.getAnnouncements().getInApp().get(i).getSeen();
                            if(isSeen){
                                inAppIdList.add(getAnnouncementList.getAnnouncements().getInApp().get(i).getId());
                            }
                        }


                        for (int i = 0; i < getAnnouncementList.getAnnouncements().getInbox().size(); i++) {
                            boolean isSeen = getAnnouncementList.getAnnouncements().getInbox().get(i).getSeen();
                            if(isSeen){
                                inBoxIdList.add(getAnnouncementList.getAnnouncements().getInbox().get(i).getId());
                            }
                        }

                    }

                    shp.setSeenInAppAnnounceList(inAppIdList);
                    shp.setSeenInboxAnnounceList(inBoxIdList);
//                    if(!getAnnouncementList.getAnnouncements().getInbox().isEmpty()){
//                        List<String> idArray = new ArrayList<>();
//                        for (int i = 0; i < getAnnouncementList.getAnnouncements().getInbox().size(); i++) {
//                            idArray.add(getAnnouncementList.getAnnouncements().getInbox().get(i).getId());
//                        }
//                        OFHelper.v("AnnouncementController", "OneFlow announcement detail ids [" + idArray.size() + "]");
//                        getAnnouncementDetailFromAPI(TextUtils.join(",", idArray));
//                    }
//                    checkAnnouncement(getAnnouncementList.getAnnouncements().getInApp());

                    //Enabled again on 13/June/22
                    new OFEventDBRepoKT().fetchEventsBeforeSurvey(mContext, this, OFConstants.ApiHitType.fetchEventsBeforSurveyFetched);
                }
                break;
            case fetchAnnouncementDetailFromAPI:
                OFHelper.v("AnnouncementController", "OneFlow announcement detail received [" + reserved + "]");
                if (obj != null) {
                    if(reserved.equals("1")){
                        ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses = (ArrayList<OFGetAnnouncementDetailResponse>) obj;
                        if(!getAnnouncementDetailResponses.isEmpty()){
                            triggerInboxAnnouncement(getAnnouncementDetailResponses);
                        }
                    }else{
                        ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses = (ArrayList<OFGetAnnouncementDetailResponse>) obj;
                        OFHelper.v("AnnouncementController", "OneFlow announcement detail received [" + new Gson().toJson(getAnnouncementDetailResponses) + "]");
                        if(!getAnnouncementDetailResponses.isEmpty()){
                            String style = checkStyle(getAnnouncementDetailResponses.get(0).getId());
                            OFHelper.v("AnnouncementController", "OneFlow announcement detail received style " + style);
                            if(style.equalsIgnoreCase("modal")){
                                triggerAnnouncement(getAnnouncementDetailResponses);
                            }else if(style.equalsIgnoreCase("banner_top")){
                                triggerAnnouncementBannerTop(getAnnouncementDetailResponses);
                            }else if(style.equalsIgnoreCase("banner_bottom")){
                                triggerAnnouncementBannerBottom(getAnnouncementDetailResponses);
                            }else if(style.equalsIgnoreCase("top_left")){
                                triggerAnnouncementSideTop(getAnnouncementDetailResponses);
                            }else if(style.equalsIgnoreCase("top_right")){
                                triggerAnnouncementSideTop(getAnnouncementDetailResponses);
                            }else if(style.equalsIgnoreCase("bottom_left")){
                                triggerAnnouncementSideBottom(getAnnouncementDetailResponses);
                            }else if(style.equalsIgnoreCase("bottom_right")){
                                triggerAnnouncementSideBottom(getAnnouncementDetailResponses);
                            }
                        }else{
                            OneFlow.callEvent();
                        }
                    }
                }
                break;
            case fetchEventsBeforSurveyFetched:
                try {
                    if (obj != null) {
                        String[] name = (String[]) obj;
                        OFHelper.v("OFAnnouncementController", "OneFlow events before survey found[" + Arrays.asList(name) + "]length[" + name.length + "]");
                        if (name.length > 0) {
                            HashMap<String,Object> eventMapLocal;
                            eventMapArray = new JSONArray();


                            for(String nameLocal: name){
                                OFHelper.v("OFAnnouncementController", "OneFlow events for["+nameLocal+"]");
                                eventMapLocal = new HashMap<>();
                                eventMapLocal.put("name", nameLocal);
                                eventMapLocal.put("timestamp", System.currentTimeMillis() / 1000);
                                eventMapArray.put(new JSONObject(eventMapLocal));

                            }
                            triggeredStartAppSurveyOrAnnouncement();
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

    private void triggeredStartAppSurveyOrAnnouncement(){
        ArrayList<OFAnnouncementIndex> inAppList = new ArrayList<>();
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        if (shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getAnnouncements() != null) {
            for (int i = 0; i < shp.getAnnouncementResponse().getAnnouncements().getInApp().size(); i++) {

                OFAnnouncementIndex announcementIndex = shp.getAnnouncementResponse().getAnnouncements().getInApp().get(i);

                boolean isAvailable = false;
                for (int i1 = 0; i1 < shp.getSeenInAppAnnounceList().size(); i1++) {
                    if (shp.getSeenInAppAnnounceList().get(i1).equals(announcementIndex.getId())) {
                        OFHelper.v("OFAnnouncementController", "1Flow announcement check[" + announcementIndex.getId() + "]");
                        isAvailable = true;
                        break;
                    }
                }

                if (announcementIndex.getStatus().equalsIgnoreCase("active") && !isAvailable) {
                    inAppList.add(announcementIndex);
                }
            }
        }
        OFHelper.v("OFAnnouncementController", "1Flow announcement1 check[" + inAppList + "]");
        if (!inAppList.isEmpty()) {
            boolean isRunning = OFHelper.isServiceRunning(mContext, OFAnnouncementLanderService.class);
            if (isRunning) {
                return;
            }

            Intent intent = new Intent(mContext, OFAnnouncementLanderService.class);
            intent.putExtra("listData", inAppList);
            intent.putExtra("eventData", eventMapArray.toString());
            mContext.startService(intent);
        }else{
            boolean isRunning = OFHelper.isServiceRunning(mContext, OFSurveyLanderService.class);
            if(isRunning){
                return;
            }

            Intent intent = new Intent(mContext, OFSurveyLanderService.class);
            intent.putExtra("eventName", "");
            intent.putExtra("eventData", eventMapArray.toString());
            mContext.startService(intent);
        }
    }

    private void triggerAnnouncement(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");
        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFAnnouncementActivityModel.class);

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("announcementData", getAnnouncementDetailResponses);

        if (!OFSDKBaseActivity.isActive) {
            mContext.getApplicationContext().startActivity(surveyIntent);
        }
    }

    private void triggerAnnouncementBannerTop(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");
        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFAnnouncementActivityBannerTop.class);

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("announcementData", getAnnouncementDetailResponses);

        if (!OFSDKBaseActivity.isActive) {
            mContext.getApplicationContext().startActivity(surveyIntent);
        }
    }

    private void triggerAnnouncementBannerBottom(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");
        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFAnnouncementActivityBannerBottom.class);

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("announcementData", getAnnouncementDetailResponses);

        if (!OFSDKBaseActivity.isActive) {
            mContext.getApplicationContext().startActivity(surveyIntent);
        }
    }

    private void triggerAnnouncementSideTop(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");
        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFAnnouncementActivitySlideTop.class);

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("announcementData", getAnnouncementDetailResponses);

        if (!OFSDKBaseActivity.isActive) {
            mContext.getApplicationContext().startActivity(surveyIntent);
        }
    }

    private void triggerAnnouncementSideBottom(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");
        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFAnnouncementActivitySlideBottom.class);

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("announcementData", getAnnouncementDetailResponses);

        if (!OFSDKBaseActivity.isActive) {
            mContext.getApplicationContext().startActivity(surveyIntent);
        }
    }

    private void triggerInboxAnnouncement(ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses){

        OFHelper.v("1Flow", "1Flow activity reached running[" + OFSDKBaseActivity.isActive + "]");
        final Intent surveyIntent = new Intent(mContext.getApplicationContext(), OFAnnouncementActivityFullScreen.class);

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("announcementData", getAnnouncementDetailResponses);

        if (!OFSDKBaseActivity.isActive) {
            mContext.getApplicationContext().startActivity(surveyIntent);
        }
    }

    public String checkStyle(String id){
        String style = "";

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(mContext);
        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getAnnouncements() != null){
            for (int i = 0; i < shp.getAnnouncementResponse().getAnnouncements().getInApp().size(); i++) {
                OFAnnouncementIndex announcementIndex = shp.getAnnouncementResponse().getAnnouncements().getInApp().get(i);
                if(announcementIndex.getId().equalsIgnoreCase(id)){
                    style = announcementIndex.getInApp().getStyle();
                }
            }
        }

        return style;
    }
}
