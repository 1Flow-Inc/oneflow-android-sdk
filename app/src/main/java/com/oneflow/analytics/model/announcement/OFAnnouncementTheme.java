package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFAnnouncementTheme extends OFBaseModel {

    @SerializedName("brandColor")
    @Expose
    private String brandColor;
    @SerializedName("brandOpacity")
    @Expose
    private String brandOpacity;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("backgroundOpacity")
    @Expose
    private String backgroundOpacity;
    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("textOpacity")
    @Expose
    private String textOpacity;

    public String getBrandColor() {
        return brandColor;
    }

    public void setBrandColor(String brandColor) {
        this.brandColor = brandColor;
    }

    public String getBrandOpacity() {
        return brandOpacity;
    }

    public void setBrandOpacity(String brandOpacity) {
        this.brandOpacity = brandOpacity;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundOpacity() {
        return backgroundOpacity;
    }

    public void setBackgroundOpacity(String backgroundOpacity) {
        this.backgroundOpacity = backgroundOpacity;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextOpacity() {
        return textOpacity;
    }

    public void setTextOpacity(String textOpacity) {
        this.textOpacity = textOpacity;
    }
}
