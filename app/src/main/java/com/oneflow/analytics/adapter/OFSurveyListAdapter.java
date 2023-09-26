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
import java.util.List;

/**
 * Created by tp00026816 on 3/5/2019.
 */

public class OFSurveyListAdapter extends RecyclerView.Adapter<OFSurveyListAdapter.MyViewHolder> {
    private List<OFGetSurveyListResponse> itemsList;
    private View.OnClickListener gch;

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String triggerName = itemsList.get(position).getTrigger_event_name();
        if(OFHelper.validateString(triggerName).equalsIgnoreCase("NA")) {
            OFHelper.v(this.getClass().getName(), "OneFlow adapter postion[" + new Gson().toJson(itemsList.get(position).getSurveySettings().getTriggerFilters()) + "]");
            if(itemsList.get(position).getSurveySettings().getTriggerFilters()!=null && !itemsList.get(position).getSurveySettings().getTriggerFilters().isEmpty()) {
                triggerName = itemsList.get(position).getSurveySettings().getTriggerFilters().get(0).getField();
            }
        }
        holder.txtSurveyKey.setText(triggerName + " (" + itemsList.get(position).getName() + ")");
        holder.txtSurveyKey.setTag(itemsList.get(position).get_id());
        holder.txtSurveyKey.setOnClickListener(gch);

        if (itemsList.get(position).getScreens() != null) {
            holder.txtSurveyData.setText("" + (itemsList.get(position).getScreens().size()));
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        OFCustomTextView txtSurveyKey;
        OFCustomTextView txtSurveyData;

        public MyViewHolder(View view) {
            super(view);
            txtSurveyKey = view.findViewById(R.id.survey_trigger_name);
            txtSurveyData = view.findViewById(R.id.survey_trigger_data);
        }

    }

    public OFSurveyListAdapter(List<OFGetSurveyListResponse> arrayList, View.OnClickListener gch) {
        this.itemsList = arrayList;
        this.gch = gch;
        OFHelper.v(this.getClass().getName(), "OneFlow size[" + itemsList.size() + "]");
    }

    public void notifyMyList(List<OFGetSurveyListResponse> arrayList) {
        this.itemsList = arrayList;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.survey_list_single_item, parent, false);


        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {

        return itemsList.size();
    }
}