package com.oneflow.analytics.utils;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.oneflow.analytics.OFSurveyLander;

import org.json.JSONObject;

public class OFDelayedSurveyCountdownTimer extends CountDownTimer{

    Context mContext;
    static OFDelayedSurveyCountdownTimer cdt;
    static int count = 0;
    Intent intent1;

    boolean isOpenSurvey = false;
    String triggerEventName;
    String eventData;
    public static synchronized OFDelayedSurveyCountdownTimer getInstance(Context context, Long duration, Long interval, Intent intent) {
        if (cdt == null) {
            cdt = new OFDelayedSurveyCountdownTimer(context, duration, interval,intent);
        }
        return cdt;
    }
    private OFDelayedSurveyCountdownTimer(Context context, Long duration, Long interval, Intent delayedIntent) {
        super(duration, interval);
        this.mContext = context;
        this.intent1 = delayedIntent;
    }

    public void openSurvey(boolean isOpenSurvey,String eventData,String triggerEventName){
        this.isOpenSurvey = isOpenSurvey;
        this.eventData = eventData;
        this.triggerEventName = triggerEventName;
    }

    @Override
    public void onTick(long millisUntilFinished) {
       OFHelper.v("OFDelayedSurveyCountdownTimer", "1Flow waiting for survey to start");

    }

    @Override
    public void onFinish() {
        OFHelper.v("OFDelayedSurveyCountdownTimer", "1Flow init survey");
        if(isOpenSurvey){
            OFSurveyLander ofSurveyLander = new OFSurveyLander(mContext);
            ofSurveyLander.setData(this.eventData,this.triggerEventName);
        }else{
            mContext.getApplicationContext().startActivity(intent1);
        }
    }

}
