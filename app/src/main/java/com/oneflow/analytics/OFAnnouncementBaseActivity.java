package com.oneflow.analytics;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oneflow.analytics.model.announcement.OFAnnouncementTheme;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFHelper;

public class OFAnnouncementBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void setTheme(RelativeLayout viewLayout, ImageView closeBtnImageView){
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(this);
        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getTheme() != null){
            OFAnnouncementTheme sdkTheme = shp.getAnnouncementResponse().getTheme();

            viewLayout.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackgroundColor())));

            Drawable closeIcon = closeBtnImageView.getDrawable();
            closeIcon.setColorFilter(OFHelper.manipulateColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getTextColor())), 1.0f), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
