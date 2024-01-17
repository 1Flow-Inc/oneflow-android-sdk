package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementIndex extends OFBaseModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("seen")
    @Expose
    private Boolean seen;
    @SerializedName("inbox")
    @Expose
    private OFAnnouncementSubIndex inbox;
    @SerializedName("in_app")
    @Expose
    private OFAnnouncementSubIndex inApp;
    @SerializedName("settings")
    @Expose
    private OFAnnouncementSettings settings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public OFAnnouncementSubIndex getInbox() {
        return inbox;
    }

    public void setInbox(OFAnnouncementSubIndex inbox) {
        this.inbox = inbox;
    }

    public OFAnnouncementSubIndex getInApp() {
        return inApp;
    }

    public void setInApp(OFAnnouncementSubIndex inApp) {
        this.inApp = inApp;
    }

    public OFAnnouncementSettings getSettings() {
        return settings;
    }

    public void setSettings(OFAnnouncementSettings settings) {
        this.settings = settings;
    }
}
