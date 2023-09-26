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

package com.oneflow.analytics.sdkdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.oneflow.analytics.model.adduser.OFAddUserResponse;
import com.oneflow.analytics.model.loguser.OFLogUserRequest;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.model.survey.OFThrottlingConfig;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class OFOneFlowSHP {
    String keyName = "one_flow_temp.db";
    SharedPreferences pref;
    Gson gson;
    private static OFOneFlowSHP shp = null;

    public static OFOneFlowSHP getInstance(Context context) {

        if (shp == null) {
            synchronized (OFOneFlowSHP.class) {
                shp = new OFOneFlowSHP(context);
            }
        }
        return shp;


    }

    private OFOneFlowSHP(Context context) {

        pref = context.getSharedPreferences(keyName, 0); // 0 - for private mode

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        gson = builder.setPrettyPrinting().create();
    }

    public void storeValue(String key, Object value) {
        SharedPreferences.Editor editor = pref.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (int) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }


    public OFAddUserResponse getUserDetails() {
        String json = pref.getString(OFConstants.USERDETAILSHP, null);
        OFHelper.v("json", "[" + json + "]");
        return gson.fromJson(json, OFAddUserResponse.class);
    }

    public void setUserDetails(OFAddUserResponse arr) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        String json = gson.toJson(arr);
        OFHelper.v("json", "[" + json + "]");
        prefsEditor.putString(OFConstants.USERDETAILSHP, json);
        prefsEditor.apply();
    }

    public OFLogUserRequest getLogUserRequest() {
        String json = pref.getString(OFConstants.LOGUSERREQUESTSHP, null);
        OFHelper.v("json", "[" + json + "]");
        OFLogUserRequest obj;
        if (json != null) {
            obj = gson.fromJson(json, OFLogUserRequest.class);
        } else {
            return null;
        }
        return obj;
    }

    public void setLogUserRequest(OFLogUserRequest arr) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        String json = gson.toJson(arr);
        OFHelper.v("json", "[" + json + "]");
        prefsEditor.putString(OFConstants.LOGUSERREQUESTSHP, json);
        prefsEditor.apply();
    }

    public OFThrottlingConfig getThrottlingConfig() {
        String json = pref.getString(OFConstants.SHP_THROTTLING_KEY, null);
        OFHelper.v("json", "[" + json + "]");
        OFThrottlingConfig obj;
        if (json != null) {
            obj = gson.fromJson(json, OFThrottlingConfig.class);
        } else {
            return null;
        }
        return obj;
    }

    public void setThrottlingConfig(OFThrottlingConfig arr) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        String json = gson.toJson(arr);
        OFHelper.v("json", "[" + json + "]");
        prefsEditor.putString(OFConstants.SHP_THROTTLING_KEY, json);
        prefsEditor.apply();
    }

    public void clearLogUserRequest() {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.remove(OFConstants.LOGUSERREQUESTSHP).apply();
    }


    public void setSurveyList(ArrayList<OFGetSurveyListResponse> list) {
        SharedPreferences.Editor editor = pref.edit();
        String json = gson.toJson(list);
        editor.putString(OFConstants.SURVEYLISTSHP, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<OFGetSurveyListResponse> getSurveyList() {
        String json = pref.getString(OFConstants.SURVEYLISTSHP, null);
        Type type = new TypeToken<ArrayList<OFGetSurveyListResponse>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void setClosedSurveyList(ArrayList<String> list) {
        SharedPreferences.Editor editor = pref.edit();
        String json = gson.toJson(list);
        editor.putString(OFConstants.SURVEYCLOSEDLISTSHP, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<String> getClosedSurveyList() {
        String json = pref.getString(OFConstants.SURVEYCLOSEDLISTSHP, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String getStringValue(String key) {
        return pref.getString(key, "NA");
    }

    public long getLongValue(String key) {
        return pref.getLong(key, 0);
    }

    public int getIntegerValue(String key) {
        return pref.getInt(key, 0);
    }

    public float getFloatValue(String key) {
        return pref.getFloat(key, 0f);
    }

    public boolean getBooleanValue(String key, Boolean defaultValue) {
        return pref.getBoolean(key, defaultValue);
    }


}
