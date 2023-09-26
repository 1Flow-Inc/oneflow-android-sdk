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

package com.oneflow.analytics.model.loguser;

import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.ArrayList;

public class OFLogUserResponse extends OFBaseModel {

    @SerializedName("analytic_user_id")
    private String analytic_user_id;
    @SerializedName("created_on")
    private String created_on;
    @SerializedName("devices")
    private ArrayList<OFLogDevice> devices;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("locations")
    private ArrayList<OFLogResponseLocation> locations;
    @SerializedName("parameters")
    private OFLogParameter parameters;
    @SerializedName("session_id")
    private String sessionId;
    @SerializedName("system_id")
    private String system_id;
    @SerializedName("updated_on")
    private String updated_on;

    public String getAnalytic_user_id() {
        return analytic_user_id;
    }

    public void setAnalytic_user_id(String analytic_user_id) {
        this.analytic_user_id = analytic_user_id;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public ArrayList<OFLogDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<OFLogDevice> devices) {
        this.devices = devices;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public ArrayList<OFLogResponseLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<OFLogResponseLocation> locations) {
        this.locations = locations;
    }

    public OFLogParameter getParameters() {
        return parameters;
    }

    public void setParameters(OFLogParameter parameters) {
        this.parameters = parameters;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSystem_id() {
        return system_id;
    }

    public void setSystem_id(String system_id) {
        this.system_id = system_id;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }
}
