package com.oneflow.tryskysdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResponse {//implements Serializable {
    @SerializedName("success")
    @Expose
    private int success;

    @SerializedName("message")
    @Expose
    private String message;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
