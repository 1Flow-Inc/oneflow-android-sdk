package com.oneflow.analytics.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oneflow.analytics.R;
import com.oneflow.analytics.adapter.OFAnnouncementListAdapter;
import com.oneflow.analytics.adapter.OFSurveyOptionsAdapter;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.utils.OFHelper;

public class OFAnnouncementFragment extends BaseFragment {

    String tag = this.getClass().getName();
    OFAnnouncementListAdapter announcementListAdapter;
    RecyclerView announcementRecyclerView;

    public static OFAnnouncementFragment newInstance(OFSurveyScreens ahdList, OFSDKSettingsTheme sdkTheme, String themeColor) {
        OFAnnouncementFragment myFragment = new OFAnnouncementFragment();

        Bundle args = new Bundle();
        args.putSerializable("data", ahdList);
        args.putSerializable("theme", sdkTheme);
        args.putString("themeColor", themeColor);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        OFHelper.v(tag, "1Flow OnResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        announcementRecyclerView = (RecyclerView) view.findViewById(R.id.announcement_list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        announcementListAdapter = new OFAnnouncementListAdapter();

        announcementRecyclerView.setLayoutManager(mLayoutManager);
        announcementRecyclerView.setItemAnimator(new DefaultItemAnimator());
        announcementRecyclerView.setAdapter(announcementListAdapter);

        return view;
    }
}