package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.ArrayList;
import java.util.List;

public class OFAnnouncementRule extends OFBaseModel {

    @SerializedName("filters")
    @Expose
    private ArrayList<OFAnnouncementFilter> filters;

    public ArrayList<OFAnnouncementFilter> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<OFAnnouncementFilter> filters) {
        this.filters = filters;
    }

}
