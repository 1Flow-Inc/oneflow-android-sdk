package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementSettings extends OFBaseModel {

    @SerializedName("audience_rule")
    @Expose
    private OFAnnouncementRule audienceRule;

    public OFAnnouncementRule getAudienceRule() {
        return audienceRule;
    }

    public void setAudienceRule(OFAnnouncementRule audienceRule) {
        this.audienceRule = audienceRule;
    }

}
