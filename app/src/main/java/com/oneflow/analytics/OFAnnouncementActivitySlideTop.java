package com.oneflow.analytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.oneflow.analytics.databinding.ActivityOfannouncementSlideTopBinding;
import com.oneflow.analytics.fragment.OFAnnouncementFragmentModel;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;

public class OFAnnouncementActivitySlideTop extends OFAnnouncementBaseActivity {

    ActivityOfannouncementSlideTopBinding binding;

    String tag = this.getClass().getName();

    ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses;

    Window window;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityOfannouncementSlideTopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getAnnouncementDetailResponses = (ArrayList<OFGetAnnouncementDetailResponse>) getIntent().getSerializableExtra("announcementData");

        setTheme(binding.viewLayout, binding.closeBtnImageView);

        binding.closeBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.bottomWaterMark.setOnClickListener(v -> {
            String waterMark1 = "https://1flow.app/?utm_source=1flow-android-sdk&utm_medium=watermark&utm_campaign=real-time+feedback+powered+by+1flow";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(waterMark1));
            startActivity(browserIntent);
        });

        loadFragments();

        window = this.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        OFHelper.v(tag, "OneFlow Window size width[" + window.getAttributes().width + "]height[" + window.getAttributes().height + "]");

        wlp.gravity = Gravity.TOP;
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        window.setAttributes(wlp);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing,R.anim.slide_exit_upward);
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
            frag = OFAnnouncementFragmentModel.newInstance(getAnnouncementDetailResponses, null, "");
        } catch (Exception ex) {

            //  error
        }
        return frag;
    }
}