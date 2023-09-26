package com.oneflow.analytics.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OFFilterSurveys extends Thread {
    Context context;
    OFMyResponseHandlerOneFlow responseHandler;
    OFConstants.ApiHitType type;
    String eventNames;

    public OFFilterSurveys(Context context, OFMyResponseHandlerOneFlow responseHandler, OFConstants.ApiHitType type, String eventNames) {
        this.context = context;
        this.type = type;
        this.eventNames = eventNames;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        super.run();
        getFilterList();
    }

    String tag = this.getClass().getName();
    public void getFilterList() {
        ArrayList<OFGetSurveyListResponse> currentList = OFOneFlowSHP.getInstance(context).getSurveyList();
        ArrayList<OFGetSurveyListResponse> returningList = new ArrayList<>();

        ArrayList<String> closedSurveyList = OFOneFlowSHP.getInstance(context).getClosedSurveyList();
        boolean hasClosed = false;

        if (currentList != null) {
            OFHelper.v(tag, "1Flow actual size[" + currentList.size() + "]");
            for (OFGetSurveyListResponse gslrLocal : currentList) {


                boolean availability = checkSurveyAvailability(gslrLocal);
                if (availability) {
                    OFHelper.v(tag, "1Flow actual found true");

                    hasClosed = false;
                    if (closedSurveyList != null) {
                        hasClosed = closedSurveyList.contains(gslrLocal.get_id()); //checking locally if survey has already been completed
                    }

                    if (!(gslrLocal.getSurveySettings().getClosedAsFinished() && hasClosed)) {
                        returningList.add(gslrLocal);
                    }
                }
            }
            OFHelper.v(tag, "1Flow actual size 1[" + returningList.size() + "]");
        }
        responseHandler.onResponseReceived(type, returningList, 0l, "", eventNames, null);

    }

    private Boolean checkSurveyAvailability(OFGetSurveyListResponse gslr) {


        Long submittedSurvey = OFOneFlowSHP.getInstance(context).getLongValue(gslr.get_id());
        OFHelper.v(tag, "1Flow actual at checkSurveyAvailability[" + new Gson().toJson(gslr) + "]");
        boolean reSurvey = gslr.getSurveySettings().getResurvey_option();
        if (reSurvey) {
            if (gslr.getSurveySettings().getTriggerFilters() != null && !gslr.getSurveySettings().getTriggerFilters().isEmpty()) {
                OFHelper.v(tag, "1Flow checking list recursive [" + gslr.getSurveySettings().getTriggerFilters().get(0).getField() + "]");
            }
            Long totalInterval = 0l;
            Long diff = Calendar.getInstance().getTimeInMillis() - submittedSurvey;
            int diffDuration = 0;
            OFHelper.v("1Flow", "1Flow actual resurvey check diff[" + diff + "]retakeInputValue[" + gslr.getSurveySettings().getRetake_survey().getRetake_input_value() + "]");
            OFHelper.v("1Flow", "1Flow actual resurvey check retakeSelectValue[" + gslr.getSurveySettings().getRetake_survey().getRetake_select_value() + "]");
            diffDuration = (int) (diff / 1000);
            switch (gslr.getSurveySettings().getRetake_survey().getRetake_select_value()) {
                case "minutes":
                    totalInterval = gslr.getSurveySettings().getRetake_survey().getRetake_input_value() * 60;
                    break;
                case "hours":
                    totalInterval = gslr.getSurveySettings().getRetake_survey().getRetake_input_value() * 60 * 60;
                    break;
                case "days":
                    totalInterval = gslr.getSurveySettings().getRetake_survey().getRetake_input_value() * 24 * 60 * 60;
                    break;
                default:
                    OFHelper.v("1Flow", "1Flow actual retake_select_value is neither of minutes, hours or days");
            }
            OFHelper.v("1Flow", "1Flow actual resurvey check diffDuration[" + diffDuration + "]totalInterval[" + totalInterval + "]");
            if (diffDuration > totalInterval) {
                return true;
            }
        } else {
            if (gslr.getSurveySettings().getTriggerFilters() != null && !gslr.getSurveySettings().getTriggerFilters().isEmpty()) {
                OFHelper.v(tag, "1Flow checking list single use[" + gslr.getSurveySettings().getTriggerFilters().get(0).getField() + "]");
            }

            if (submittedSurvey <= 0) {
                return true;
            }
        }
        return false;
    }
}
