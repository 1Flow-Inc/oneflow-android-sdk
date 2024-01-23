package com.oneflow.analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.databinding.ActivityOfannouncementBannerBottomBinding;
import com.oneflow.analytics.databinding.ActivityOfannouncementBannerTopBinding;
import com.oneflow.analytics.model.announcement.OFAnnouncementTheme;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class OFAnnouncementActivityBannerBottom extends OFAnnouncementBaseActivity {

    String tag = this.getClass().getName();
    Window window;
    ActivityOfannouncementBannerBottomBinding binding;
    ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses;

    int textColor = Color.RED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityOfannouncementBannerBottomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getAnnouncementDetailResponses = (ArrayList<OFGetAnnouncementDetailResponse>) getIntent().getSerializableExtra("announcementData");

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(this);
        if(shp.getAnnouncementResponse() != null && shp.getAnnouncementResponse().getTheme() != null){
            OFAnnouncementTheme sdkTheme = shp.getAnnouncementResponse().getTheme();
            textColor = Color.parseColor(OFHelper.pickFontColorBasedOnBgColor(OFHelper.handlerColor(sdkTheme.getBackgroundColor()),"#ffffff","#000000"));

            binding.viewLayout.setBackgroundColor(Color.parseColor(OFHelper.handlerColor(sdkTheme.getBackgroundColor())));

            binding.tvTitle.setTextColor(textColor);
            Drawable closeIcon = binding.closeBtnImageView.getDrawable();
            closeIcon.setColorFilter(OFHelper.manipulateColor(textColor, 1.0f), PorterDuff.Mode.SRC_ATOP);
        }

        if(getAnnouncementDetailResponses != null && getAnnouncementDetailResponses.get(0) != null){
            String text = "";
            OFGetAnnouncementDetailResponse getAnnouncementDetailResponse = getAnnouncementDetailResponses.get(0);

            handleSeen(getAnnouncementDetailResponse.getId());

            viewedAnnouncement(getAnnouncementDetailResponse.getId());

            text = getAnnouncementDetailResponse.getTitle();

            if(getAnnouncementDetailResponse.getAction() != null && getAnnouncementDetailResponse.getAction().getLink() != null){
                text += " " + getAnnouncementDetailResponse.getAction().getName();
            }

            final SpannableString spannableString = new SpannableString(text);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String action = getAnnouncementDetailResponse.getAction().getLink();
                    if(action != null && !action.isEmpty()){
                        clickedAnnouncement(getAnnouncementDetailResponse.getId(),getAnnouncementDetailResponse.getAction().getName(),action);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(browserIntent);
                    }
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                    ds.setColor(textColor);
                }
            };
            spannableString.setSpan(clickableSpan, text.length() - getAnnouncementDetailResponse.getAction().getName().length(), spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            binding.tvTitle.setText(spannableString);
            binding.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            binding.tvTitle.setHighlightColor(Color.TRANSPARENT);
        }

//        setTheme(binding.viewLayout, binding.closeBtnImageView);

        binding.closeBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        window = this.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        OFHelper.v(tag, "OneFlow Window size width[" + window.getAttributes().width + "]height[" + window.getAttributes().height + "]");

        wlp.gravity = Gravity.BOTTOM;
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        window.setAttributes(wlp);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing,R.anim.slide_down_new_theme);
    }

    private void clickedAnnouncement(String id, String text, String url){
        OFEventController ec = OFEventController.getInstance(this);
        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("announcement_id", id);
        mapValue.put("channel", "in-app");
        mapValue.put("link_text", text);
        mapValue.put("link_url", url);
        ec.storeEventsInDB(OFConstants.ANN_CLICKED, mapValue, 0);
    }

    private void viewedAnnouncement(String id){
        OFEventController ec = OFEventController.getInstance(this);
        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("announcement_id", id);
        mapValue.put("channel", "in-app");
        ec.storeEventsInDB(OFConstants.ANN_VIEWED, mapValue, 0);
    }

    public void handleSeen(String id){
        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(this);

        ArrayList<String> inAppIdList;
        inAppIdList = shp.getSeenInAppAnnounceList();

        inAppIdList.add(id);

        shp.setSeenInAppAnnounceList(inAppIdList);
    }
}