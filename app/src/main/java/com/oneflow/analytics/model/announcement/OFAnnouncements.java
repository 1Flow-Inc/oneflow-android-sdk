package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.ArrayList;
import java.util.List;

public class OFAnnouncements extends OFBaseModel {

    @SerializedName("inbox")
    @Expose
    private ArrayList<OFAnnouncementIndex> inbox;

    @SerializedName("inApp")
    @Expose
    private ArrayList<OFAnnouncementIndex> inApp;

    public ArrayList<OFAnnouncementIndex> getInbox() {
        return inbox;
    }

    public void setInbox(ArrayList<OFAnnouncementIndex> inbox) {
        this.inbox = inbox;
    }

    public ArrayList<OFAnnouncementIndex> getInApp() {
        return inApp;
    }

    public void setInApp(ArrayList<OFAnnouncementIndex> inApp) {
        this.inApp = inApp;
    }
}
