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

package com.oneflow.analytics;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oneflow.analytics.adapter.OFSurveyListAdapter;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OFSurveyList extends OFSDKBaseActivity {

    RecyclerView listOfSurvey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_survey_list);

        listOfSurvey = (RecyclerView) findViewById(R.id.list_of_survey);

        ArrayList<OFGetSurveyListResponse> slr = OFOneFlowSHP.getInstance(this).getSurveyList();
        OFSurveyListAdapter addb = new OFSurveyListAdapter(slr, clickListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listOfSurvey.setLayoutManager(linearLayoutManager);
        listOfSurvey.setAdapter(addb);



    }

    View.OnClickListener clickListener = v -> {
        String tag = (String) v.getTag();



        HashMap<String,String> mapvalues = new HashMap<>();
        mapvalues.put("testKey1","testValue1");
        mapvalues.put("testKey2","testValue2");
        mapvalues.put("testKey3","testValue3");
        OneFlow.recordEvents(tag,mapvalues);
    };
}