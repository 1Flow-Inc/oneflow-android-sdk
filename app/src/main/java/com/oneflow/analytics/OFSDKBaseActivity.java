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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
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
import java.util.List;
import java.util.UUID;

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

        OFHelper.v(this.getClass().getName(), "1Flow onSaveInstanceState called[" + savedInstanceCalled + "]");
        outState.putSerializable("SurveyType", surveyItem);
        outState.putSerializable("SurveyTheme", sdkTheme);
        outState.putSerializable("position", position);
        OFHelper.v(this.getClass().getName(), "1Flow onSaveInstanceState called 0[" + position + "]");
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        OFHelper.v(this.getClass().getName(), "1Flow onRestoreInstanceState base called 0[" + position + "]");
        try {
            surveyItem = (OFGetSurveyListResponse) savedInstanceState.getSerializable("SurveyType");
            sdkTheme = (OFSDKSettingsTheme) savedInstanceState.getSerializable("SurveyTheme");
            position = (int) savedInstanceState.getSerializable("position");

            OFHelper.v(this.getClass().getName(), "1Flow onRestoreInstanceState base called 1 [" + position + "]");

        } catch (Exception ex) {
            OFHelper.e(this.getClass().getName(), "1Flow onRestoreInstanceState base called 0");
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        OFHelper.v(tag, "1Flow onConfigurationChanged called [" + position + "]");

    }

    OFGetSurveyListResponse surveyItem;
    boolean shouldFadeAway = false;
    int heightScreen;
    float per = 0.8f;
    Boolean savedInstanceCalled = false;

    @Override
    protected void onStart() {
        super.onStart();

        OFHelper.v(this.getClass().getName(), "1Flow onStart called [" + savedInstanceCalled + "]");

        //this condition added because on rotation it was showing older fragment

    }

    public void loadThisView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display disp = getWindowManager().getDefaultDisplay();
        disp.getMetrics(displayMetrics);
        heightScreen = (int) (displayMetrics.heightPixels * per);


        surveyItem = (OFGetSurveyListResponse) this.getIntent().getSerializableExtra("SurveyType");

        surveyName = surveyItem.getName();
        screens = surveyItem.getScreens();
        triggerEventName = this.getIntent().getStringExtra("eventName");
        setProgressMax(surveyItem.getScreens().size()); // -1 for excluding thankyou page from progress bar; 2-sept-2022 showing progressbar at thankyou page
        selectedSurveyId = surveyItem.get_id();

        OFHelper.v(this.getClass().getName(), "1Flow onLoadThisView called position[" + position + "]surveyId[" + selectedSurveyId + "]triggerEventName[" + triggerEventName + "]");

        closeBtn.setOnClickListener(v -> {

            //closed survey logic for storage.
            OFOneFlowSHP ofs = OFOneFlowSHP.getInstance(OFSDKBaseActivity.this);
            ArrayList<String> closedSurveyList = ofs.getClosedSurveyList();
            if (closedSurveyList == null) {
                closedSurveyList = new ArrayList<>();
            }
            OFHelper.v(tag, "1Flow close button clicked [" + surveyResponseChildren + "]position[" + position + "]size[" + screens.size() + "]");
            if (surveyResponseChildren == null || surveyResponseChildren.isEmpty()) {

                surveyClosingStatus = "skipped";
                if (!closedSurveyList.contains(selectedSurveyId)) {
                    closedSurveyList.add(selectedSurveyId);
                    ofs.setClosedSurveyList(closedSurveyList);
                    OFEventController ec = OFEventController.getInstance(OFSDKBaseActivity.this);
                    HashMap<String, Object> mapValue = new HashMap<>();
                    mapValue.put("survey_id", selectedSurveyId);
                    ec.storeEventsInDB(OFConstants.AUTOEVENT_CLOSED_SURVEY, mapValue, 0);
                }

            } else if (!screens.isEmpty() && position < screens.size()) {
                if(screens.get(position).getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                        screens.get(position).getInput().getInput_type().equalsIgnoreCase("end-screen")){
                    surveyClosingStatus = "finished";
                }else{
                    surveyClosingStatus = "closed";
                }

            } else {
                surveyClosingStatus = "closed";
            }

            if (position >= screens.size()) {
                OFSDKBaseActivity.this.finish();
            } else {
                finishSurveyNow();
            }
        });


        themeColor = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimaryDark));

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
                themeColor = "#" + tranparancy + tempColor;
            } else {
                themeColor = tranparancy + tempColor;
            }

        } catch (Exception ex) {
            // error
        }
        try {
            pagePositionPBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
        } catch (NumberFormatException nfe) {

            themeColor = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            pagePositionPBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
        }


        sdkTheme = surveyItem.getSurveySettings().getSdkTheme();


        mainChildForBackground.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackground_color())));

        Drawable closeIcon = closeBtn.getDrawable();
        closeIcon.setColorFilter(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f), PorterDuff.Mode.SRC_ATOP);


        slider.setOnTouchListener(sliderTouchListener);
        sliderLayout.setOnTouchListener(sliderTouchListener);
        slider.setOnDragListener((v, event) -> false);


        //New theme custome UI
        boolean progress = sdkTheme.getProgress_bar();
        if (progress) {
            pagePositionPBar.setVisibility(View.VISIBLE);
        } else {
            pagePositionPBar.setVisibility(View.GONE);
        }

        boolean close = sdkTheme.getClose_button();
        if (close) {
            closeBtn.setVisibility(View.VISIBLE);
        } else {
            closeBtn.setVisibility(View.GONE);
        }

        isActive = true;

        boolean darkOverlay = sdkTheme.getDark_overlay();
        if (darkOverlay) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0.25f); //0 for no dim to 1 for full dim
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0f); //0 for no dim to 1 for full dim
        }
        OFHelper.v(tag, "1Flow from onLoadThisView");
        initFragment(0);
    }


    public static boolean isActive;

    @Override
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        OFHelper.v(tag, "1Flow onPause called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OFHelper.v(tag, "1Flow onResume called[" + position + "]");
        // below method has been shifted in onResume because onStart is being called befor onRestoreState method
        loadThisView();

    }

    public void finishSurveyNow() {

        // recording finish events
        if (surveyClosingStatus.equalsIgnoreCase("finished")) {
            OFHelper.v(tag, "1Flow auto event step completed recording");
            // event for all type of step visibility
            HashMap<String, Object> mapValue = new HashMap<>();
            mapValue.put("flow_id", selectedSurveyId);

            OFEventController ec = OFEventController.getInstance(OFSDKBaseActivity.this);
            ec.storeEventsInDB(OFConstants.AUTOEVENT_FLOW_COMPLETED, mapValue, 0);
        } else {
            OFHelper.v(tag, "1Flow auto event step ended recording");
            // event for all type of step visibility
            HashMap<String, Object> mapValue = new HashMap<>();
            mapValue.put("flow_id", selectedSurveyId);

            OFEventController ec = OFEventController.getInstance(OFSDKBaseActivity.this);
            ec.storeEventsInDB(OFConstants.AUTOEVENT_FLOW_ENDED, mapValue, 0);
        }


        OFOneFlowSHP.getInstance(this).storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        //on close of this page considering survey is over, so submit the respones to api
        if (surveyResponseChildren != null) {
            if (!surveyResponseChildren.isEmpty()) {

                OFHelper.v(tag, "1Flow input found submitting");
                prepareAndSubmitUserResposneNew();
            } else {
//                OFHelper.v(tag, "1Flow no input no submit");
//                surveyFinishList = new ArrayList<>();
//                Intent intent = new Intent("survey_finished");
//
//                OFFinishCallBack finishData = new OFFinishCallBack();
//                finishData.setStatus(surveyClosingStatus);
//                finishData.setSurveyId(selectedSurveyId);
//                finishData.setSurveyName(surveyName);
//                finishData.setTriggerName(triggerEventName);
//                finishData.setScreens(prepareFinishCallback());
//
//                intent.putExtra(OFConstants.surveyDetail, new Gson().toJson(finishData));
//                sendBroadcast(intent);
//                OFSDKBaseActivity.this.finish();

                surveyFinishList = new ArrayList<>();
                OFHelper.v(tag, "1Flow input found submitting1");
                prepareAndSubmitUserResposneNew();
            }
        } else {
//            OFHelper.v(tag, "1Flow no input no submit");
//            surveyFinishList = new ArrayList<>();
//            Intent intent = new Intent("survey_finished");
//
//            OFFinishCallBack finishData = new OFFinishCallBack();
//            finishData.setStatus(surveyClosingStatus);
//            finishData.setSurveyId(selectedSurveyId);
//            finishData.setSurveyName(surveyName);
//            finishData.setTriggerName(triggerEventName);
//            finishData.setScreens(prepareFinishCallback());
//
//            intent.putExtra(OFConstants.surveyDetail, new Gson().toJson(finishData));
//            sendBroadcast(intent);
//            OFSDKBaseActivity.this.finish();

            surveyFinishList = new ArrayList<>();
            OFHelper.v(tag, "1Flow input found submitting2");
            prepareAndSubmitUserResposneNew();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
        OFOneFlowSHP.getInstance(this).storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        OFHelper.v(tag, "1Flow onDestroy called [" + isActive + "]");
    }

    @Override
    protected void onStop() {
        super.onStop();
        OFOneFlowSHP.getInstance(this).storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        OFHelper.v(tag, "1Flow onStop called [" + isActive + "]");
    }

    /**
     * Record User inputs
     *
     * @param screenID
     * @param answerIndex
     * @param answerValue
     */
    public void addUserResponseToList(String screenID, String answerIndex, String answerValue) {

        OFHelper.v(tag, "1Flow answerindex position 0 [" + position + "][" + answerIndex + "]answervalue[" + answerValue + "]");
        if (surveyResponseChildren == null) {
            surveyResponseChildren = new ArrayList<>();
        }
        OFEventController ec = OFEventController.getInstance(this);

        // event click only for welcome or info screen

        if (screens.get(position).getInput().getInput_type().equalsIgnoreCase("welcome-screen")) {
            HashMap<String, Object> mapValue = new HashMap<>();
            mapValue.put("flow_id", selectedSurveyId);
            mapValue.put("step_id", screenID);


            ec.storeEventsInDB(OFConstants.AUTOEVENT_FLOWSTEP_CLICKED, mapValue, 0);
            OFHelper.v(tag, "1Flow auto event clicked recording [" + mapValue + "]");
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
                if (src.getScreen_id().equals(screenID)) {
                    OFHelper.v(tag, "1Flow Replacing Value");
                    found = true;
                    Collections.replaceAll(surveyResponseChildren, src, asrc);
                }
            }
            if (!found) {
                surveyResponseChildren.add(asrc);
            }

            if(position < screens.size()){
                // event for all type of step visibility
                HashMap<String, Object> mapValue1 = new HashMap<>();
                mapValue1.put("question_id", surveyItem.get_id());
                mapValue1.put("flow_id", selectedSurveyId);
                mapValue1.put("step_id", screenID);
                mapValue1.put("type", screens.get(position).getInput().getInput_type());
                mapValue1.put("answer", OFHelper.validateString(answerValue).equalsIgnoreCase("na") ? answerIndex : answerValue);
                mapValue1.put("question_title", screens.get(position).getTitle());
                mapValue1.put("question_description", screens.get(position).getMessage());
                mapValue1.put("survey_name", surveyItem.getName());

                ec.storeEventsInDB(OFConstants.AUTOEVENT_QUESTION_ANSWERED, mapValue1, 0);
                OFHelper.v(tag, "1Flow auto event answered recording [" + mapValue1 + "]");
            }

        }

        OFHelper.v(tag, "1Flow position [" + position + "]");
        position++;
        OFHelper.v(tag, "1Flow position after[" + position + "]");

        try {
            if (screens.get(position - 1).getRules() != null) {
                preparePositionOnRule(screenID, answerIndex, answerValue);
            } else {
                initFragment(1);
            }
        } catch (Exception ex) {
            OFHelper.e(tag, "Survey Result error[" + ex.getMessage() + "]");
        }
    }


    //Checking rules
    private void preparePositionOnRule(String screenID, String answerIndex, String answerValue) {

        boolean found = false;
        String action = "";
        String type = "";

        OFHelper.v(tag, "1Flow condition rule[" + screenID + "]answerIndex[" + answerIndex + "][" + answerValue + "]");
        if (screens.get(position - 1).getRules() != null && (screens.get(position - 1).getRules().getDataLogic() != null)) {
                for (OFDataLogic dataLogic : screens.get(position - 1).getRules().getDataLogic()) {

                    OFHelper.v(tag, "1Flow condition rule[" + new Gson().toJson(screens.get(position - 1).getRules()) + "]");
                    OFHelper.v(tag, "1Flow condition 0[" + dataLogic.getCondition() + "]");
                    action = dataLogic.getAction();
                    type = dataLogic.getType();
                    if (dataLogic.getCondition().equalsIgnoreCase("is")) {

                        OFHelper.v(tag, "1Flow condition at is ");
                        if (answerIndex != null) {
                            OFHelper.v(tag, "1Flow condition value[" + dataLogic.getValues() + "][" + answerIndex + "]");
                            if (dataLogic.getValues().equalsIgnoreCase(answerIndex)) {
                                found = true;
                                break;

                            }
                        } else {
                            if (answerValue != null) {
                                String[] valueArray = answerValue.split(",");
                                String[] logicValue = dataLogic.getValues().split(",");
                                OFHelper.v(tag, "1Flow condition[" + Arrays.asList(valueArray) + "][" + dataLogic.getValues() + "]");
                                if (Arrays.equals(valueArray, logicValue)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-not")) {
                        String[] rulesArray2 = dataLogic.getValues().split(",");
                        OFHelper.v(tag, "1Flow condition at is NOT [" + dataLogic.getValues() + "]index[" + answerIndex + "]");
                        found = false;
                        if (answerIndex != null) {
                            if (!dataLogic.getValues().equalsIgnoreCase(answerIndex)) {
                                found = true;
                                break;
                            }
                        } else {
                            String[] values = answerValue.split(",");
                            for (String value : values) {
                                if (!(Arrays.asList(rulesArray2).contains(value))) {
                                    found = true;
                                    break;
                                }
                            }
                            // breaking outer loop
                            if (found) break;
                        }
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-one-of")) {

                        OFHelper.v(tag, "1Flow condition at is one of [" + dataLogic.getValues() + "]index[" + answerIndex + "]answerValue[" + answerValue + "]");
                        String[] rulesArray = dataLogic.getValues().split(",");

                        if (answerIndex != null) {
                            if (Arrays.asList(rulesArray).contains(answerIndex)) {
                                found = true;
                                break;
                            }
                        } else {
                            String[] values = answerValue.split(",");
                            for (String value : values) {
                                OFHelper.v(tag, "1Flow condition[" + value + "][" + Arrays.asList(values) + "][" + Arrays.asList(rulesArray).contains(value) + "]");
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
                        found = false;
                        if (answerIndex != null) {
                            if (!(Arrays.asList(rulesArray1).contains(answerIndex))) {
                                found = true;
                                break;

                            }
                        } else {
                            String[] values = answerValue.split(",");
                            for (String value : values) {
                                if (!(Arrays.asList(rulesArray1).contains(value))) {
                                    found = true;
                                    break;
                                }
                            }
                            // breaking outer loop
                            if (found) break;
                        }
                    } else if (dataLogic.getCondition().equalsIgnoreCase("is-any")) {
                        found = true;
                        break;
                    }
                }

        }

        OFHelper.v(tag, "1Flow found [" + found + "]action[" + action + "]type[" + type + "]");
        // rating and open url is pending
        if (found) {
            if (OFHelper.validateString(action).equalsIgnoreCase("the-end")) {
                try {
                    if ((screens.get(screens.size() - 1).getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                            screens.get(screens.size() - 1).getInput().getInput_type().equalsIgnoreCase("end-screen")
                    )) {
                        position = screens.size() - 1;
                    } else {
                        position = screens.size();
                    }
                } catch (Exception ex) {
                    // error
                }
                initFragment(2);

            } else {
                if (type.equalsIgnoreCase("open-url")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                    startActivity(browserIntent);
                    finishSurveyNow();
                } else if (type.equalsIgnoreCase("rating")) {

                    reviewThisApp(this);
                    finishSurveyNow();
                } else {
                    findNextQuestionPosition(action);
                }
            }
        } else {
            initFragment(3);
        }
    }


    public void reviewThisApp(Context context) {

        ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                OFHelper.v(tag, "1Flow review success called");
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow((Activity) context, reviewInfo);
                flow.addOnCompleteListener(task2 -> {

                });
                flow.addOnSuccessListener(result -> {
                    // onSuccess
                });
            }
        });


    }

    private void findNextQuestionPosition(String nextQuestionId) {
        int index = position - 1;
        OFHelper.v(tag, "1Flow condition[" + nextQuestionId + "]");
        while (index < screens.size()) {
            if (nextQuestionId.equalsIgnoreCase(screens.get(index).get_id())) {
                //question found break the loop
                OFHelper.v(tag, "1Flow condition question found at [" + index + "]");
                break;
            }
            index++;
        }

        if(index==screens.size()){
            index= position;
        }
        position = index;
        initFragment(4);
    }

    public String themeColor = "";
    String selectedSurveyId;


    public void prepareAndSubmitUserResposneNew() {

        OFHelper.v(tag, "1Flow checking value prepareAndSubmitUserResposneNew called [" + surveyResponseChildren.size() + "]surveyId[" + selectedSurveyId + "]");

        OFOneFlowSHP ofs = OFOneFlowSHP.getInstance(this);
        ofs.storeValue(OFConstants.SHP_SURVEY_RUNNING, false);
        OFHelper.v(tag, "1Flow checking value prepareAndSubmitUserResposneNew called [" + ofs.getBooleanValue(OFConstants.SHP_SURVEY_RUNNING, false));

        OFSurveyUserInput sur = new OFSurveyUserInput();
        sur.setId(OFHelper.mongoObjectId());
        sur.setTotDuration(totalTimeSpentInSec());
        sur.setMode(OFConstants.MODE);
        sur.setTrigger_event(triggerEventName);
        sur.setAnswers(surveyResponseChildren);
        sur.setOs(OFConstants.os);
        sur.setAnalytic_user_id(OFHelper.validateString(ofs.getUserDetails().getAnalytic_user_id()));
        sur.setSurvey_id(selectedSurveyId);
        sur.setUser_id(ofs.getStringValue(OFConstants.USERUNIQUEIDSHP));
        sur.setCreatedOn(System.currentTimeMillis());
        new OFLogUserDBRepoKT().insertUserInputs(this, sur, this, OFConstants.ApiHitType.insertSurveyInDB);

    }


    private Integer totalTimeSpentInSec() {

        Long l = (System.currentTimeMillis() - inTime) / 1000;
        OFHelper.v(tag, "1Flow inTime [" + inTime + "][" + System.currentTimeMillis() + "][" + l + "]");
        return l.intValue();
    }

    public int position = 0;


    public void initFragment(int calledFrom) {
        OFHelper.v(tag, "1Flow initFragment answer calledFrom[" + calledFrom + "] position [" + position + "]screensize[" + screens.size() + "]selected answers[" + new Gson().toJson(surveyResponseChildren) + "]");

        if (position >= screens.size()) {
            finishSurveyNow();
        } else {
            loadFragments();
        }
        OFHelper.v(tag, "1Flow answer position after[" + position + "]screensize[" + screens.size() + "]selected answers[" + new Gson().toJson(surveyResponseChildren) + "]");
    }


    void setProgressMax(int max) {
        OFHelper.v(tag, "1Flow max [" + max + "]postion[" + position + "]");
        pagePositionPBar.setMax(max * 100);
    }

    private void setProgressAnimate() {

        OFHelper.v(tag, "1Flow progressbar [" + position + "] max [" + pagePositionPBar.getProgress() + "]postion[" + (position * 100) + "]");

        if (position == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ObjectAnimator animation = ObjectAnimator.ofInt(pagePositionPBar, "progress", 0, 100);
                animation.setDuration(500);
                animation.setAutoCancel(true);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
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

        OFHelper.v(this.getClass().getName(), "1Flow loadFragments called[" + position + "]frag[" + frag + "]");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //====================for prohibiting recreating of fragment====================
        frag = fm.findFragmentByTag("Frag" + position);

        if (frag == null) {


            // event for all type of step visibility
            HashMap<String, Object> mapValue = new HashMap<>();
            mapValue.put("flow_id", selectedSurveyId);
            mapValue.put("step_id", screens.get(position).get_id());
            OFEventController ec = OFEventController.getInstance(this);

            ec.storeEventsInDB(OFConstants.AUTOEVENT_FLOWSTEP_SEEN, mapValue, 0);
            OFHelper.v(tag, "1Flow auto event seen recording [" + mapValue + "]");

            frag = getFragment();

            if (frag != null) {
                setProgressAnimate();

                if (position == 0) {
                    ft.add(R.id.fragment_view, frag, "Frag" + position).commit();
                } else {
                    ft.replace(R.id.fragment_view, frag, "Frag" + position).commit();
                }
            }
        } else {
            setProgressAnimate();
        }
        //===============================================================================
    }


    public void resetHeight(int fragmentHeight) {

        int dialogHeight = fragmentHeight > heightScreen ? heightScreen + 50 : ViewGroup.LayoutParams.WRAP_CONTENT;
///=================================================================

        OFHelper.v(tag, "1Flow Window size width[" + window.getAttributes().width + "]height[" + window.getAttributes().height + "]");

        double[] data = OFHelper.getScreenSize(this);
        OFHelper.v(tag, "1Flow Window size width[" + data[0] + "]data[1][" + data[1] + "]");
        int width = 0;
        if (data[0] > 3) {
            width = OFConstants.screenWidth;
        } else {
            width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

///=================================================================

        getWindow().setLayout(width, dialogHeight);

        OFHelper.v(tag, "1Flow displayHeight condi[" + (fragmentHeight > heightScreen) + "]dialogHeight[" + dialogHeight + "]inPx[" + heightScreen + "]dialogHeight inPx[" + fragmentHeight + "].");
    }

    public Fragment getFragment() {

        Fragment frag = null;
        try {

            OFHelper.v(tag, "1Flow getFragment called");

            OFSurveyScreens screen = validateScreens();

            if (screen != null) {
                OFHelper.v(tag, "1Flow finding question type found inputtype[" + screen.getInput().getInput_type() + "]");
                if (screen.getInput().getInput_type().equalsIgnoreCase("thank_you") ||
                        screen.getInput().getInput_type().equalsIgnoreCase("end-screen")
                ) {
                    frag = OFSurveyQueThankyouFragment.newInstance(screen, sdkTheme, themeColor);
                    try {
                        //Logic for showing close button if fade away is false then have to show close button at thankyou page
                        boolean fades = screen.getRules().getDismissBehavior().getFadesAway();
                        if (!fades) {
                            closeBtn.setVisibility(View.VISIBLE);
                            shouldFadeAway = true;
                        }
                    } catch (Exception ex) {

                        OFHelper.e(tag, "1Flow ERROR at getFragment[" + ex.getMessage() + "]");

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

            //  error
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

        OFHelper.v(tag, "1Flow validateScreens called[" + position + "]");

        while (position < screens.size()) {
            screen = screens.get(position);
            OFHelper.v(tag, "1Flow finding question type [" + screen.getInput().getInput_type() + "]");
            if (Arrays.asList(possibleType).contains(screen.getInput().getInput_type())) {
                OFHelper.v(tag, "1Flow finding question type [" + screen.getInput().getInput_type() + "] found position[" + position + "]");
                break;
            } else {
                OFHelper.v(tag, "1Flow finding question type [" + screen.getInput().getInput_type() + "] not found skipping this question");
                screen = null;
                position++;
            }
        }


        return screen;
    }

    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj, Long reserve, String reserved, Object obj2, Object obj3) {
        OFHelper.v(tag, "1Flow submitting survey[" + hitType + "]");
        switch (hitType) {
            case insertSurveyInDB:

                //if internet available then only send to api else already stored locally
                if (OFHelper.isConnected(this)) {
                    OFSurveyUserInput sur = (OFSurveyUserInput) obj;

                    OFHelper.v(tag, "1Flow calling submit user surveyId[" + sur.getSurvey_id() + "]surID[" + sur.get_lid() + "] Resposne [" + sur.getAnswers() + "]");

                    if (sur.getAnswers() != null && (!sur.getAnswers().isEmpty())) {
                        OFSurvey.submitUserResponse(OFOneFlowSHP.getInstance(this).getStringValue(OFConstants.APPIDSHP), sur, OFConstants.ApiHitType.surveySubmited, this);
                    }
                } else {

                    OFHelper.v(tag, "1Flow no data connectivity available submit survey later[" + position + "][" + screens.size() + "]lastScreen[" + screens.get(screens.size() - 1).getInput().getInput_type() + "]");
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

                OFHelper.v(tag, "1Flow survey submitted successfully");
                if (obj != null) {
                    OFSurveyUserInput sur = (OFSurveyUserInput) obj;

                    OFHelper.v(tag, "1Flow survey submitted successfully [" + sur.get_lid() + "]surveyId[" + sur.getSurvey_id() + "]");
                    //Updating survey once data is sent to server, Sending type null as return is not required
                    new OFLogUserDBRepoKT().updateSurveyInput(this, null, null, true, sur.getSurvey_id());

                    OFOneFlowSHP.getInstance(this).storeValue(sur.getSurvey_id(), Calendar.getInstance().getTimeInMillis());

                    Intent intent = new Intent("survey_finished");

                    OFFinishCallBack finishData = new OFFinishCallBack();
                    finishData.setStatus(surveyClosingStatus);
                    finishData.setSurveyId(sur.getSurvey_id());
                    finishData.setSurveyName(surveyName);
                    finishData.setTriggerName(sur.getTrigger_event());
                    finishData.setScreens(prepareFinishCallback());

                    intent.putExtra(OFConstants.surveyDetail, new Gson().toJson(finishData));
                    sendBroadcast(intent);
                }
                surveyResponseChildren = null;
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
            default:
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
                                for (String option : options) {
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
            if (finishModel.getQuestionAns() != null && !finishModel.getQuestionAns().isEmpty()) {
                list.add(finishModel);
            }
        }
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
                                // height
                            } else {
                                // Has user scroll enough to "auto close" popup ?
                                if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                    return true;
                                }
                            }
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
                default:
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
                // Repeat
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Cancel
            }

            @Override
            public void onAnimationStart(Animator animation) {
                // Start
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
                // Repeat
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Cancel
            }

            @Override
            public void onAnimationStart(Animator animation) {
                // Start
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
        OFHelper.v(tag, "1Flow field value in [" + ss + "][" + findThisId + "]");
        for (OFSurveyChoises choice : ss.getInput().getChoices()) {

            if (choice.getId().equalsIgnoreCase(findThisId)) {
                label = choice.getTitle();
                break;
            }

        }
        OFHelper.v(tag, "1Flow field value out [" + label + "]");
        return label;

    }

}
