package com.oneflow.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import com.oneflow.analytics.customwidgets.OFCustomEditText;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class OFSecondActivity extends AppCompatActivity {

    String tag = this.getClass().getName();

    OFCustomTextView initSurvey;
    OFCustomTextView addMore;
    OFCustomEditText eventName;
    LinearLayout listOfParams;
    ArrayList<com.oneflow.analytics.model.survey.OFGetSurveyListResponse> slr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.of_activity_second);

        addMore = (OFCustomTextView) findViewById(R.id.add_more);
        eventName = (OFCustomEditText) findViewById(R.id.event_name_text);

        initSurvey = (OFCustomTextView) findViewById(R.id.init_survey);
        initSurvey.setOnClickListener(submitSurvey);
        listOfParams = (LinearLayout) findViewById(R.id.list_of_params);


        slr = new ArrayList<>();
        addMore.setOnClickListener(view -> {
            if(validateFirst()) {
                addMoreToParams();
            }else{
                OFHelper.makeText(OFSecondActivity.this,"Please fill all the fields",1);
            }
        });

        IntentFilter inf = new IntentFilter();
        inf.addAction("survey_finished");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(listFetched, inf,RECEIVER_EXPORTED);
        }
    }

    public boolean validateFirst(){
        try {

            View v = listOfParams.getChildAt(listOfParams.getChildCount() - 1);
            String key1 = ((OFCustomEditText) v.findViewById(R.id.key1)).getText().toString();
            String value1 = ((OFCustomEditText) v.findViewById(R.id.value1)).getText().toString();

            if (OFHelper.validateString(key1).equalsIgnoreCase("na") || OFHelper.validateString(value1).equalsIgnoreCase("na")) {
                return false;
            }
        }catch(Exception ex){
            // error
        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(validateFirst()) {
            addMoreToParams();
        }else{
            OFHelper.makeText(OFSecondActivity.this,"Please fill all the fields",1);
        }
    }
    public void addMoreToParams(){
        View itemView = LayoutInflater.from(this).inflate(R.layout.dialog_survey_add_fields_new, null, false);
        listOfParams.addView(itemView);
    }

    HashMap<String,Object> params;
    public void setupParams(){
        int count = listOfParams.getChildCount();
        params = new HashMap<>();
        for(int i=0;i<count;i++){
            View v = listOfParams.getChildAt(i);
            String key1 = ((OFCustomEditText) v.findViewById(R.id.key1)).getText().toString().trim();
            String value1 = ((OFCustomEditText) v.findViewById(R.id.value1)).getText().toString().trim();
            if (!(OFHelper.validateString(key1).equalsIgnoreCase("na") || OFHelper.validateString(value1).equalsIgnoreCase("na"))) {
                if(value1.equalsIgnoreCase("true") || value1.equalsIgnoreCase("false")){
                    params.put(key1, Boolean.parseBoolean(value1.toLowerCase()));
                }else{
                    params.put(key1, value1);
                }
            }
            OFHelper.v(tag,"1Flow params1 ["+params+"]");
        }
    }

    View.OnClickListener submitSurvey = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                if(OFHelper.validateString(eventName.getText().toString()).equalsIgnoreCase("na")){
                    OFHelper.makeText(OFSecondActivity.this,"Please enter event name",1);
            }else {
                  setupParams();
                    OFHelper.v(tag,"1Flow params isEmpty["+params.isEmpty()+"]["+params+"]");
                  if(params.isEmpty()){
                      OneFlow.recordEvents(eventName.getText().toString());
                  }else{
                      OFHelper.v(tag,"1Flow params ["+params+"]");
                      OneFlow.recordEvents(eventName.getText().toString(),params);
                  }
            }
        }
    };
    BroadcastReceiver listFetched = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OFHelper.v(tag, "OneFlow reached receiver");


            if (intent.getAction().equalsIgnoreCase("survey_finished")) {
                String triggerName = intent.getStringExtra("surveyDetail");
                OFHelper.v(tag, "OneFlow Submitted survey data[" + triggerName + "]");
                listOfParams.removeAllViews();
                eventName.setText("");
                addMoreToParams();
            }

        }
    };

}