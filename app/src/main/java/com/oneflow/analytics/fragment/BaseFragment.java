package com.oneflow.analytics.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.oneflow.analytics.OFSDKBaseActivity;
import com.oneflow.analytics.OFSurveyActivityFullScreen;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomeWebView;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.lang.ref.WeakReference;

public class BaseFragment extends Fragment {
    boolean isActive = false;
    GradientDrawable gdSubmit;
    View webLayout;
    ProgressBar pBar;
    OFCustomeWebView webContent;
    LinearLayout waterMarkLayout;
    LinearLayout infoWebLayout;
    OFSurveyScreens surveyScreens;
    OFSDKSettingsTheme sdkTheme;
    String themeColor;
    String tag = this.getClass().getName();

    WeakReference<OFSDKBaseActivity> weakReference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            weakReference = new WeakReference<>((OFSDKBaseActivity) context);

            OFHelper.v(tag, "1Flow custom survery reading");
        } catch (Exception ex) {
            OFHelper.v(tag, "1Flow custom survery exception");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        weakReference.clear();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        OFHelper.v(tag, "1Flow frag onSaveInstanceState called 0");
        outState.putSerializable("data", surveyScreens);
        outState.putSerializable(OFConstants.PASS_THEME, sdkTheme);
        outState.putSerializable(OFConstants.PASS_THEME_COLOR, themeColor);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) {
            OFHelper.v(tag, "1Flow frag onCreate called 0");
            surveyScreens = (OFSurveyScreens) savedInstanceState.getSerializable("data");
            sdkTheme = (OFSDKSettingsTheme) savedInstanceState.getSerializable(OFConstants.PASS_THEME);
            themeColor = savedInstanceState.getString(OFConstants.PASS_THEME_COLOR);
        } else {
            OFHelper.v(tag, "1Flow frag onCreate called 1");
            surveyScreens = (OFSurveyScreens) getArguments().getSerializable("data");
            sdkTheme = (OFSDKSettingsTheme) getArguments().getSerializable(OFConstants.PASS_THEME);
            themeColor = getArguments().getString(OFConstants.PASS_THEME_COLOR);
        }
        OFHelper.v(tag, "1Flow frag data[" + surveyScreens.getMediaEmbedHTML() + "]");


    }

    int thisViewHeight = 0;

    public void setThisViewHeight(int newHeight) {
        if (newHeight > thisViewHeight) {
            thisViewHeight = newHeight;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!OFHelper.validateString(surveyScreens.getMediaEmbedHTML()).equalsIgnoreCase("NA")) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    try {
                        setThisViewHeight(((View) ((View) ((View) ((View) ((View) view.getParent()).getParent()).getParent()).getParent()).getParent()).getHeight());
                    } catch (Exception ex) {
                        setThisViewHeight(((View) ((View) ((View) ((View) view.getParent()).getParent()).getParent()).getParent()).getHeight());
                    }

                }
            });
            OFHelper.e(tag, "1Flow view created now [" + view.getHeight() + "]parent[" + ((View) view.getParent()).getHeight() + "]");
        }
    }

    public void setupWeb() {

        double[] data = OFHelper.getScreenSize(getActivity());
        OFHelper.v(tag, "1Flow Window size width[" + data[0] + "]");

        if (OFHelper.validateString(surveyScreens.getMediaEmbedHTML()).equalsIgnoreCase("NA")) {
            webLayout.setVisibility(View.GONE);
        } else {
            if (surveyScreens.getMediaEmbedHTML().contains("<video") || surveyScreens.getMediaEmbedHTML().contains("<iframe")) {
                webContent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                webContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            webContent.getSettings().setJavaScriptEnabled(true);
            webContent.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webContent.getSettings().setBlockNetworkImage(false);
            webContent.setBackgroundColor(Color.TRANSPARENT);
            webLayout.setVisibility(View.VISIBLE);
            webContent.setVisibility(View.VISIBLE);

            webContent.setWebChromeClient(new WebChromeClient() {
                                              @Override
                                              public void onProgressChanged(WebView view, int newProgress) {
                                                  super.onProgressChanged(view, newProgress);
                                                  try {
                                                      if (((View) ((View) ((View) ((View) ((View) view.getParent()).getParent()).getParent()).getParent()).getParent()).getParent() != null) {
                                                          setThisViewHeight(((View) ((View) ((View) ((View) ((View) view.getParent()).getParent()).getParent()).getParent()).getParent()).getHeight());
                                                      }
                                                  } catch (Exception ex) {
                                                      setThisViewHeight(((View) ((View) ((View) ((View) view.getParent()).getParent()).getParent()).getParent()).getHeight());
                                                  }
                                              }
                                          }
            );


            String webData = "<html><head></head><body style='margin:0;padding:0;'>" + surveyScreens.getMediaEmbedHTML() + "</body></html>";
            webContent.loadDataWithBaseURL(null, webData, "text/html", "UTF-8", null);


        }

    }

    public void transitActive() {
        try {

            int colorFrom;
            int colorTo;
            if (weakReference.get() != null) {
                colorFrom = OFHelper.manipulateColorNew(Color.parseColor(weakReference.get().themeColor), OFConstants.buttonActiveValue);
                colorTo = Color.parseColor(weakReference.get().themeColor);
            } else {
                colorFrom = ContextCompat.getColor(getActivity(),R.color.btn_pressed);
                colorTo = ContextCompat.getColor(getActivity(),R.color.btn_normal);
            }
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(250); // milliseconds
            colorAnimation.addUpdateListener(animator -> gdSubmit.setColor((int) animator.getAnimatedValue()));
            colorAnimation.start();
        } catch (Exception ex) {
           // error
        }
    }

    public void transitInActive() {
        try {
            int colorFrom;
            int colorTo;

            if (weakReference.get() != null) {
                colorFrom = OFHelper.manipulateColorNew(Color.parseColor(weakReference.get().themeColor), OFConstants.buttonActiveValue);
                colorTo = Color.parseColor(weakReference.get().themeColor);
            } else {
                colorFrom = ContextCompat.getColor(getActivity(),R.color.btn_pressed);
                colorTo = ContextCompat.getColor(getActivity(),R.color.btn_normal);
            }

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(250); // milliseconds
            colorAnimation.addUpdateListener(animator -> gdSubmit.setColor((int) animator.getAnimatedValue()));
            colorAnimation.start();
        } catch (Exception ex) {
           // error
        }
    }

    public void handleWaterMarkStyle(OFSDKSettingsTheme theme) {

        try {

            if (weakReference.get() != null) {
                if (weakReference.get() instanceof OFSurveyActivityFullScreen) {
                    waterMarkLayout.setVisibility(View.GONE);
                } else {
                    boolean isRemoveWaterMark = theme.getRemove_watermark();
                    if (isRemoveWaterMark) {
                        waterMarkLayout.setVisibility(View.GONE);
                    } else {
                        waterMarkLayout.setVisibility(View.VISIBLE);
                    }
                    int colorAlpha = OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(theme.getText_color())), 0.1f);
                    GradientDrawable gd = (GradientDrawable) waterMarkLayout.getBackground();
                    waterMarkLayout.setOnClickListener(v -> {

                        String waterMark = "https://1flow.app/?utm_source=1flow-android-sdk&utm_medium=watermark&utm_campaign=real-time+feedback+powered+by+1flow";
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(waterMark));
                        startActivity(browserIntent);


                    });
                    waterMarkLayout.setOnTouchListener((v, event) -> {

                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                gd.setColor(colorAlpha);
                                break;

                            case MotionEvent.ACTION_MOVE:
                                // touch move code
                                break;

                            case MotionEvent.ACTION_UP:

                                gd.setColor(null);

                                break;
                            default:
                                break;
                        }
                        return false;
                    });
                }
            }
        } catch (Exception ex) {
            OFHelper.e("BaseFragment", "1Flow watermark error ");
        }
    }
}
