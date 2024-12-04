package com.oneflow.analytics.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableRow;

import com.google.gson.Gson;
import com.oneflow.analytics.OFAnnouncementActivityFullScreen;
import com.oneflow.analytics.OFAnnouncementActivityModel;
import com.oneflow.analytics.R;
import com.oneflow.analytics.adapter.OFAnnouncementListAdapter;
import com.oneflow.analytics.adapter.OFSurveyOptionsAdapter;
import com.oneflow.analytics.customwidgets.OFCustomTextViewBold;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementResponse;
import com.oneflow.analytics.model.survey.OFSDKSettingsTheme;
import com.oneflow.analytics.model.survey.OFSurveyScreens;
import com.oneflow.analytics.repositories.OFAnnouncementRepo;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import java.util.ArrayList;

public class OFAnnouncementFragment extends Fragment implements OFMyResponseHandlerOneFlow {

    String tag = this.getClass().getName();
    OFAnnouncementListAdapter announcementListAdapter;
    RecyclerView announcementRecyclerView;
    ProgressBar progressBar;

    OFCustomTextViewBold tvEmpty;

    Context mContext;

    ArrayList<String> idArray;

    public static OFAnnouncementFragment newInstance(ArrayList<String> idArray, OFSDKSettingsTheme sdkTheme, String themeColor) {
        OFAnnouncementFragment myFragment = new OFAnnouncementFragment();

        Bundle args = new Bundle();
        args.putSerializable("data", idArray);
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

        mContext = getActivity();

        announcementRecyclerView = (RecyclerView) view.findViewById(R.id.announcement_list);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_circular);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        idArray = (ArrayList<String>) getArguments().getSerializable("data");

        if(idArray != null && !idArray.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);
            OFOneFlowSHP shp = OFOneFlowSHP.getInstance(getActivity());
            OFAnnouncementRepo.getAnnouncementDetail(shp.getStringValue(OFConstants.APPIDSHP), this, OFConstants.ApiHitType.fetchAnnouncementDetailFromAPI, TextUtils.join(",", idArray),"1");
        }else{
            tvEmpty.setVisibility(View.VISIBLE);
        }

//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        announcementListAdapter = new OFAnnouncementListAdapter(getActivity(),getAnnouncementDetailResponses);
//
//        announcementRecyclerView.setLayoutManager(mLayoutManager);
//        announcementRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        announcementRecyclerView.setAdapter(announcementListAdapter);

        return view;
    }

    public void finishActivity(){
        if(mContext instanceof OFAnnouncementActivityFullScreen){
            ((OFAnnouncementActivityFullScreen) mContext).finish();
        }
    }

    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj, Long reserve, String reserved, Object obj2, Object obj3) {
        switch (hitType) {
            case fetchAnnouncementDetailFromAPI:
                OFHelper.v("AnnouncementController", "OneFlow announcement detail received [" + reserved + "]");
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    if(reserved.equals("1")){
                        ArrayList<OFGetAnnouncementDetailResponse> getAnnouncementDetailResponses = (ArrayList<OFGetAnnouncementDetailResponse>) obj;
                        if(!getAnnouncementDetailResponses.isEmpty()){

                            OFOneFlowSHP shp = OFOneFlowSHP.getInstance(getActivity());

                            ArrayList<String> inboxIdList;
                            inboxIdList = shp.getSeenInboxAnnounceList();

                            OFHelper.v("OFAnnouncementController", inboxIdList + "");
                            for (int i = 0; i < getAnnouncementDetailResponses.size(); i++) {
                                if(inboxIdList.contains(getAnnouncementDetailResponses.get(i).getId())){
                                    getAnnouncementDetailResponses.get(i).isSeen = true;
                                }
                            }


                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            announcementListAdapter = new OFAnnouncementListAdapter(getActivity(),getAnnouncementDetailResponses);
                            announcementListAdapter.setFragment(this);

                            announcementRecyclerView.setLayoutManager(mLayoutManager);
                            announcementRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            announcementRecyclerView.setAdapter(announcementListAdapter);
                        }else{
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }
}