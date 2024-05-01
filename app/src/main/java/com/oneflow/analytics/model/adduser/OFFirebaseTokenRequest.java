package com.oneflow.analytics.model.adduser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class OFFirebaseTokenRequest extends OFBaseModel {

    @SerializedName("token")
    private String token;

//    @SerializedName("type")
//    private String type;
//
//    @SerializedName("link")
//    private String link;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getLink() {
//        return link;
//    }
//
//    public void setLink(String link) {
//        this.link = link;
//    }
}
