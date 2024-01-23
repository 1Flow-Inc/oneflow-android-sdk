package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.List;

public class OFAnnouncementTimingOption extends OFBaseModel {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private long value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
