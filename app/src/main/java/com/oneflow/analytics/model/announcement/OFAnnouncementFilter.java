package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementFilter extends OFBaseModel {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("timingOption")
    @Expose
    private OFAnnouncementTimingOption timingOption;
    @SerializedName("property_filters")
    @Expose
    private OFAnnouncementPropertyFilters propertyFilters;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public OFAnnouncementTimingOption getTimingOption() {
        return timingOption;
    }

    public void setTimingOption(OFAnnouncementTimingOption timingOption) {
        this.timingOption = timingOption;
    }

    public OFAnnouncementPropertyFilters getPropertyFilters() {
        return propertyFilters;
    }

    public void setPropertyFilters(OFAnnouncementPropertyFilters propertyFilters) {
        this.propertyFilters = propertyFilters;
    }

}
