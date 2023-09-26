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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomEditText;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;


public class OFSurveyQueTextFragment extends BaseFragment implements View.OnClickListener {


    OFCustomTextViewBold surveyTitle;
    OFCustomTextViewBold submitButton;
    RelativeLayout optionLayout;
    RelativeLayout optionLayoutOuter;
    OFCustomEditText userInput;
    OFCustomEditText userInputShort;

    OFCustomTextView surveyInputLimit;
    OFCustomTextView skipBtn;

    OFCustomTextView surveyDescription;

    String userText = "";

    String tag = this.getClass().getName();


    public static OFSurveyQueTextFragment newInstance(OFSurveyScreens ahdList, OFSDKSettingsTheme sdkTheme, String themeColor) {
        OFSurveyQueTextFragment myFragment = new OFSurveyQueTextFragment();

        Bundle args = new Bundle();
        args.putSerializable("data", ahdList);
        args.putSerializable("theme", sdkTheme);
        args.putString("themeColor", themeColor);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        OFHelper.v(tag, "1Flow reached onSaveInstanceState");
        if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("short-text")) {
            OFOneFlowSHP.getInstance(getActivity()).storeValue("userInput", userInputShort.getText().toString());
        } else {
            OFOneFlowSHP.getInstance(getActivity()).storeValue("userInput", userInput.getText().toString());
        }

