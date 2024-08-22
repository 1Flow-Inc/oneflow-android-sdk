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

package com.oneflow.analytics.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.R;
import com.oneflow.analytics.adapter.OFSurveyOptionsAdapter;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.model.survey.OFRatingsModel;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFGenericClickHandler;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;


public class OFSurveyQueFragment extends BaseFragment implements OFGenericClickHandler {

    OFCustomTextViewBold surveyTitle;
    OFCustomTextViewBold submitButton;
    RecyclerView surveyOptionRecyclerView;
    OFCustomTextView ratingsNotLike;
    OFCustomTextView ratingsFullLike;
    OFCustomTextView surveyDescription;
    OFCustomTextView starRatingLabel;

    RelativeLayout optionLayout;

    //this is for testing

    String tag = this.getClass().getName();

    OFSurveyOptionsAdapter dashboardAdapter;
    Animation animation1;
    Animation animation2;
    Animation animation3;
    Animation animation4;
    Animation animationIn;

    public static OFSurveyQueFragment newInstance(OFSurveyScreens ahdList, OFSDKSettingsTheme sdkTheme, String themeColor) {
        OFSurveyQueFragment myFragment = new OFSurveyQueFragment();

        Bundle args = new Bundle();
        args.putSerializable("data", ahdList);
        args.putSerializable("theme", sdkTheme);
        args.putString("themeColor", themeColor);
        myFragment.setArguments(args);
        return myFragment;
    }

    int i = 0;

    @Override
    public void onResume() {
        super.onResume();
        OFHelper.v(tag, "1Flow OnResume");

        setupWeb();


        View[] animateViews = new View[]{surveyTitle, surveyDescription, optionLayout, submitButton};

        Animation[] annim = new Animation[]{animation1, animation2, animation3, animation4};

        if (i == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if(i < animateViews.length){
                    annim[i].setFillAfter(true);
                    animateViews[i].startAnimation(annim[i]);
                }
            }, 1000);

            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Start
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    i++;

                    if (!(surveyScreens.getMessage() != null && surveyScreens.getMessage().length() > 0)) {
                        i++;
                    }

