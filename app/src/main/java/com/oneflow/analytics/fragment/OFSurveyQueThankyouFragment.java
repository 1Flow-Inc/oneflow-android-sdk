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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.oneflow.analytics.OneFlow;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.model.survey.OFDataLogic;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.utils.OFHelper;

public class OFSurveyQueThankyouFragment extends BaseFragment {

    ImageView thankyouImage;
    ImageView waterMarkImage;

    OFCustomTextViewBold surveyTitle;

    OFCustomTextView surveyDescription;

    String tag = this.getClass().getName();

    public static OFSurveyQueThankyouFragment newInstance(OFSurveyScreens ahdList, OFSDKSettingsTheme sdkTheme, String themeColor) {

        OFSurveyQueThankyouFragment myFragment = new OFSurveyQueThankyouFragment();

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
        View view = inflater.inflate(R.layout.survey_que_thankyou_fragment, container, false);

        OFHelper.v(tag, "1Flow list data.[" + surveyScreens + "]");
        OFHelper.v(tag, "1Flow list data.html[" + surveyScreens.getMediaEmbedHTML() + "]");



        thankyouImage = (ImageView) view.findViewById(R.id.thankyou_img);
        waterMarkImage = (ImageView) view.findViewById(R.id.watermark_img);
        waterMarkLayout = (LinearLayout) view.findViewById(R.id.bottom_water_mark);

        surveyTitle = (OFCustomTextViewBold) view.findViewById(R.id.survey_title);
        surveyDescription = (OFCustomTextView) view.findViewById(R.id.survey_sub_title);
        surveyTitle.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())));

        int colorAlpha = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.8f);
        int colorlike = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f);
        ((TextView) waterMarkLayout.getChildAt(1)).setTextColor(colorlike);
        surveyDescription.setTextColor(colorAlpha);

        webLayout = view.findViewById(R.id.weblayout);
        webContent = view.findViewById(R.id.webview_contents);
        pBar = view.findViewById(R.id.pbar);

        setupWeb();

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

        if(weakReference != null && weakReference.get() != null) {
            weakReference.get().position = weakReference.get().screens.size();
        }else{
            OFHelper.v(tag,"1Flow no instance available to process");
        }
        handleWaterMarkStyle(sdkTheme);
        Glide.with(this).load(R.drawable.thanku_bg).into(new DrawableImageViewTarget(thankyouImage) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                super.onResourceReady(resource, transition);
                if (resource instanceof GifDrawable) {
                    ((GifDrawable) resource).setLoopCount(1);
                    ((GifDrawable) resource).registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationStart(Drawable drawable) {
                            // Start
                        }

                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            super.onAnimationEnd(drawable);

                            if (surveyScreens.getRules() != null) {
                                if (surveyScreens.getRules().getDismissBehavior() != null) {
                                    boolean fades = surveyScreens.getRules().getDismissBehavior().getFadesAway();
                                    if (fades) {
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                            ruleAction();
                                            if(weakReference != null && weakReference.get() != null) {
                                                weakReference.get().finish();
                                            }else{
                                                OFHelper.v(tag,"1Flow no instance available to process");
                                            }
                                        }, (long)surveyScreens.getRules().getDismissBehavior().getDelayInSeconds() * 1000);
                                        // above logic is added for fade away if true then should fade away in mentioned duration
                                    }
                                } else {
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        ruleAction();
                                        if(weakReference != null && weakReference.get() != null) {
                                            weakReference.get().finish();
                                        }else{
                                            OFHelper.v(tag,"1Flow no instance available to process");
                                        }
                                    }, 20);
                                }
                            } else {
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    if(weakReference != null && weakReference.get() != null) {
                                        weakReference.get().finish();
                                    }else{
                                        OFHelper.v(tag,"1Flow no instance available to process");
                                    }
                                }, 20);
                            }
                        }
                    });
                }

            }
        });
        if(weakReference != null && weakReference.get() != null) {
            weakReference.get().initFragment(5);
        }else{
            OFHelper.v(tag,"1Flow no instance available to process");
        }
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //Logic for showing close button if fade away is false then have to show close button at thankyou page
            boolean fades = surveyScreens.getRules().getDismissBehavior().getFadesAway();
            if (!fades) {
                if(weakReference != null && weakReference.get() != null) {
                    weakReference.get().closeBtn.setVisibility(View.VISIBLE);
                }else{
                    OFHelper.v(tag,"1Flow no instance available to process");
                }
            }
        } catch (Exception ex) {
           // error
        }
    }

    private void ruleAction() {
        if (surveyScreens.getRules() != null && (surveyScreens.getRules().getDataLogic() != null && !surveyScreens.getRules().getDataLogic().isEmpty())) {
                OFDataLogic dl = surveyScreens.getRules().getDataLogic().get(0);
                if (dl != null) {
                    if (dl.getType().equalsIgnoreCase("open-url")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dl.getAction()));
                        startActivity(browserIntent);
                    } else if (dl.getType().equalsIgnoreCase("rating")) {
                        if(weakReference != null && weakReference.get() != null) {
                            weakReference.get().reviewThisApp(getActivity());
                        }else{
                            OFHelper.v(tag,"1Flow no instance available to process");
                        }
                    }
                }

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
}
