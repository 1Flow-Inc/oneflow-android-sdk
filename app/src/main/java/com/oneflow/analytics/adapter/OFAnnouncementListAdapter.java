package com.oneflow.analytics.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.oneflow.analytics.R;
import com.oneflow.analytics.customwidgets.OFCustomTextView;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.utils.OFHelper;

import java.util.ArrayList;

public class OFAnnouncementListAdapter extends RecyclerView.Adapter<OFAnnouncementListAdapter.MyViewHolder> {
    private ArrayList<OFGetSurveyListResponse> itemsList;
    private View.OnClickListener gch;

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }

    }

    public OFAnnouncementListAdapter() {
//        OFHelper.v(this.getClass().getName(), "OneFlow size[" + itemsList.size() + "]");
    }

    public void notifyMyList(ArrayList<OFGetSurveyListResponse> arrayList) {
        this.itemsList = arrayList;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_list_single_item, parent, false);


        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {

        return 5;
    }
}