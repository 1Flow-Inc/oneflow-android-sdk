package com.oneflow.analytics.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.gson.Gson;
import com.oneflow.analytics.R;
import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.model.survey.OFDataLogic;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.model.survey.OFSurveyUserResponseChild;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomFrag extends Fragment {

    String tag = this.getClass().getName();
    OFSurveyScreens surveyScreens;
    ArrayList<OFSurveyUserResponseChild> surveyResponseChildren;
    OFGetSurveyListResponse surveyItem;
    static CustomFrag myFragment;
    public static CustomFrag newInstance(){

        if(myFragment ==null){
             myFragment = new CustomFrag();
        }

        return myFragment;
    }

    ArrayList<OFSurveyScreens> screens;


    @Override
    public void onResume() {
        super.onResume();
        getView().setVisibility(View.GONE);
        IntentFilter inf = new IntentFilter();
        inf.addAction("CustomView");
        getActivity().registerReceiver(br, inf);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(br);
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OFHelper.v("SecondActivity broadcast recevice","OneFlow receiver called["+intent.getStringExtra("eventName")+"]");
            surveyItem = (OFGetSurveyListResponse) intent.getSerializableExtra("SurveyType");
            triggerEventName = intent.getStringExtra("eventName");
            if(surveyItem!=null) {
                getView().setVisibility(View.VISIBLE);
                initSurvey();
            }
        }
    };
    ProgressBar pagePositionPBar;
    ImageView closeBtn;
    View slider;
    RelativeLayout sliderLayout;
    RelativeLayout basePopupLayout;
    RelativeLayout mainChildForBackground;
    FrameLayout fragmentView;
    Long inTime = 0l;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.survey_view_custom_frag, container, false);

        inTime = System.currentTimeMillis();
        OFHelper.v(tag, "OneFlow reached at surveyActivity");
        pagePositionPBar = (ProgressBar) view.findViewById(R.id.pbar);
        closeBtn = (ImageView) view.findViewById(R.id.close_btn_image_view);

        basePopupLayout = (RelativeLayout) view.findViewById(R.id.base_popup_layout);
        mainChildForBackground = (RelativeLayout) view.findViewById(R.id.view_layout);
        fragmentView = (FrameLayout) view.findViewById(R.id.fragment_view);


        return view;

    }

    int position = 0;

    public void initFragment() {
        OFHelper.v(tag, "OneFlow answer position [" + position + "]screensize[" + screens.size() + "]selected answers[" + new Gson().toJson(surveyResponseChildren) + "]");
        if (position >= screens.size()) {
            ((ViewGroup)getView().getParent()).setVisibility(View.GONE);
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


    private void loadFragments() {

        setProgressAnimate();

        Fragment frag = getFragment();

        if (frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (position == 0) {
                ft.add(R.id.fragment_view, frag).commit();
            } else {
                ft.replace(R.id.fragment_view, frag).commit();
            }
        }

    }

    public Fragment getFragment() {
        Fragment frag = null;
        try {
            OFSurveyScreens screen = validateScreens();
            if (screen != null) {
                if (screen.getInput().getInput_type().equalsIgnoreCase("thank_you")) {
                    pagePositionPBar.setVisibility(View.GONE);
                    frag = OFSurveyQueThankyouFragment.newInstance(screen,sdkTheme,themeColor);
                } else if (screen.getInput().getInput_type().equalsIgnoreCase("text")) {
                    frag = OFSurveyQueTextFragment.newInstance(screen,sdkTheme,themeColor);
                } else {
                    frag = OFSurveyQueFragment.newInstance(screen,sdkTheme,themeColor);
                }
            }
        } catch (Exception ex) {
            OFHelper.e(tag, "OneFlow ERROR [" + ex.getMessage() + "]");
        }
        return frag;
    }

    String surveyName = "";
    OFSDKSettingsTheme sdkTheme;
    String triggerEventName = "";
    String surveyClosingStatus = "finished";

    public void initSurvey(){
        surveyName = surveyItem.getName();
        screens = surveyItem.getScreens();

        setProgressMax(surveyItem.getScreens().size() - 1); // -1 for excluding thankyou page from progress bar
        selectedSurveyId = surveyItem.get_id();
        closeBtn.setOnClickListener(v -> {

            //closed survey logic for storage.
            OFOneFlowSHP ofs = OFOneFlowSHP.getInstance(getActivity());
            List<String> closedSurveyList = ofs.getClosedSurveyList();
            OFHelper.v(tag, "OneFlow close button clicked [" + surveyResponseChildren + "]");

            surveyClosingStatus = "skipped";
            if (closedSurveyList != null && !closedSurveyList.contains(selectedSurveyId)) {
                closedSurveyList.add(selectedSurveyId);
                ofs.setClosedSurveyList(closedSurveyList);
                OFEventController ec = OFEventController.getInstance(getActivity());
                HashMap<String, Object> mapValue = new HashMap<>();
                mapValue.put("survey_id", selectedSurveyId);
                ec.storeEventsInDB(OFConstants.AUTOEVENT_CLOSED_SURVEY, mapValue, 0);
            }

            getActivity().finish();
        });


        themeColor = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
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
                themeColor = "#" + tranparancy + tempColor;
            } else {
                themeColor = tranparancy + tempColor;
            }
            OFHelper.v(tag, "OneFlow colors transparancy [" + tranparancy + "]tempColor[" + tempColor + "]themeColor[" + themeColor + "]");
        } catch (Exception ex) {
            //error
        }
        OFHelper.v(tag, "OneFlow color after[" + themeColor + "]");
        try {
            pagePositionPBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
        } catch (NumberFormatException nfe) {
            OFHelper.e(tag, "OneFlow color number format exception after[" + nfe.getMessage() + "]");
            themeColor = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            pagePositionPBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
        }

        sdkTheme = surveyItem.getSurveySettings().getSdkTheme();

        OFHelper.v(tag, "OneFlow sdkTheme [" + new Gson().toJson(sdkTheme) + "]");
        OFHelper.v(tag, "OneFlow sdkTheme Close[" + sdkTheme.getClose_button() + "]");
        OFHelper.v(tag, "OneFlow sdkTheme progress[" + sdkTheme.getProgress_bar() + "]");

        mainChildForBackground.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackground_color())));

        Drawable closeIcon = closeBtn.getDrawable();
        closeIcon.setColorFilter(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getText_color())), 0.6f), PorterDuff.Mode.SRC_ATOP);

        surveyResponseChildren = new ArrayList<>();


        OFHelper.v(tag, "OneFlow sdkTheme 0[" + sdkTheme + "]widget[" + sdkTheme.getWidgetPosition() + "]");
        OFHelper.v(tag, "OneFlow sdkTheme 0 Close[" + sdkTheme.getClose_button() + "]");
        OFHelper.v(tag, "OneFlow sdkTheme 0 progress[" + sdkTheme.getProgress_bar() + "]");
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

        initFragment();
    }

    String themeColor = "";
    String selectedSurveyId;

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
                flow.addOnSuccessListener(result -> {

                });
            }
        });


    }

    /**
     * This method will check if any unknown survey has came
     *
     * @return
     */
    public OFSurveyScreens validateScreens() {
        String[] possibleType = new String[]{"text", "thank_you", "rating-numerical", "rating-5-star", "rating", "rating-emojis", "nps"};
        OFSurveyScreens screen = null;

        while (position < screens.size()) {
            screen = screens.get(position);
            if (Arrays.asList(possibleType).contains(screen.getInput().getInput_type())) {
                break;
            } else {
                position++;
            }
        }


        return screen;
    }
}