        outState.putString("userText", userText);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        try {
            OFHelper.v(tag, "1Flow reached onSaveInstanceState");
            if (savedInstanceState != null) {
                userText = savedInstanceState.getString("userText");
            }
            OFHelper.v(tag, "1Flow reached onSaveInstanceState0[" + userText + "]");
            if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("short-text")) {
                userInputShort.setText(userText);
            } else {
                userInput.setText(userText);
            }
        } catch (Exception ex) {
            // error
        }
    }

    int i = 0;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        OFHelper.v(tag, "1Flow visible to user");
        if (isVisibleToUser) {

            View[] animateViews = new View[]{surveyTitle, surveyDescription};


            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Start
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (i < animateViews.length) {
                        animateViews[i++].startAnimation(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Repeat
                }
            });
            animateViews[i++].startAnimation(animation);
        }
    }


    Animation animation1;
    Animation animation2;
    Animation animation3;
    Animation animation4;
    Animation animation5;
    Animation animationIn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.survey_que_text_fragment, container, false);

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation5 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);


        surveyTitle = (OFCustomTextViewBold) view.findViewById(R.id.survey_title);
        submitButton = (OFCustomTextViewBold) view.findViewById(R.id.submit_btn);
        surveyDescription = (OFCustomTextView) view.findViewById(R.id.survey_description);
        skipBtn = (OFCustomTextView) view.findViewById(R.id.skip_btn);
        userInput = (OFCustomEditText) view.findViewById(R.id.child_user_input);
        userInputShort = (OFCustomEditText) view.findViewById(R.id.child_user_input_short);
        surveyInputLimit = (OFCustomTextView) view.findViewById(R.id.text_limit);
        optionLayoutOuter = (RelativeLayout) view.findViewById(R.id.input_layout_view);
        optionLayout = (RelativeLayout) view.findViewById(R.id.option_layout);
        waterMarkLayout = (LinearLayout) view.findViewById(R.id.bottom_water_mark);


        handleWaterMarkStyle(sdkTheme);
        surveyTitle.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())));

        int colorAlpha = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.8f);
        int colorlike = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f);
        surveyDescription.setTextColor(colorAlpha);
        skipBtn.setTextColor(colorlike);
        ((TextView) waterMarkLayout.getChildAt(1)).setTextColor(colorlike);


        skipBtn.setOnClickListener(this);

        surveyInputLimit.setTextColor(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.5f));


        OFHelper.v(tag, "1Flow list data[" + surveyScreens + "]");
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
        if (surveyScreens.getMessage() != null) {

            if (OneFlow.subTitleFace != null) {
                if (OneFlow.subTitleFace.getTypeface() != null) {
                    surveyDescription.setTypeface(OneFlow.subTitleFace.getTypeface());
                }

                if (OneFlow.subTitleFace.getFontSize() != null) {
                    surveyDescription.setTextSize(OneFlow.subTitleFace.getFontSize());
                }
            }
            surveyDescription.setText(surveyScreens.getMessage());
        } else {
            surveyDescription.setVisibility(View.GONE);
        }

        webLayout = view.findViewById(R.id.weblayout);
        webContent = view.findViewById(R.id.webview_contents);
        pBar = view.findViewById(R.id.pbar);


        surveyInputLimit.setText("0/" + surveyScreens.getInput().getMax_chars());
        OFHelper.v(tag, " OneFlow onTextChanged min[" + surveyScreens.getInput().getMin_chars() + "]max[" + surveyScreens.getInput().getMax_chars() + "]");
        userInput.setHintTextColor(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.5f));
        userInput.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())));
        userInputShort.setHintTextColor(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.5f));
        userInputShort.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())));

        if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("text")) {
            userInput.setHint(surveyScreens.getInput().getPlaceholderText());
            userInputShort.setVisibility(View.GONE);
            optionLayout.setVisibility(View.VISIBLE);
            submitButtonBeautification();
        } else {
            userInputShort.setHint(surveyScreens.getInput().getPlaceholderText());
            optionLayout.setVisibility(View.GONE);
            userInputShort.setVisibility(View.VISIBLE);
        }

        submitButton.requestFocus();
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // beforeTextChanged
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (userInput.getText().toString().trim().length() >= surveyScreens.getInput().getMin_chars()) {

                    try {
                        if (!OFHelper.validateString(surveyScreens.getButtons().get(0).getTitle()).equalsIgnoreCase("NA")) {
                            submitButton.setText(surveyScreens.getButtons().get(0).getTitle());
                        }
                    } catch (Exception ex) {
                        OFHelper.e(tag, "Button list not found");
                    }

                    if (!isActive) {
                        transitActive();
                        isActive = true;
                    }

                } else {
                    if (surveyScreens.getButtons().size() == 1 || surveyScreens.getButtons().size() == 2 && (isActive)) {
                        transitInActive();
                        isActive = false;
                    }
                }
                if (userInput.getText().toString().length() > surveyScreens.getInput().getMax_chars()) {
                    OFHelper.makeText(getActivity(), "You have exceeded max length", 1);
                    userInput.setText(userInput.getText().toString().substring(0, userInput.getText().length() - count));
                    userInput.setSelection(userInput.getText().toString().length());
                }

                surveyInputLimit.setText(userInput.getText().toString().length() + "/" + surveyScreens.getInput().getMax_chars());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // afterTextChanged
            }
        });

        submitButton.setOnClickListener(this);

        return view;

    }


    private void submitButtonBeautification() {
        try {
            gdSubmit = (GradientDrawable) (submitButton).getBackground();

            int colorAlpha = OFHelper.manipulateColorNew(Color.parseColor(themeColor), OFConstants.buttonActiveValue);
            gdSubmit.setColor(colorAlpha);
            submitButton.setTypeface(null, Typeface.BOLD);

            submitButton.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (userInput.getText().toString().trim().length() >= surveyScreens.getInput().getMin_chars()) {
                            gdSubmit.setColor(colorAlpha);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;

                    case MotionEvent.ACTION_UP:
                        if (userInput.getText().toString().trim().length() >= surveyScreens.getInput().getMin_chars()) {
                            gdSubmit.setColor(Color.parseColor(themeColor));
                        }
                        break;
                    default:
                        break;
                }
                return false;
            });
        } catch (Exception ex) {
            // error
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupWeb();
        View[] animateViews;
        String savedValue = OFOneFlowSHP.getInstance(getActivity()).getStringValue("userInput");

        animateViews = new View[]{surveyTitle, surveyDescription, optionLayoutOuter, submitButton};

        if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("text")) {

            if (!savedValue.equalsIgnoreCase("NA")) {
                userInput.setText(savedValue);
            }
        } else {
            if (!savedValue.equalsIgnoreCase("NA")) {
                userInputShort.setText(savedValue);
            }
        }

        Animation[] annim = new Animation[]{animation1, animation2, animation3, animation4, animation5};

        if (i == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                annim[i].setFillAfter(true);
                animateViews[i].startAnimation(annim[i]);

            }, 500);

            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    OFHelper.v(tag, "1Flow animation START [" + i + "]");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    OFHelper.v(tag, "1Flow animation END[" + i + "]");
                    //
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
            animation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    OFHelper.v(tag, "1Flow animation START [" + i + "]");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    OFHelper.v(tag, "1Flow animation END[" + i + "]");
                    //
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
                    OFHelper.v(tag, "1Flow animation START [" + i + "]");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    OFHelper.v(tag, "1Flow animation END[" + i + "]");

                    i++;

                    if (i < animateViews.length) {
                        try {
                            OFHelper.v(tag, "1Flow min char reached [" + surveyScreens.getButtons().get(0).getTitle() + "]");
                            if (!OFHelper.validateString(surveyScreens.getButtons().get(0).getTitle()).equalsIgnoreCase("NA")) {
                                ((OFCustomTextViewBold) animateViews[i]).setText(surveyScreens.getButtons().get(0).getTitle());
                            }
                            animateViews[i].setVisibility(View.VISIBLE);
                            animateViews[i].startAnimation(annim[i]);

                        } catch (Exception ex) {
                            OFHelper.e(tag, "Button list not found");
                        }
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    OFHelper.v(tag, "1Flow animation REPEAT[" + i + "]");
                }
            });
            animation4.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    OFHelper.v(tag, "1Flow animation4 START [" + i + "]");
                }

                @Override
                public void onAnimationEnd(Animation animation) {


                    OFHelper.v(tag, "1Flow animation4 END[" + i + "]len[" + animateViews.length + "][" + surveyScreens.getInput().getMin_chars() + "]");


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
        }

        OFHelper.v(tag, "1Flow reached onResume 0[" + userText + "]");
        if (!userText.isEmpty()) {
            if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("short-text")) {
                userInputShort.setText(userText);
            } else {
                userInput.setText(userText);
            }
        }

    }

    @Override
    public void onClick(View v) {
        long lastHitGap = System.currentTimeMillis() - OFOneFlowSHP.getInstance(getActivity()).getLongValue(OFConstants.SHP_LAST_CLICK_TIME);
        OFHelper.v(tag, "1Flow lastHit[" + lastHitGap + "]");
        if (lastHitGap > 1500) {
            OFOneFlowSHP.getInstance(getActivity()).storeValue("userInput", "");
            OFOneFlowSHP.getInstance(getActivity()).storeValue(OFConstants.SHP_LAST_CLICK_TIME, System.currentTimeMillis());
            if (v.getId() == R.id.skip_btn) {
                if (weakReference != null) {
                    weakReference.get().addUserResponseToList(surveyScreens.get_id(), null, null);
                } else {
                    OFHelper.v(tag, "1Flow no instance available to process");
                }
            } else if (v.getId() == R.id.submit_btn) {
                if (surveyScreens.getInput().getInput_type().equalsIgnoreCase("text")) {
                    if (userInput.getText().toString().trim().length() >= surveyScreens.getInput().getMin_chars()) {
                        if (weakReference != null) {
                            weakReference.get().addUserResponseToList(surveyScreens.get_id(), null, userInput.getText().toString().trim().length() > 0 ? userInput.getText().toString().trim() : null);
                        } else {
                            OFHelper.v(tag, "1Flow no instance available to process");
                        }
                    }
                } else {
                    if (weakReference != null) {
                        weakReference.get().addUserResponseToList(surveyScreens.get_id(), null, userInputShort.getText().toString().trim().length() > 0 ? userInputShort.getText().toString().trim() : null);
                    } else {
                        OFHelper.v(tag, "1Flow no instance available to process");
                    }
                }

            } else if (v.getId() == R.id.cancel_btn) {
                //  Cancel button
            }
        }
    }

}
