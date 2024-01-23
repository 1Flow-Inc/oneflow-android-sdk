package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

public class OFGetAnnouncementDetailResponse extends OFBaseModel {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("category")
    @Expose
    private OFAnnouncementDetailCategory category;
    @SerializedName("publishedAt")
    @Expose
    private Long publishedAt;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("action")
    @Expose
    private OFGetAnnouncementDetailAction action;

    public boolean isSeen = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OFAnnouncementDetailCategory getCategory() {
        return category;
    }

    public void setCategory(OFAnnouncementDetailCategory category) {
        this.category = category;
    }

    public Long getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Long publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OFGetAnnouncementDetailAction getAction() {
        return action;
    }

    public void setAction(OFGetAnnouncementDetailAction action) {
        this.action = action;
    }

}
