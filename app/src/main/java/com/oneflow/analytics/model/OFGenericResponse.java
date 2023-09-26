/*
 *  Copyright 2021 1Flow, Inc.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.oneflow.analytics.model;

import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.survey.OFThrottlingConfig;

import java.io.Serializable;

public class OFGenericResponse<T> implements Serializable {// extends BaseResponse{


    @SerializedName("result")
    private T result ;

    @SerializedName("success")
    private int success;

    @SerializedName("message")
    private String message = "Something went wrong";

    @SerializedName("throttlingMobileSDKConfig")
    OFThrottlingConfig throttlingConfig;

    public OFThrottlingConfig getThrottlingConfig() {
        return throttlingConfig;
    }

    public void setThrottlingConfig(OFThrottlingConfig throttlingConfig) {
        this.throttlingConfig = throttlingConfig;
    }

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

    public T getResult() {
        if(result == null){
            return (T)new String("fake");
        }
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
