package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementSettings extends OFBaseModel {

    @SerializedName("audience_rule")
    @Expose
    private OFAnnouncementAudienceRule audienceRule;

    public OFAnnouncementAudienceRule getAudienceRule() {
        return audienceRule;
    }

    public void setAudienceRule(OFAnnouncementAudienceRule audienceRule) {
        this.audienceRule = audienceRule;
    }

}
