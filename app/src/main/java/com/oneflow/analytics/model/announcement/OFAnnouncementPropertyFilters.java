package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.ArrayList;
import java.util.List;

public class OFAnnouncementPropertyFilters extends OFBaseModel {

    @SerializedName("operator")
    @Expose
    private String operator;
    @SerializedName("filters")
    @Expose
    private ArrayList<OFAnnouncementPropertySubFilters> filters;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public ArrayList<OFAnnouncementPropertySubFilters> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<OFAnnouncementPropertySubFilters> filters) {
        this.filters = filters;
    }

}
