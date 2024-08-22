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

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.utils.OFHelper;

public class OFSurveyQueInfoFragment extends BaseFragment implements View.OnClickListener{


    ImageView waterMarkImage;

    OFCustomTextViewBold surveyTitle;
    OFCustomTextViewBold submitButton;

    OFCustomTextView surveyDescription;

    String tag = this.getClass().getName();


    public static OFSurveyQueInfoFragment newInstance(OFSurveyScreens ahdList, OFSDKSettingsTheme sdkTheme, String themeColor) {
        OFSurveyQueInfoFragment myFragment = new OFSurveyQueInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable("data", ahdList);
        args.putSerializable("theme", sdkTheme);
        args.putString("themeColor", themeColor);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.survey_que_info_fragment, container, false);
        OFHelper.v(tag, "1Flow list data[" + new Gson().toJson(surveyScreens) + "]");

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
        animation5 = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);

        webLayout = view.findViewById(R.id.weblayout);
        webContent = view.findViewById(R.id.webview_contents);
        pBar = view.findViewById(R.id.pbar);

        waterMarkImage = (ImageView) view.findViewById(R.id.watermark_img);
        waterMarkLayout = (LinearLayout) view.findViewById(R.id.bottom_water_mark);
        infoWebLayout = (LinearLayout) view.findViewById(R.id.info_weblayout);
        submitButton = (OFCustomTextViewBold) view.findViewById(R.id.submit_btn);


        surveyTitle = (OFCustomTextViewBold) view.findViewById(R.id.survey_title);
        surveyDescription = (OFCustomTextView) view.findViewById(R.id.survey_sub_title);

        surveyTitle.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())));

        int colorAlpha = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.8f);
        int colorlike = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f);
        ((TextView) waterMarkLayout.getChildAt(1)).setTextColor(colorlike);
        surveyDescription.setTextColor(colorAlpha);


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
        submitButton.setOnClickListener(this);
        handleWaterMarkStyle(sdkTheme);
        try {
            if (!OFHelper.validateString(surveyScreens.getButtons().get(0).getTitle()).equalsIgnoreCase("NA")) {
                submitButton.setText(surveyScreens.getButtons().get(0).getTitle());
            }
        } catch (Exception ex) {
            OFHelper.e(tag, "Button list not found");
        }
        submitButtonBeautification();
        submitButton.requestFocus();
        transitActive();

        return view;

    }

    private void submitButtonBeautification() {
        try {
            gdSubmit = (GradientDrawable) (submitButton).getBackground();

            int colorAlpha = OFHelper.manipulateColor(Color.parseColor(themeColor), 0.5f);
            gdSubmit.setColor(colorAlpha);
            submitButton.setTypeface(null, Typeface.BOLD);

            submitButton.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gdSubmit.setColor(colorAlpha);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        gdSubmit.setColor(Color.parseColor(themeColor));
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_sdk);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Start
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // End
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Repeat
                }
            });
            surveyTitle.startAnimation(animation);
        }
    }

    public void handleClick(View v) {
        if (v.getId() == R.id.watermark_img) {
            String waterMark = "https://1flow.app/?utm_source=1flow-android-sdk&utm_medium=watermark&utm_campaign=real-time+feedback+powered+by+1flow";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(waterMark));
            startActivity(browserIntent);

        }
    }

    int i = 0;
    Dialog dialog;
    Animation animation1;
    Animation animation2;
    Animation animation3;
    Animation animation4;
    Animation animation5;

    @Override
    public void onResume() {
        super.onResume();

        setupWeb();

        View[] animateViews;
        if(OFHelper.validateString(surveyScreens.getMediaEmbedHTML()).equalsIgnoreCase("NA")) {
            animateViews = new View[]{surveyTitle, surveyDescription, submitButton};
        }else{
            animateViews = new View[]{surveyTitle, surveyDescription, infoWebLayout, submitButton};
        }


        Animation[] annim = new Animation[]{animation1, animation2, animation3, animation4, animation5};

        if (i == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if(i < animateViews.length){
                    annim[i].setFillAfter(true);
                    animateViews[i].startAnimation(annim[i]);
                }
            }, 500);

            animation1.setAnimationListener(new Animation.AnimationListener() {
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

    }
    @Override
    public void onClick(View v) {

        if(weakReference != null && weakReference.get() != null) {
            weakReference.get().addUserResponseToList(surveyScreens.get_id(), null, null);
        }else{
            OFHelper.v(tag,"1Flow no instance available to process");
        }
    }
}
