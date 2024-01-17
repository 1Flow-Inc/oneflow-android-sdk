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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.fragment.OFAnnouncementFragment;
import com.oneflow.analytics.fragment.OFSurveyQueFragment;
import com.oneflow.analytics.fragment.OFSurveyQueInfoFragment;
import com.oneflow.analytics.fragment.OFSurveyQueTextFragment;
import com.oneflow.analytics.fragment.OFSurveyQueThankyouFragment;
import com.oneflow.analytics.model.announcement.OFAnnouncementTheme;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class OFAnnouncementActivityFullScreen extends AppCompatActivity {



    String tag = this.getClass().getName();
    ArrayList<String> idArray;
    ImageView closeBtn;
    OFCustomTextViewBold tvTitle;
    RelativeLayout relTopHeader;
    RelativeLayout viewLayout;
    String themeColor;
    LinearLayout waterMarkLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.announcement_view_full_screen);

        OFHelper.v(tag, "OneFlow reached at announcementActivity");

        idArray = (ArrayList<String>) getIntent().getSerializableExtra("announcementData");

        tvTitle = findViewById(R.id.tvTitle);
        relTopHeader = findViewById(R.id.relTopHeader);
        viewLayout = findViewById(R.id.view_layout);
        closeBtn = findViewById(R.id.close_btn_image_view);
        waterMarkLayout = findViewById(R.id.bottom_water_mark);

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(this);
        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getTheme() != null){
            OFAnnouncementTheme sdkTheme = shp.getAnnouncementResponse().getTheme();
            tvTitle.setTextColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getTextColor())));
            themeColor = OFHelper.handlerColor(sdkTheme.getBrandColor());
            viewLayout.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackgroundColor())));

            Drawable closeIcon = closeBtn.getDrawable();
            closeIcon.setColorFilter(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getTextColor())), 1.0f), PorterDuff.Mode.SRC_ATOP);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        waterMarkLayout.setOnClickListener(v -> {
            String waterMark1 = "https://1flow.app/?utm_source=1flow-android-sdk&utm_medium=watermark&utm_campaign=real-time+feedback+powered+by+1flow";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(waterMark1));
            startActivity(browserIntent);
        });

        loadFragments();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing,R.anim.fade_out_sdk);
    }

    Fragment frag;

    private void loadFragments() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (frag == null) {

            frag = getFragment();

            if (frag != null) {
                ft.add(R.id.fragment_view, frag, "0").commit();
            }
        }
    }

    public Fragment getFragment() {

        Fragment frag = null;
        try {
            frag = OFAnnouncementFragment.newInstance(idArray, null, "");
        } catch (Exception ex) {

            //  error
        }
        return frag;
    }
}
