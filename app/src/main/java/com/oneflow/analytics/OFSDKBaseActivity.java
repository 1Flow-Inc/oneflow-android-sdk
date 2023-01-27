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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.fragment.OFSurveyQueFragment;
import com.oneflow.analytics.fragment.OFSurveyQueInfoFragment;
import com.oneflow.analytics.fragment.OFSurveyQueTextFragment;
import com.oneflow.analytics.fragment.OFSurveyQueThankyouFragment;
import com.oneflow.analytics.model.survey.OFDataLogic;
import com.oneflow.analytics.model.survey.OFFinishCallBack;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyChoises;
import com.oneflow.analytics.model.survey.OFSurveyFinishChild;
import com.oneflow.analytics.model.survey.OFSurveyFinishModel;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.model.survey.OFSurveyUserInput;
import com.oneflow.analytics.model.survey.OFSurveyUserInputKT;
import com.oneflow.analytics.model.survey.OFSurveyUserResponseChild;
import com.oneflow.analytics.repositories.OFLogUserDBRepoKT;
import com.oneflow.analytics.repositories.OFSurvey;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class OFSDKBaseActivity extends AppCompatActivity implements OFMyResponseHandlerOneFlow {


    String tag = this.getClass().getName();
    Window window;
    ProgressBar pagePositionPBar;
    public ImageView closeBtn;
    View slider;
    RelativeLayout sliderLayout, basePopupLayout, mainChildForBackground;
    FrameLayout fragmentView;
    ArrayList<OFSurveyUserResponseChild> surveyResponseChildren = null;
    public ArrayList<OFSurveyScreens> screens;
    Long inTime = 0l;
    String surveyClosingStatus = "finished";
    ArrayList<OFSurveyFinishModel> surveyFinishList;
    String surveyName = "";
    public OFSDKSettingsTheme sdkTheme;
    String triggerEventName = "";
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;
    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;
    LinearLayout waterMarkLayout;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("SurveyType", surveyItem);
        outState.putSerializable("SurveyTheme", sdkTheme);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try {
            surveyItem = (OFGetSurveyListResponse) savedInstanceState.getSerializable("SurveyType");
            sdkTheme = (OFSDKSettingsTheme) savedInstanceState.getSerializable("SurveyTheme");
        } catch (Exception ex) {

        }
        //position--;
    }

    OFGetSurveyListResponse surveyItem;
    boolean shouldFadeAway = false;

    @Override
    protected void onStart() {
        super.onStart();

        surveyItem = (OFGetSurveyListResponse) this.getIntent().getSerializableExtra("SurveyType");

        surveyName = surveyItem.getName();
        screens = surveyItem.getScreens();
        triggerEventName = this.getIntent().getStringExtra("eventName");//surveyItem.getTrigger_event_name();
        // Helper.makeText(getApplicationContext(),"Size ["+screens.size()+"]",1);
        setProgressMax(surveyItem.getScreens().size()); // -1 for excluding thankyou page from progress bar; 2-sept-2022 showing progressbar at thankyou page
        selectedSurveyId = surveyItem.get_id();
        OFHelper.v(this.getClass().getName(),"OneFlow surveyId["+selectedSurveyId+"]");
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //closed survey logic for storage.
                OFOneFlowSHP ofs = OFOneFlowSHP.getInstance(OFSDKBaseActivity.this);
                ArrayList<String> closedSurveyList = ofs.getClosedSurveyList();
                if (closedSurveyList == null) {
                    closedSurveyList = new ArrayList<>();
                }
                OFHelper.v(tag, "OneFlow close button clicked [" + surveyResponseChildren + "]position[" + position + "]size[" + screens.size() + "]");
                if (surveyResponseChildren == null || surveyResponseChildren.size() == 0) {

                    surveyClosingStatus = "skipped";
                    if (!closedSurveyList.contains(selectedSurveyId)) {
                        closedSurveyList.add(selectedSurveyId);
                        ofs.setClosedSurveyList(closedSurveyList);
                        OFEventController ec = OFEventController.getInstance(OFSDKBaseActivity.this);
                        HashMap<String, Object> mapValue = new HashMap<>();
                        mapValue.put("survey_id", selectedSurveyId);
                        ec.storeEventsInDB(OFConstants.AUTOEVENT_CLOSED_SURVEY, mapValue, 0);
                    }

                } else if (position == screens.size()) {
                    surveyClosingStatus = "finished";
                } else {
                    surveyClosingStatus = "closed";
                }

                if (position >= screens.size()) {
                    OFSDKBaseActivity.this.finish();
                } else {
                    finishSurveyNow();
                }

                // overridePendingTransition(0,R.anim.slide_down_dialog);
            }
        });


        themeColor = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        OFHelper.v(tag, "OneFlow color 1[" + themeColor + "]primaryColor[" + surveyItem.getStyle().getPrimary_color() + "]");
        try {

            String tranparancy = "";
            if (surveyItem.getStyle().getPrimary_color().length() > 6 && !surveyItem.getStyle().getPrimary_color().startsWith("#")) {
                tranparancy = surveyItem.getStyle().getPrimary_color().substring(6, 8);
            }
            String tempColor;
            if (!surveyItem.getStyle().getPrimary_color().startsWith("#")) {
                tempColor = surveyItem.getStyle().getPrimary_color().substring(0, 6);
            } else {
                tempColor = surveyItem.getStyle().getPrimary_color().substring(1, 7);
            }


            if (!surveyItem.getStyle().getPrimary_color().startsWith("#")) {
                themeColor = "#" + tranparancy + tempColor;//surveyItem.getStyle().getPrimary_color();
            } else {
                themeColor = tranparancy + tempColor;//surveyItem.getStyle().getPrimary_color();
            }
            OFHelper.v(tag, "OneFlow colors transparancy [" + tranparancy + "]tempColor[" + tempColor + "]themeColor[" + themeColor + "]");
        } catch (Exception ex) {
            //styleColor=""+getResources().getColor(R.color.colorPrimaryDark);
        }
        //styleColor=String.valueOf(getResources().getColor(R.color.colorPrimaryDark));
        OFHelper.v(tag, "OneFlow color after[" + themeColor + "]");
        try {
            pagePositionPBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
            //pagePositionPBar.getProgressDrawable().setColorFilter(Color.parseColor(styleColor.toString()), PorterDuff.Mode.DARKEN);
        } catch (NumberFormatException nfe) {
            OFHelper.e(tag, "OneFlow color number format exception after[" + nfe.getMessage() + "]");
            themeColor = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            pagePositionPBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
        }


        //This is temp remove in prod
        //surveyItem.getSurveySettings().getSdkTheme().setText_color(themeColor);

        sdkTheme = surveyItem.getSurveySettings().getSdkTheme();

        OFHelper.v(tag, "OneFlow sdkTheme [" + new Gson().toJson(sdkTheme) + "]");
        OFHelper.v(tag, "OneFlow sdkTheme Close[" + sdkTheme.getClose_button() + "]");
        OFHelper.v(tag, "OneFlow sdkTheme progress[" + sdkTheme.getProgress_bar() + "]");

        mainChildForBackground.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackground_color())));

        Drawable closeIcon = closeBtn.getDrawable();
        closeIcon.setColorFilter(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f), PorterDuff.Mode.SRC_ATOP);


        slider.setOnTouchListener(sliderTouchListener);
        sliderLayout.setOnTouchListener(sliderTouchListener);
        slider.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                OFHelper.v(tag, "OneFlow getAction[" + event.getAction() + "]");
                OFHelper.v(tag, "OneFlow getX[" + event.getX() + "]");

                return false;
            }
        });

        OFHelper.v(tag, "OneFlow sdkTheme 0[" + sdkTheme + "]widget[" + sdkTheme.getWidgetPosition() + "]");
        OFHelper.v(tag, "OneFlow sdkTheme 0 Close[" + sdkTheme.getClose_button() + "]");
        OFHelper.v(tag, "OneFlow sdkTheme 0 progress[" + sdkTheme.getProgress_bar() + "]");
        //New theme custome UI
        if (sdkTheme.getProgress_bar()) {
            pagePositionPBar.setVisibility(View.VISIBLE);
        } else {
            pagePositionPBar.setVisibility(View.GONE);
        }

        OFHelper.v(tag, "OneFlow position[" + position + "]size[" + sdkTheme.getClose_button() + "][" + shouldFadeAway + "]");
        if (sdkTheme.getClose_button()) {
            closeBtn.setVisibility(View.VISIBLE);
        } else {

            closeBtn.setVisibility(View.GONE);

        }


        if (sdkTheme.getDark_overlay()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0.25f); //0 for no dim to 1 for full dim
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0f); //0 for no dim to 1 for full dim
        }
        initFragment();
    }

    @Override
    public void onBackPressed() {
        if (false) {//!sdkTheme.getClose_button()) {

            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        OFHelper.v(tag, "OneFlow onPause called");
        //overridePendingTransition(0, R.anim.slide_down_dialog_sdk);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void finishSurveyNow() {
        OFHelper.v(tag, "OneFlow answer position after finishSurveyNow called");
        OFOneFlowSHP.getInstance(this).storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        //on close of this page considering survey is over, so submit the respones to api
        if (surveyResponseChildren != null) {
            if (surveyResponseChildren.size() > 0) {

                OFHelper.v(tag, "OneFlow input found submitting");
                prepareAndSubmitUserResposneNew();
            } else {
                OFHelper.v(tag, "OneFlow no input no submit");
                surveyFinishList = new ArrayList<>();
                Intent intent = new Intent("survey_finished");

                OFFinishCallBack finishData = new OFFinishCallBack();
                finishData.setStatus(surveyClosingStatus);
                finishData.setSurveyId(selectedSurveyId);
                finishData.setSurveyName(surveyName);
                finishData.setTriggerName(triggerEventName);
                finishData.setScreens(prepareFinishCallback());

                intent.putExtra(OFConstants.surveyDetail, new Gson().toJson(finishData));
                //OFHelper.v(tag,"OneFlow sending data ["+new Gson().toJson(finishData)+"]");
                sendBroadcast(intent);
                OFSDKBaseActivity.this.finish();
            }
        } else {
            OFHelper.v(tag, "OneFlow no input no submit");
            surveyFinishList = new ArrayList<>();
            Intent intent = new Intent("survey_finished");

            OFFinishCallBack finishData = new OFFinishCallBack();
            finishData.setStatus(surveyClosingStatus);
            finishData.setSurveyId(selectedSurveyId);
            finishData.setSurveyName(surveyName);
            finishData.setTriggerName(triggerEventName);
            finishData.setScreens(prepareFinishCallback());

            intent.putExtra(OFConstants.surveyDetail, new Gson().toJson(finishData));
            //OFHelper.v(tag,"OneFlow sending data ["+new Gson().toJson(finishData)+"]");
            sendBroadcast(intent);
            OFSDKBaseActivity.this.finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        OFHelper.v(tag, "OneFlow onStop called");
        /*OFOneFlowSHP ofs1 = new OFOneFlowSHP(this);
        ofs1.storeValue(OFConstants.SHP_SURVEY_RUNNING, true);*/
        //overridePendingTransition(0,R.anim.slide_down_dialog);
    }

    /**
     * Record User inputs
     *
     * @param screenID
     * @param answerIndex
     * @param answerValue
     */
    public void addUserResponseToList(String screenID, String answerIndex, String answerValue) {

        OFHelper.v(tag, "OneFlow answerindex position 0 [" + position + "][" + answerIndex + "]answervalue[" + answerValue + "]");
        if (surveyResponseChildren == null) {
            surveyResponseChildren = new ArrayList<>();
        }
        //this condition for skipping question
        if (answerIndex != null || answerValue != null) {
            OFSurveyUserResponseChild asrc = new OFSurveyUserResponseChild();
            asrc.setScreen_id(screenID);

            if (answerValue != null) {
                asrc.setAnswer_value(answerValue);
            }
            if (answerIndex != null) {
                asrc.setAnswer_index(answerIndex);
            }
            boolean found = false;
            // checking if list already have same value
            for (OFSurveyUserResponseChild src : surveyResponseChildren) {
                if (src.getScreen_id() == screenID) {
                    OFHelper.v(tag, "OneFlow Replacing Value");
                    found = true;
                    Collections.replaceAll(surveyResponseChildren, src, asrc);
                }
            }
            if (!found) {
                surveyResponseChildren.add(asrc);
            }
        }

        OFHelper.v(tag, "OneFlow position [" + position + "]");
        position++;
        OFHelper.v(tag, "OneFlow position after[" + position + "]");

        try {
            // OFHelper.v(tag, "OneFlow rules [" + new Gson().toJson(screens.get(position - 1).getRules()) + "]");
            if (screens.get(position - 1).getRules() != null) {
                preparePositionOnRule(screenID, answerIndex, answerValue);
            } else {
                initFragment();
            }
        } catch (Exception ex) {
            OFHelper.e(tag, "Survey Result error[" + ex.getMessage() + "]");
        }
    }


    //Checking rules
    private void preparePositionOnRule(String screenID, String answerIndex, String answerValue) {

        boolean found = false;
        String action = "", type = "";

        if (screens.get(position - 1).getRules() != null) {
            if (screens.get(position - 1).getRules().getDataLogic() != null) {
                for (OFDataLogic dataLogic : screens.get(position - 1).getRules().getDataLogic()) {

                    OFHelper.v(tag, "OneFlow condition rule[" + new Gson().toJson(screens.get(position - 1).getRules()) + "]");
                    OFHelper.v(tag, "OneFlow condition 0[" + dataLogic.getCondition() + "]");
                    action = dataLogic.getAction();
                    type = dataLogic.getType();
                    if (dataLogic.getCondition().equalsIgnoreCase("is")) {

                        OFHelper.v(tag, "OneFlow condition at is ");
                        if (answerIndex != null) {
                            OFHelper.v(tag, "OneFlow condition value[" + dataLogic.getValues() + "][" + answerIndex + "]");
                            if (dataLogic.getValues().equalsIgnoreCase(answerIndex)) {
                                found = true;
                                break;

                            }
                        } else {
                            if (answerValue != null) {
                                String[] valueArray = answerValue.split(",");
                                String[] logicValue = dataLogic.getValues().split(",");
                                int i = 0;
                                OFHelper.v(tag, "OneFlow condition[" + Arrays.asList(valueArray) + "][" + dataLogic.getValues() + "]");
                                // if(logicValue.length == valueArray.length) {
                            /*while (i < valueArray.length) {
                                if (dataLogic.getValues().equalsIgnoreCase(valueArray[i])) {
                                    OFHelper.v(tag, "OneFlow condition[found in array]");
                                    found = true;
                                    //      action = dataLogic.getAction();
                                    break;
                                }
                                i++;
                            }*/
                                if (Arrays.equals(valueArray, logicValue)) {
                                    found = true;
                                    break;
                                }
                                //}
                        /*// breaking outer loop
                        if(found) break;*/
                            }
                        }
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-not")) {

                        OFHelper.v(tag, "OneFlow condition at is NOT [" + dataLogic.getValues() + "]index[" + answerIndex + "]");
                        if (!dataLogic.getValues().equalsIgnoreCase(answerIndex)) {
                            found = true;
                            break;
                            //findNextQuestionPosition(dataLogic.getValues());
                        } /*else {
                    initFragment();
                }*/
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-one-of")) {

                        OFHelper.v(tag, "OneFlow condition at is one of [" + dataLogic.getValues() + "]index[" + answerIndex + "]answerValue[" + answerValue + "]");
                        String[] rulesArray = dataLogic.getValues().split(",");

                        if (answerIndex != null) {
                            if (Arrays.asList(rulesArray).contains(answerIndex)) {
                                found = true;
                                break;
                            }
                        } else {
                            String[] values = answerValue.split(",");
                            for (String value : values) {
                                OFHelper.v(tag, "OneFlow condition[" + value + "][" + Arrays.asList(values) + "][" + Arrays.asList(rulesArray).contains(value) + "]");
                                if (Arrays.asList(rulesArray).contains(value)) {
                                    found = true;
                                    break;
                                }
                            }
                            // breaking outer loop
                            if (found) break;
                        }
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-none-of")) {
                        String[] rulesArray1 = dataLogic.getValues().split(",");
                        found = true;
                        if (answerIndex != null) {
                            if (Arrays.asList(rulesArray1).contains(answerIndex)) {
                                found = false;
                                break;

                            }
                        } else {
                            String[] values = answerValue.split(",");
                            for (String value : values) {
                                if (Arrays.asList(rulesArray1).contains(value)) {
                                    found = true;
                                    break;
                                }
                            }
                            // breaking outer loop
                            if (found) break;
                        }
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-any")) {

                        //findNextQuestionPosition(dataLogic.getAction());
                        found = true;
                        break;
                    }
                }
            }
        }

        OFHelper.v(tag, "OneFlow found [" + found + "]action[" + action + "]type[" + type + "]");
        // rating and open url is pending
        if (found) {
            if (OFHelper.validateString(action).equalsIgnoreCase("the-end")) {
                position = screens.size() - 1;
                initFragment();
            } else {
                if (type.equalsIgnoreCase("open-url")) {
                    //todo need to close properly

                    //position = screens.size();
                    //initFragment();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                    startActivity(browserIntent);
                    finishSurveyNow();
                } else if (type.equalsIgnoreCase("rating")) {
                    //OFHelper.makeText(OFSurveyActivity.this,"RATING METHOD CALLED",1);
                    //position = screens.size();
                    //initFragment();

                    reviewThisApp(this);
                    finishSurveyNow();
                } else {
                    findNextQuestionPosition(action);
                }
            }
        } else {
            initFragment();
        }
    }
    // initFragment();

    public void reviewThisApp(Context context) {

        ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                OFHelper.v(tag, "OneFlow review success called");
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow((Activity) context, reviewInfo);
                flow.addOnCompleteListener(task2 -> {

                });
                flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {

                    }
                });
            }
        });


    }

    private void findNextQuestionPosition(String nextQuestionId) {
        int index = position - 1;
        OFHelper.v(tag, "OneFlow condition[" + nextQuestionId + "]");
        while (index < screens.size()) {
            if (nextQuestionId.equalsIgnoreCase(screens.get(index).get_id())) {
                //question found break the loop
                OFHelper.v(tag, "OneFlow condition question found at [" + index + "]");
                break;
            }
            index++;
        }

        position = index;
        initFragment();
    }

    public String themeColor = "";
    String selectedSurveyId;


   /* public void prepareAndSubmitUserResposne() {

        OFOneFlowSHP ofs = new OFOneFlowSHP(this);
        ofs.storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        OFSurveyUserInput sur = new OFSurveyUserInput();
        sur.setMode(OFConstants.MODE);
        sur.setTrigger_event(triggerEventName);
        sur.setAnswers(surveyResponseChildren);
        sur.setOs(OFConstants.os);
        sur.setAnalytic_user_id(ofs.getUserDetails().getAnalytic_user_id());
        sur.setSurvey_id(selectedSurveyId);
        sur.setSession_id(ofs.getStringValue(OFConstants.SESSIONDETAIL_IDSHP));

        if (OFHelper.isConnected(this)) {
            OFHelper.v(tag, "OneFlow calling submit user Resposne");
            OFSurvey.submitUserResponse(new OFOneFlowSHP(this).getStringValue(OFConstants.APPIDSHP), sur, OFConstants.ApiHitType.surveySubmited, this);
        } else {

            sur.setUser_id(ofs.getStringValue(OFConstants.USERUNIQUEIDSHP));

            //TODO Store data in db
            OFLogUserDBRepo.insertUserInputs(this, sur, null, OFConstants.ApiHitType.insertSurveyInDB);
            //storing id for avoiding repeatation of offline surveys
            new OFOneFlowSHP(this).storeValue(sur.getSurvey_id(), Calendar.getInstance().getTimeInMillis());
            //Helper.makeText(this,getString(R.string.no_network),1);
        }
    }*/

    public void prepareAndSubmitUserResposneNew() {
        OFHelper.v(tag, "OneFlow checking value prepareAndSubmitUserResposneNew called [" + surveyResponseChildren.size()+"]surveyId["+selectedSurveyId+"]");
        //setupGlobalTimerToDeactivateThrottlingLocally();
        OFOneFlowSHP ofs = OFOneFlowSHP.getInstance(this);
        ofs.storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        OFHelper.v(tag, "OneFlow checking value prepareAndSubmitUserResposneNew called [" + ofs.getBooleanValue(OFConstants.SHP_SURVEY_RUNNING,false));
        OFSurveyUserInput sur = new OFSurveyUserInput();
        sur.setTotDuration(totalTimeSpentInSec());
        sur.setMode(OFConstants.MODE);
        sur.setTrigger_event(triggerEventName);
        sur.setAnswers(surveyResponseChildren);
        sur.setOs(OFConstants.os);
        sur.setAnalytic_user_id(OFHelper.validateString(ofs.getUserDetails().getAnalytic_user_id()));
        sur.setSurvey_id(selectedSurveyId);
        //sur.setSession_id(ofs.getStringValue(OFConstants.SESSIONDETAIL_IDSHP));
        sur.setUser_id(ofs.getStringValue(OFConstants.USERUNIQUEIDSHP));
        sur.setCreatedOn(System.currentTimeMillis());
        new OFLogUserDBRepoKT().insertUserInputs(this, sur, this, OFConstants.ApiHitType.insertSurveyInDB);
        //new OFMyDBAsyncTask(this,this, OFConstants.ApiHitType.insertSurveyInDB,false).execute(sur);
        /*if (!(screens.get(screens.size() - 1).getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                screens.get(screens.size() - 1).getInput().getInput_type().equalsIgnoreCase("end-screen")
        )) {
            OFSDKBaseActivity.this.finish();
        }*/
    }



    private Integer totalTimeSpentInSec() {

        Long l = (System.currentTimeMillis() - inTime) / 1000;
        OFHelper.v(tag, "OneFlow inTime [" + inTime + "][" + System.currentTimeMillis() + "][" + l + "]");
        return l.intValue();
    }

    public int position = 0;

    public void initFragment() {
        OFHelper.v(tag, "OneFlow answer position initFrag [" + position + "]screensize[" + screens.size() + "]selected answers[" + new Gson().toJson(surveyResponseChildren) + "]");
//      OFHelper.v(tag, "OneFlow answer position [" +new Gson().toJson(screens.get(position-1) )+ "]");
        if (position >= screens.size()) {
            finishSurveyNow();
        } else {
            loadFragments();
        }
        OFHelper.v(tag, "OneFlow answer position after[" + position + "]screensize[" + screens.size() + "]selected answers[" + new Gson().toJson(surveyResponseChildren) + "]");
    }


    void setProgressMax(int max) {
        OFHelper.v(tag, "OneFlow max [" + max + "]postion[" + position + "]");
        pagePositionPBar.setMax(max * 100);
    }

    private void setProgressAnimate() {
        // OFHelper.v(tag, "OneFlow animation started [" + position + "] max [" + pagePositionPBar.getProgress() + "]postion[" + (position * 100) + "]");
        if (position == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator animation = ObjectAnimator.ofInt(pagePositionPBar, "progress", 0, 100);
                    animation.setDuration(500);
                    animation.setAutoCancel(true);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }
            }, 500);
        } else {
            ObjectAnimator animation = ObjectAnimator.ofInt(pagePositionPBar, "progress", pagePositionPBar.getProgress(), (position + 1) * 100);

            animation.setDuration(500);
            animation.setAutoCancel(true);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }
    }

    Fragment frag;

    private void loadFragments() {


        //if (screen != null) {
        frag = getFragment();

        if (frag != null) {
            setProgressAnimate();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (position == 0) {
                ft.add(R.id.fragment_view, frag).commit();
            } else {
                ft.replace(R.id.fragment_view, frag).commit();
            }
        } else {
            //Helper.makeText(getApplicationContext(), "frag null", 1);
        }

    }

    public Fragment getFragment() {
        Fragment frag = null;
        try {
            OFHelper.v(tag, "OneFlow finding reached getFragment");
            OFSurveyScreens screen = validateScreens();

            if (screen != null) {
                OFHelper.v(tag, "OneFlow finding question type found inputtype[" + screen.getInput().getInput_type() + "]");
                if (screen.getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                        screen.getInput().getInput_type().equalsIgnoreCase("end-screen")
                ) {

                    //Now thankyou page will also show progress bar 2-sept-2022
                    //pagePositionPBar.setVisibility(View.GONE);
                    frag = OFSurveyQueThankyouFragment.newInstance(screen, sdkTheme, themeColor);
                    try {
                        //Logic for showing close button if fade away is false then have to show close button at thankyou page
                        if (!screen.getRules().getDismissBehavior().getFadesAway()) {
                            closeBtn.setVisibility(View.VISIBLE);
                            shouldFadeAway = true;
                        }
                    } catch (Exception ex) {
                        OFHelper.e(tag, "OneFlow ERROR[" + ex.getMessage() + "]");
                    }

                } else if (screen.getInput().getInput_type().equalsIgnoreCase("text") ||
                        screen.getInput().getInput_type().equalsIgnoreCase("short-text")
                ) {
                    frag = OFSurveyQueTextFragment.newInstance(screen, sdkTheme, themeColor);
                } else if (screen.getInput().getInput_type().equalsIgnoreCase("welcome-screen")) {
                    frag = OFSurveyQueInfoFragment.newInstance(screen, sdkTheme, themeColor);
                } else {
                    frag = OFSurveyQueFragment.newInstance(screen, sdkTheme, themeColor);
                }
            }
        } catch (Exception ex) {
          //  ex.printStackTrace();
          //  OFHelper.e(tag, "OneFlow ERROR [" + ex.getMessage() + "]");
            //frag = SurveyQueThankyouFragment.newInstance(screen);
        }
        return frag;
    }

    /**
     * This method will check if any unknown survey has came
     *
     * @return
     */
    public OFSurveyScreens validateScreens() {
        String[] possibleType = new String[]{"text", "short-text", "thank_you", "mcq", "checkbox", "rating-numerical", "rating-5-star", "rating", "rating-emojis", "nps", "welcome-screen", "end-screen"};
        OFSurveyScreens screen = null;
        OFHelper.v(tag, "OneFlow finding reached validateScreens");
        while (position < screens.size()) {
            screen = screens.get(position);
            OFHelper.v(tag, "OneFlow finding question type [" + screen.getInput().getInput_type() + "]");
            if (Arrays.asList(possibleType).contains(screen.getInput().getInput_type())) {
                OFHelper.v(tag, "OneFlow finding question type [" + screen.getInput().getInput_type() + "] found position[" + position + "]");
                break; // if found then stop;
            } else {
                screen = null;
                OFHelper.v(tag, "OneFlow finding question type [" + screen.getInput().getInput_type() + "] not found skipping this question");
                position++;// if not found then check next survey
            }
        }


        return screen;
    }

    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj, Long reserve, String reserved, Object obj2, Object obj3) {
        OFHelper.v(tag, "OneFlow submitting survey[" + hitType + "]");
        switch (hitType) {
            case insertSurveyInDB:

                //if internet available then only send to api else already stored locally
                if (OFHelper.isConnected(this)) {
                    OFSurveyUserInput sur = (OFSurveyUserInput) obj;
                    OFHelper.v(tag, "OneFlow calling submit user surveyId["+sur.getSurvey_id()+"]surID["+sur.get_id()+"] Resposne [" + sur.getAnswers() + "]");
                    if (sur.getAnswers() != null) {
                        if (sur.getAnswers().size() > 0) {
                            OFSurvey.submitUserResponse(OFOneFlowSHP.getInstance(this).getStringValue(OFConstants.APPIDSHP), sur, OFConstants.ApiHitType.surveySubmited, this);
                        }
                    }
                } else {
                    OFHelper.v(tag, "OneFlow no data connectivity available submit survey later["+position+"]["+screens.size()+"]lastScreen["+screens.get(screens.size()-1).getInput().getInput_type()+"]");
                    /*this logic is added to avoid wait on thankyou page after clicking close button,
                     * Below logic will also help to close survey if there is no thankyou page
                     * */
                    // OFHelper.v(tag, "OneFlow input response current screen[" + screens.get(position - 1).getInput().getInput_type() + "]");
                    try {
                        if (!(screens.get(position - 1).getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                                screens.get(position - 1).getInput().getInput_type().equalsIgnoreCase("end-screen")
                        )) {
                            OFSDKBaseActivity.this.finish();
                        }
                    } catch (Exception ex) {
                        OFSDKBaseActivity.this.finish();
                    }
                }
                break;
            case surveySubmited:

                OFHelper.v(tag, "OneFlow survey submitted successfully");
                if (obj != null) {
                    OFSurveyUserInput sur = (OFSurveyUserInput) obj;
                    OFHelper.v(tag, "OneFlow survey submitted successfully ["+sur.get_id()+"]surveyId["+sur.getSurvey_id()+"]");
                    //Updating survey once data is sent to server, Sending type null as return is not required
                    new OFLogUserDBRepoKT().updateSurveyInput(this, null, null, true, sur.getSurvey_id());
                    //new OFMyDBAsyncTask(this,this,OFConstants.ApiHitType.updateSubmittedSurveyLocally,false).execute(true,sur.get_id());

                    OFOneFlowSHP.getInstance(this).storeValue(sur.getSurvey_id(), Calendar.getInstance().getTimeInMillis());

                    Intent intent = new Intent("survey_finished");

                    OFFinishCallBack finishData = new OFFinishCallBack();
                    finishData.setStatus(surveyClosingStatus);
                    finishData.setSurveyId(sur.getSurvey_id());
                    finishData.setSurveyName(surveyName);
                    finishData.setTriggerName(sur.getTrigger_event());
                    finishData.setScreens(prepareFinishCallback());

                    intent.putExtra(OFConstants.surveyDetail, new Gson().toJson(finishData));
                    //OFHelper.v(tag,"OneFlow sending data ["+new Gson().toJson(finishData)+"]");
                    sendBroadcast(intent);
                }
                surveyResponseChildren = null;
                /*this logic is added to avoid wait on thankyou page after clicking close button,
                 * Below logic will also help to close survey if there is no thankyou page
                 * */
                // OFHelper.v(tag, "OneFlow input response current screen[" + screens.get(position - 1).getInput().getInput_type() + "]");
                try {
                    if (!(screens.get(position - 1).getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                            screens.get(position - 1).getInput().getInput_type().equalsIgnoreCase("end-screen")
                    )) {
                        OFSDKBaseActivity.this.finish();
                    }
                } catch (Exception ex) {
                    OFSDKBaseActivity.this.finish();
                }

                break;
        }
    }

    private ArrayList<OFSurveyFinishModel> prepareFinishCallback() {
        ArrayList<OFSurveyFinishModel> list = new ArrayList<>();
        OFSurveyFinishModel finishModel = null;
        OFSurveyFinishChild finishChild = null;
        ArrayList<OFSurveyFinishChild> listInner;

        for (int i = 0; i < screens.size() - 1; i++) {

            OFSurveyScreens ss = screens.get(i);

            finishModel = new OFSurveyFinishModel();
            finishModel.setScreenId(ss.get_id());
            finishModel.setQuestionTitle(ss.getTitle());
            finishModel.setQuestionType(ss.getInput().getInput_type());
            listInner = new ArrayList<>();
            if (surveyResponseChildren != null) {
                for (OFSurveyUserResponseChild sr : surveyResponseChildren) {
                    if (sr.getScreen_id().equalsIgnoreCase(ss.get_id())) {
                        finishChild = new OFSurveyFinishChild();
                        if (ss.getInput().getInput_type().equalsIgnoreCase("mcq") ||
                                ss.getInput().getInput_type().equalsIgnoreCase("checkbox")) {
                            //This if is for handling multiple option in checkbox.
                            if (sr.getAnswer_index().contains(",")) {
                                String[] options = sr.getAnswer_index().split(",");
                                // OFHelper.v(tag,"OneFlow sending data ["+options.length+"]");
                                for (String option : options) {
                                    //  OFHelper.v(tag,"OneFlow sending data inside loop["+option+"]");
                                    finishChild = new OFSurveyFinishChild();
                                    finishChild.setAnswerValue(getFieldValue(ss, option));
                                    if (finishChild.getAnswerValue().equalsIgnoreCase("other") || finishChild.getAnswerValue().equalsIgnoreCase("others")) {
                                        finishChild.setOtherValue(sr.getAnswer_value());
                                    }
                                    listInner.add(finishChild);
                                }
                            } else {
                                finishChild.setAnswerValue(getFieldValue(ss, sr.getAnswer_index()));
                                if (finishChild.getAnswerValue().equalsIgnoreCase("other") || finishChild.getAnswerValue().equalsIgnoreCase("others")) {
                                    finishChild.setOtherValue(sr.getAnswer_value());
                                }
                                listInner.add(finishChild);
                            }


                        } else {
                            finishChild.setAnswerValue(sr.getAnswer_value());
                            listInner.add(finishChild);
                        }

                    }

                    finishModel.setQuestionAns(listInner);

                }
            }
            if (finishModel.getQuestionAns() != null && finishModel.getQuestionAns().size() > 0) {
                list.add(finishModel);
            }
        }
        //}
        // OFHelper.v(tag,"OneFlow list ["+new Gson().toJson(list)+"]");
        return list;
    }


    View.OnTouchListener sliderTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Get finger position on screen
            final int Y = (int) event.getRawY();

            // Switch on motion event type
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    // save default base layout height
                    defaultViewHeight = basePopupLayout.getHeight();

                    // Init finger and view position
                    previousFingerPosition = Y;
                    baseLayoutPosition = (int) basePopupLayout.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    // If user was doing a scroll up
                    if (isScrollingUp) {
                        // Reset baselayout position
                        basePopupLayout.setY(0);
                        // We are not in scrolling up mode anymore
                        isScrollingUp = false;
                    }

                    // If user was doing a scroll down
                    if (isScrollingDown) {
                        // Reset baselayout position
                        basePopupLayout.setY(0);
                        // Reset base layout size
                        basePopupLayout.getLayoutParams().height = defaultViewHeight;
                        basePopupLayout.requestLayout();
                        // We are not in scrolling down mode anymore
                        isScrollingDown = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isClosing) {
                        int currentYPosition = (int) basePopupLayout.getY();

                        // If we scroll up
                        if (previousFingerPosition > Y) {
                            // First time android rise an event for "up" move
                            if (!isScrollingUp) {
                                isScrollingUp = true;
                            }

                            // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
                            if (basePopupLayout.getHeight() < defaultViewHeight) {
                                /*basePopupLayout.getLayoutParams().height = basePopupLayout.getHeight() - (Y - previousFingerPosition);
                                basePopupLayout.requestLayout();*/
                            } else {
                                // Has user scroll enough to "auto close" popup ?
                                if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                    //closeUpAndDismissDialog(currentYPosition);
                                    return true;
                                }

                                //
                            }
                            // basePopupLayout.setY(basePopupLayout.getY() + (Y - previousFingerPosition));

                        }
                        // If we scroll down
                        else {

                            // First time android rise an event for "down" move
                            if (!isScrollingDown) {
                                isScrollingDown = true;
                            }

                            // Has user scroll enough to "auto close" popup ?
                            if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                                closeDownAndDismissDialog(currentYPosition);
                                return true;
                            }

                            // Change base layout size and position (must change position because view anchor is top left corner)
                            basePopupLayout.setY(basePopupLayout.getY() + (Y - previousFingerPosition));
                            basePopupLayout.getLayoutParams().height = basePopupLayout.getHeight() - (Y - previousFingerPosition);
                            basePopupLayout.requestLayout();
                        }

                        // Update position
                        previousFingerPosition = Y;
                    }
                    break;
            }
            return true;
        }
    };

    public void closeUpAndDismissDialog(int currentPosition) {
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(basePopupLayout, "y", currentPosition, -basePopupLayout.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        isClosing = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(basePopupLayout, "y", currentPosition, screenHeight + basePopupLayout.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

        });
        positionAnimator.start();
    }

    public String getFieldValue(OFSurveyScreens ss, String findThisId) {
        String label = "";
        OFHelper.v(tag, "OneFlow field value in [" + ss + "][" + findThisId + "]");
        for (OFSurveyChoises choice : ss.getInput().getChoices()) {

            if (choice.getId().equalsIgnoreCase(findThisId)) {
                label = choice.getTitle();
                break;
            }

        }
        OFHelper.v(tag, "OneFlow field value out [" + label + "]");
        return label;

    }

}
