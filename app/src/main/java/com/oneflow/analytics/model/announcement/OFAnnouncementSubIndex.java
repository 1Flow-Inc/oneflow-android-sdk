package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementSubIndex extends OFBaseModel {

    @SerializedName("is_active")
    @Expose
    private boolean isActive;
    @SerializedName("style")
    @Expose
    private String style;
    @SerializedName("schedule")
    @Expose
    private Object schedule;
    @SerializedName("timing")
    @Expose
    private OFAnnouncementTiming timing;
    @SerializedName("ios")
    @Expose
    private boolean ios;
    @SerializedName("android")
    @Expose
    private boolean android;
    @SerializedName("web")
    @Expose
    private boolean web;
    @SerializedName("localization")
    @Expose
    private String localization;
    @SerializedName("banner_url")
    @Expose
    private String bannerUrl;

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Object getSchedule() {
        return schedule;
    }

    public void setSchedule(Object schedule) {
        this.schedule = schedule;
    }

    public OFAnnouncementTiming getTiming() {
        return timing;
    }

    public void setTiming(OFAnnouncementTiming timing) {
        this.timing = timing;
    }

    public boolean getIos() {
        return ios;
    }

    public void setIos(boolean ios) {
        this.ios = ios;
    }

    public boolean getAndroid() {
        return android;
    }

    public void setAndroid(boolean android) {
        this.android = android;
    }

    public boolean getWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

}
