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

import com.oneflow.analytics.model.events.OFEventAPIRequest;
import com.oneflow.analytics.model.events.OFRecordEventsTab;
import com.oneflow.analytics.model.events.OFRecordEventsTabToAPI;
import com.oneflow.analytics.repositories.OFEventAPIRepo;
import com.oneflow.analytics.repositories.OFEventDBRepoKT;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OFEventController implements OFMyResponseHandlerOneFlow {

    Context mContext;
    String tag = this.getClass().getName();

    private static OFEventController ec;

    public static OFEventController getInstance(Context mContext) {
        if (ec == null) {
            ec = new OFEventController(mContext);
        }
        return ec;
    }

    private OFEventController(Context mContext) {
        this.mContext = mContext;
    }

    public void storeEventsInDB(String eventName, HashMap<String, Object> eventValue, int value) {

        OFHelper.v(tag, "Oneflow records inserted [" + eventName + "]value[" + eventValue + "]");
        new OFEventDBRepoKT().insertEvents(mContext, eventName, eventValue, value, this, OFConstants.ApiHitType.insertEventsInDB);

    }

    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj, Long reserve, String reserved, Object obj2, Object obj3) {

        switch (hitType) {
            case insertEventsInDB:
                OFHelper.v(tag, "Oneflow records inserted [" + (obj) + "]");
                if (reserved.equalsIgnoreCase(OFConstants.AUTOEVENT_SURVEYIMPRESSION)) {
                    OFHelper.v(tag, "Oneflow found survey impression[" + (obj) + "]");
                    new OFEventDBRepoKT().fetchEvents(mContext, this, OFConstants.ApiHitType.fetchEventsFromDB);
                }
                break;
            case fetchEventsFromDB:

                OFHelper.v(this.getClass().getName(), "OneFlow fetchEventsFromDB came back");

                if (obj != null) {
                    ArrayList<OFRecordEventsTab> list = (ArrayList<OFRecordEventsTab>) obj;
                    OFHelper.v(this.getClass().getName(), "OneFlow fetchEventsFromDB list received size[" + list.size() + "]");
                    //Preparing list to send api
                    if (!list.isEmpty()) {
                        Integer[] ids = new Integer[list.size()];
                        int i = 0;
                        ArrayList<OFRecordEventsTabToAPI> retListToAPI = new ArrayList<>();
                        OFRecordEventsTabToAPI retMain;
                        for (OFRecordEventsTab ret : list) {
                            retMain = new OFRecordEventsTabToAPI();
                            retMain.set_id(ret.getUuid());
                            retMain.setPlatform("a");
                            retMain.setEventName(ret.getEventName());
                            retMain.setTime(ret.getTime());
                            retMain.setDataMap(ret.getDataMap());
                            retListToAPI.add(retMain);

                            ids[i++] = ret.getId();
                        }

                        if (OFHelper.isConnected(mContext)) {
                            OFEventAPIRequest ear = new OFEventAPIRequest();
                            ear.setUserId(OFOneFlowSHP.getInstance(mContext).getUserDetails().getAnalytic_user_id());
                            ear.setEvents(retListToAPI);
                            OFOneFlowSHP.getInstance(mContext).storeValue(OFConstants.SHP_EVENTS_DELETE_PENDING,true);
                            OFHelper.v(this.getClass().getName(), "OneFlow fetchEventsFromDB request prepared");
                            OFEventAPIRepo.sendLogsToApi(OFOneFlowSHP.getInstance(mContext).getStringValue(OFConstants.APPIDSHP), ear, this, OFConstants.ApiHitType.sendEventsToAPI, ids);
                        }
                    }
                }
                break;
            case sendEventsToAPI:
                if (obj != null) {
                    //Events has been sent to api now deleting local records
                    Integer[] ids1 = (Integer[]) obj;
                    new OFEventDBRepoKT().deleteEvents(mContext, ids1, this, OFConstants.ApiHitType.deleteEventsFromDB);
                }
                break;
            case deleteEventsFromDB:
                if (obj != null) {
                    OFOneFlowSHP.getInstance(mContext).storeValue(OFConstants.SHP_EVENTS_DELETE_PENDING,false);
                    OFHelper.v(this.getClass().getName(), "OneFlow events delete count[" + (obj) + "]");
                    Intent intent = new Intent("events_submitted");
                    intent.putExtra("size", String.valueOf(obj));
                    mContext.sendBroadcast(intent);
                }
                break;
            default:
                break;
        }
    }
}
