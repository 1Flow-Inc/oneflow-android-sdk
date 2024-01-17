package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.ArrayList;

public class OFGetAnnouncementResponseDetail extends OFBaseModel {

    @SerializedName("result")
    @Expose
    private ArrayList<OFGetAnnouncementDetailResponse> result;

    public ArrayList<OFGetAnnouncementDetailResponse> getResult() {
        return result;
    }

    public void setResult(ArrayList<OFGetAnnouncementDetailResponse> result) {
        this.result = result;
    }
}