                    if (i < animateViews.length) {
                        animateViews[i].setVisibility(View.VISIBLE);
                        animateViews[i].startAnimation(annim[i]);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Repeat
                }
            });
            animation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Start
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    i++;
                    if (i < animateViews.length) {
                        animateViews[i].setVisibility(View.VISIBLE);
                        animateViews[i].startAnimation(annim[i]);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    OFHelper.v(tag, "1Flow animation REPEAT[" + i + "]");
                }
            });
            animation3.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Start
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    i++;
                    if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_CHECKBOX) && (i < animateViews.length)) {
                            animateViews[i].setVisibility(View.VISIBLE);
                            animateViews[i].startAnimation(annim[i]);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    OFHelper.v(tag, "1Flow animation REPEAT[" + i + "]");
                }
            });
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.survey_que_fragment, container, false);

        surveyTitle = (OFCustomTextViewBold) view.findViewById(R.id.survey_title_que);
        submitButton = (OFCustomTextViewBold) view.findViewById(R.id.submit_btn);
        surveyDescription = (OFCustomTextView) view.findViewById(R.id.survey_description_que);
        ratingsNotLike = (OFCustomTextView) view.findViewById(R.id.ratings_not_like);
        ratingsFullLike = (OFCustomTextView) view.findViewById(R.id.ratings_full_like);
        starRatingLabel = (OFCustomTextView) view.findViewById(R.id.star_ratings_label);
        surveyOptionRecyclerView = (RecyclerView) view.findViewById(R.id.survey_options_list);
        optionLayout = (RelativeLayout) view.findViewById(R.id.option_layout);
        waterMarkLayout = (LinearLayout) view.findViewById(R.id.bottom_water_mark);

        OFHelper.v(tag, "1Flow list data[" + new Gson().toJson(surveyScreens) + "]");
        int colorTitle = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 1.0f);

        surveyTitle.setTextColor(colorTitle);

        int colorDesc = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.8f);
        int colorlike = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f);


        surveyDescription.setTextColor(colorDesc);
        ratingsNotLike.setTextColor(colorlike);
        ratingsFullLike.setTextColor(colorlike);
        starRatingLabel.setTextColor(colorlike);
        ((TextView) waterMarkLayout.getChildAt(1)).setTextColor(colorlike);


        handleWaterMarkStyle(sdkTheme);
        submitButton.setOnClickListener(v -> {
            OFHelper.v(tag, "1Flow button size found 0 ");
            itemClicked(v, null, "");
        });

        submitButtonBeautification();

        OFHelper.v(tag, "1Flow list title[" + surveyScreens.getTitle() + "]");
        OFHelper.v(tag, "1Flow list desc[" + surveyScreens.getMessage() + "]length[" + surveyScreens.getMessage().length() + "]");


        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animationIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);

        if (OneFlow.titleFace != null) {
            if (OneFlow.titleFace.getTypeface() != null) {
                surveyTitle.setTypeface(OneFlow.titleFace.getTypeface());
            }
            if (OneFlow.titleFace.getFontSize() != null) {
                surveyTitle.setTextSize(OneFlow.titleFace.getFontSize());
            }
        }
        surveyTitle.setText(surveyScreens.getTitle());

        if (surveyScreens.getMessage() != null && surveyScreens.getMessage().length() > 0) {
            OFHelper.v(tag, "1Flow progress bar inside if");
            if (OneFlow.subTitleFace != null) {
                if (OneFlow.subTitleFace != null) {
                    surveyDescription.setTypeface(OneFlow.subTitleFace.getTypeface());
                }
                if (OneFlow.subTitleFace.getFontSize() != null) {
                    surveyDescription.setTextSize(OneFlow.subTitleFace.getFontSize());
                }
            }
            surveyDescription.setText(surveyScreens.getMessage());
        } else {
            OFHelper.v(tag, "1Flow progress bar inside else");
            surveyDescription.setVisibility(View.GONE);
        }


        if (surveyScreens.getInput().getRating_min_text() != null) {
            ratingsNotLike.setText(surveyScreens.getInput().getRating_min_text());
        }

        if (surveyScreens.getInput().getRating_max_text() != null) {
            ratingsFullLike.setText(surveyScreens.getInput().getRating_max_text());
        }


        webLayout = view.findViewById(R.id.weblayout);
        webContent = view.findViewById(R.id.webview_contents);
        pBar = view.findViewById(R.id.pbar);


        OFHelper.v(tag, "1Flow input type [" + surveyScreens.getInput().getInput_type() + "][" + surveyScreens.getInput().getStars() + "]min[" + surveyScreens.getInput().getMin_val() + "][" + surveyScreens.getInput().getMax_val() + "][][][]");
        if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_NUMERICAL)) {
            if (surveyScreens.getInput() != null) {
                //Setting default value if not received from api
                if (surveyScreens.getInput().getMin_val() == null) {
                    surveyScreens.getInput().setMin_val(1);
                }
                if (surveyScreens.getInput().getMax_val() == null || surveyScreens.getInput().getMax_val() == 0) {
                    surveyScreens.getInput().setMax_val("5");
                }

                surveyScreens.getInput().setRatingsList(prepareRatingsList(surveyScreens.getInput().getMin_val(), surveyScreens.getInput().getMax_val()));
            }

        } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_FIVE_START) || surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING)) {
            if (surveyScreens.getInput() != null) {

                // this is only to keep stars closer
                ViewGroup.LayoutParams params = surveyOptionRecyclerView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                surveyOptionRecyclerView.setLayoutParams(params);

                surveyScreens.getInput().setRatingsList(prepareRatingsList(1, 5));
                ratingsNotLike.setVisibility(View.GONE);
                ratingsFullLike.setVisibility(View.GONE);
            }
        } else if (surveyScreens.getInput().getInput_type().contains(OFConstants.STR_RATING_EMOJI)) {
            if (surveyScreens.getInput() != null) {

                surveyScreens.getInput().setRatingsList(prepareRatingsList(1, 5));
                ratingsNotLike.setVisibility(View.GONE);
                ratingsFullLike.setVisibility(View.GONE);
            }
        } else if (surveyScreens.getInput().getInput_type().contains("nps")) {
            //Setting default value if not received from api
            if (surveyScreens.getInput().getMin_val() == null) {
                surveyScreens.getInput().setMin_val(0);
            }
            if (surveyScreens.getInput().getMax_val() == null || surveyScreens.getInput().getMax_val() == 0) {
                surveyScreens.getInput().setMax_val("10");
            }

            surveyScreens.getInput().setRatingsList(prepareRatingsList(surveyScreens.getInput().getMin_val(), surveyScreens.getInput().getMax_val()));
        } else {
            ratingsNotLike.setVisibility(View.GONE);
            ratingsFullLike.setVisibility(View.GONE);
        }
        OFHelper.v(tag, "1Flow input type min after[" + surveyScreens.getInput().getMin_val() + "][" + surveyScreens.getInput().getMax_val() + "]");

        RecyclerView.LayoutManager mLayoutManager = null;
        if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_NUMERICAL) || surveyScreens.getInput().getInput_type().equalsIgnoreCase("nps")) {
            OFHelper.v(tag, "1Flow gridLayout set");
            mLayoutManager = new GridLayoutManager(getActivity(), (surveyScreens.getInput().getMax_val() + 1) - surveyScreens.getInput().getMin_val());
        } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_EMOJI)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 5);
        } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING) || surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_FIVE_START)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 5);
            starRatingLabel.setVisibility(View.VISIBLE);
            starRatingLabel.setText(surveyScreens.getInput().getRating_text().get("0"));
        } else {
            if (surveyScreens.getInput().getChoices() != null && !surveyScreens.getInput().getChoices().isEmpty()) {
                OFHelper.v(tag, "1Flow inputtype choices init");
                ratingsNotLike.setVisibility(View.GONE);
                starRatingLabel.setVisibility(View.GONE);
                ratingsFullLike.setVisibility(View.GONE);
                checkBoxSelection = new ArrayList<>();
            }

            OFHelper.v(tag, "1Flow linearlayout set");
            mLayoutManager = new LinearLayoutManager(getActivity());
        }

        OFHelper.v(tag, "1Flow theme color [" + themeColor + "]");
        dashboardAdapter = new OFSurveyOptionsAdapter(getActivity(), surveyScreens.getInput(), this, themeColor, OFHelper.handlerColor(sdkTheme.getText_color()));

        surveyOptionRecyclerView.setLayoutManager(mLayoutManager);
        surveyOptionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        surveyOptionRecyclerView.setAdapter(dashboardAdapter);


        return view;

    }


    private void submitButtonBeautification() {
        try {
            gdSubmit = (GradientDrawable) (submitButton).getBackground();
            gdSubmit.setColor(Color.parseColor(themeColor));
            int colorAlpha = OFHelper.manipulateColorNew(Color.parseColor(themeColor), OFConstants.buttonActiveValue);

            submitButton.setText(surveyScreens.getButtons().get(0).getTitle());
            submitButton.setTypeface(null,Typeface.BOLD);

            submitButton.setOnTouchListener((v, event) -> {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (checkBoxSelection != null && !checkBoxSelection.isEmpty()) {
                            gdSubmit.setColor(colorAlpha);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //touch move code
                        break;

                    case MotionEvent.ACTION_UP:
                        // touch up code
                        if (checkBoxSelection != null && !checkBoxSelection.isEmpty()) {
                            gdSubmit.setColor(Color.parseColor(themeColor));
                        }

                        break;
                    default:
                        break;
                }
                return false;
            });

            if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_CHECKBOX)) {
                gdSubmit.setColor(colorAlpha);
            } else {
                submitButton.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            // error
        }
    }

    private ArrayList<OFRatingsModel> prepareRatingsList(int min, int max) {
        ArrayList<OFRatingsModel> ratingsList = new ArrayList<>();
        OFRatingsModel rm = null;
        while (min <= max) {
            rm = new OFRatingsModel();
            rm.setId(min++);
            rm.setSelected(false);
            ratingsList.add(rm);
        }
        return ratingsList;
    }


    @Override
    public void itemClicked(View v, Object obj, String reserve) {

        long lastHitGap = System.currentTimeMillis() - OFOneFlowSHP.getInstance(getActivity()).getLongValue(OFConstants.SHP_LAST_CLICK_TIME);
        OFHelper.v(tag, "1Flow lastHit[" + lastHitGap + "]");
        if (lastHitGap > 1000 || surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_CHECKBOX)) {

            OFHelper.v(tag, "1Flow othervalue [" + obj + "]reserve[" + reserve + "]");
            if (v.getId() == R.id.submit_btn) {
                if (lastHitGap > 400) {
                    OFOneFlowSHP.getInstance(getActivity()).storeValue(OFConstants.SHP_LAST_CLICK_TIME, System.currentTimeMillis());
                    OFHelper.v(tag, "1Flow othervalue submit btn");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (checkBoxSelection != null && (!checkBoxSelection.isEmpty())) {
                                String allSelections = checkBoxSelection.toString().replace("[", "");
                                allSelections = allSelections.replace("]", "");
                                allSelections = allSelections.replace(" ", "");
                                OFHelper.v(tag, "1Flow allselection[" + allSelections + "] str[" + reserve + "]");
                                if (weakReference != null && weakReference.get() != null) {
                                    weakReference.get().addUserResponseToList(surveyScreens.get_id(), allSelections, reserve);
                                } else {
                                    OFHelper.v(tag, "1Flow no instance available to process");
                                }
                        }
                    }, 1000);
                }
            } else {
                OFOneFlowSHP.getInstance(getActivity()).storeValue(OFConstants.SHP_LAST_CLICK_TIME, System.currentTimeMillis());
                OFHelper.v(tag, "1Flow inputtype[" + surveyScreens.getInput().getInput_type() + "]isCheckbox[" + surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_CHECKBOX) + "]ratings[" + surveyScreens.getInput().getInput_type().contains(OFConstants.STR_RATING) + "]isStar[" + surveyScreens.getInput().getStars() + "]");
                if (surveyScreens.getInput().getInput_type().contains(OFConstants.STR_RATING_EMOJI)) {
                    int position = (int) v.getTag();
                    OFHelper.v(tag, "1Flow inputType[" + surveyScreens.getInput().getStars() + "]position[" + position + "]");
                    setSelected(position, true);

                } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING) ||
                        surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_FIVE_START)) {
                    int position = (int) v.getTag();
                    OFHelper.v(tag, "1Flow inputType[" + surveyScreens.getInput().getStars() + "]position[" + position + "]rating text[" + surveyScreens.getInput().getRating_text() + "]");
                    setSelected(position, false);
                    starRatingLabel.setText(surveyScreens.getInput().getRating_text().get(String.valueOf(position + 1)));
                } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("nps") ||
                        surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_NUMERICAL)) {
                    int position = (int) v.getTag();
                    setSelected(position, true);
                } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("mcq")) {
                    String position = (String) v.getTag();
                    if (v instanceof RadioButton) { // added for handling other click

                        OFHelper.v(tag, "1Flow mcq clicked Position[" + position + "]");
                        OFHelper.v(tag, "1Flow mcq clicked choices radio id[]other id[" + surveyScreens.getInput().getOtherOption() + "]");
                        if (!surveyScreens.getInput().getOtherOption().equalsIgnoreCase(position)) {

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (weakReference != null && weakReference.get() != null) {
                                    weakReference.get().addUserResponseToList(surveyScreens.get_id(), position, null);
                                } else {
                                    OFHelper.v(tag, "1Flow no instance available to process");
                                }
                            }, 5);
                        }
                    } else {

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().addUserResponseToList(surveyScreens.get_id(), position, (String) obj);
                            } else {
                                OFHelper.v(tag, "1Flow no instance available to process");
                            }
                        }, 5);
                    }

                } else if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_CHECKBOX)) {
                    OFHelper.v(tag, "1Flow inside checkbox reserve[" + reserve + "]");
                    if (v instanceof CheckBox) {
                        CheckBox cb = (CheckBox) v;
                        OFHelper.v(tag, "1Flow inside checkbox 1");
                        String viewTag = (String) cb.getTag();
                        OFHelper.v(tag, "1Flow inside checkbox tag[" + viewTag + "]isChecked[" + cb.isChecked() + "]");
                        checkBoxSelectionStatus(viewTag, cb.isChecked(), (String) obj);
                    } else {
                        String viewTag = (String) v.getTag();
                        checkBoxSelectionStatus(viewTag, (boolean) obj, reserve);
                    }
                }
            }
        } else {
            OFHelper.v(tag, "1Flow double click not allowed");
        }
    }


    private void setSelected(int position, Boolean isSingle) {
        int pos = 0;
        OFHelper.v(tag, "1Flow position [" + position + "]isSingle[" + isSingle + "]");
        try {

            while (pos < surveyScreens.getInput().getRatingsList().size()) {
                if (Boolean.TRUE.equals(isSingle)) {
                    surveyScreens.getInput().getRatingsList().get(pos).setSelected(false);
                } else {
                    surveyScreens.getInput().getRatingsList().get(pos).setSelected(pos <= position);
                }
                pos++;
            }
            if (Boolean.TRUE.equals(isSingle)) {
                if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("nps") ||
                        surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_NUMERICAL) ||
                        surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_FIVE_START)
                ) {
                    for (OFRatingsModel rm : surveyScreens.getInput().getRatingsList()) {
                        OFHelper.v(tag, "1Flow " + surveyScreens.getInput().getInput_type() + " rm.getId()[" + rm.getId() + "]position[" + position + "]");
                        if (rm.getId() == position) {
                            rm.setSelected(true);
                        }
                    }
                } else {
                    surveyScreens.getInput().getRatingsList().get(position).setSelected(true);
                }
            }
            dashboardAdapter.notifyMyList(surveyScreens.getInput());
            if (submitButton.getVisibility() != View.VISIBLE) {

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_FIVE_START) ||
                            surveyScreens.getInput().getInput_type().equalsIgnoreCase(OFConstants.STR_RATING_EMOJI)) {
                        if(weakReference != null && weakReference.get() != null) {
                            weakReference.get().addUserResponseToList(surveyScreens.get_id(), null, String.valueOf(position + 1));
                        }else{
                            OFHelper.v(tag,"1Flow no instance available to process");
                        }
                    } else {
                        if(weakReference != null && weakReference.get() != null) {
                            weakReference.get().addUserResponseToList(surveyScreens.get_id(), null, String.valueOf(position));
                        }else{
                            OFHelper.v(tag,"1Flow no instance available to process");
                        }
                    }
                }, 1000);
            }
        } catch (Exception ex) {
            OFHelper.e(tag, "setSelect[" + ex.getMessage() + "]");
        }
    }

    ArrayList<String> checkBoxSelection;

    private void checkBoxSelectionStatus(String tag, Boolean isCheck, String str) {
        OFHelper.v(tag, "1Flow button size tag[" + tag + "]isChecked[" + isCheck + "]othervalue[" + str + "]");
        if (Boolean.TRUE.equals(isCheck)) { // adding value in the list
            if (!checkBoxSelection.contains(tag)) {
                checkBoxSelection.add(tag);
            }
        } else { // removing value from the list
            checkBoxSelection.remove(tag);
        }

        if (!checkBoxSelection.isEmpty()) {
            if (surveyScreens.getButtons() != null) {
                if (surveyScreens.getButtons().size() == 1) {
                    submitButton.setText(surveyScreens.getButtons().get(0).getTitle());
                    OFHelper.v(tag, "1Flow color theme[" + themeColor + "]parsed color[" + Color.parseColor(themeColor) + "]");

                    if (!isActive) {
                        transitActive();
                        isActive = true;
                    }
                    submitButton.setOnClickListener(v -> {
                        OFHelper.v(tag, "1Flow button size found 1 ");
                        String strLoc = dashboardAdapter.handleCheckboxFromOutside();
                        itemClicked(v, str, strLoc);
                    });
                } else if (surveyScreens.getButtons().size() == 2) {
                    submitButton.setText(surveyScreens.getButtons().get(0).getTitle());
                    submitButton.setBackgroundColor(Color.parseColor(themeColor));
                    gdSubmit.setColor(Color.parseColor(themeColor));
                    submitButton.setOnClickListener(v -> {
                        OFHelper.v(tag, "1Flow button size found 2 ");
                        itemClicked(v, str, "");
                    });
                }
            } else {
                OFHelper.e(tag, "Button list not found");
            }
        } else {
            //In case of selection reverted


            if (isActive) {
                transitInActive();
                isActive = false;
            }

        }


    }


}
