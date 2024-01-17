package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFGetAnnouncementResponse extends OFBaseModel {

    @SerializedName("announcements")
    @Expose
    private OFAnnouncements announcements;

    @SerializedName("theme")
    @Expose
    private OFAnnouncementTheme theme;

    public OFAnnouncements getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(OFAnnouncements announcements) {
        this.announcements = announcements;
    }

    public OFAnnouncementTheme getTheme() {
        return theme;
    }

    public void setTheme(OFAnnouncementTheme theme) {
        this.theme = theme;
    }
}
