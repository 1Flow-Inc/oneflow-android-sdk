package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementTiming extends OFBaseModel {

    @SerializedName("condition")
    @Expose
    private String condition;
    @SerializedName("rule")
    @Expose
    private OFAnnouncementRule rule;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public OFAnnouncementRule getRule() {
        return rule;
    }

    public void setRule(OFAnnouncementRule rule) {
        this.rule = rule;
    }

}
