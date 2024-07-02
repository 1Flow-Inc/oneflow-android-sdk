package com.oneflow.analytics;

import android.content.Context;
import android.text.TextUtils;

import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneflow.analytics.controller.OFAnnouncementController;
import com.oneflow.analytics.model.announcement.OFAnnouncementIndex;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OFAnnouncementLander {

    Context context;
    String triggerEventName;
    String eventData;
    String tag = this.getClass().getName();
    JSONArray eventMapArray;

    ArrayList<OFAnnouncementIndex> filteredList;

    public OFAnnouncementLander(Context context) {
        this.context = context;
    }

    public void setData(String eventData,String triggerEventName, ArrayList<OFAnnouncementIndex> filteredList){
        this.eventData = eventData;
        this.triggerEventName = triggerEventName;
        this.filteredList = filteredList;

        OFHelper.v(tag, "1Flow v8 announcement called [" + eventData + "]");
        try {
            eventMapArray = new JSONArray(eventData);
            OFHelper.v(tag, "1Flow v8 announcement called [" + eventMapArray.get(0) + "]");

        } catch (JSONException je) {
            // error
        }

        try {
            handleV8(eventMapArray.get(0).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleV8(String eventData){
        StringBuilder jsCode = new StringBuilder();

        jsCode = getFileContents1(context.getCacheDir().getPath() + File.separator + OFConstants.ANN_FILE_NAME);

        if (jsCode != null) {
            OFHelper.v(tag, "1Flow v8 announcement [" + jsCode.length() + "]");
        }
        if (jsCode == null) {
            jsCode = getFileContentsFromLocal1(OFConstants.ANN_FILE_NAME);
        }

        if (jsCode != null) {

            StringBuilder jsFunction = new StringBuilder();
            try {
                jsFunction = new StringBuilder("oneflowAnnouncementFilter(" + new Gson().toJson(filteredList) + "," + eventData + ");");

            } catch (Exception ex) {
                OFHelper.e(tag, "1Flow error[" + ex.getMessage() + "]");
            }

            StringBuilder jsCallerMethod = new StringBuilder("function oneFlowAnnouncementCallBack(announcement){ console.log(\"reached at callback method\"); onResultReceived(JSON.stringify(announcement));}");
            StringBuilder finalCode = new StringBuilder(jsCode.toString() + "\n\n" + jsFunction.toString() + "\n\n" + jsCallerMethod.toString());

            try {

                // Create a V8 runtime instance
                V8 runtime = V8.createV8Runtime();

                runtime.registerJavaMethod(new JavaVoidCallback() {
                    public void invoke(V8Object receiver, V8Array parameters) {
                        // Handle callback from JavaScript
                        if (parameters.length() > 0 && !parameters.isUndefined()) {
                            String jsonString = parameters.get(0).toString();
                            handleDataFromJS(jsonString);
                        }
                    }
                }, "onResultReceived");

                runtime.executeVoidScript(finalCode.toString());

                // Release the V8 runtime
                runtime.release(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleDataFromJS(String resultJson){
        OFHelper.v(tag, "1Flow JavaScript returns1: [" + resultJson + "][" + eventMapArray.length() + "]");
        if (!resultJson.equalsIgnoreCase("null")) {
            if (!OFHelper.validateString(resultJson).equalsIgnoreCase("NA")) {
                if (!resultJson.equalsIgnoreCase("undefined")) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<OFAnnouncementIndex>>() {
                    }.getType();
                    ArrayList<OFAnnouncementIndex> result = gson.fromJson(resultJson,type);
                    handleResult(result);
                }
            }
        }
    }

    private void handleResult(ArrayList<OFAnnouncementIndex> result) {
        // Do something with the result
        OFHelper.v(tag, "1Flow JavaScript returns2: " + result.toString());
        if (result != null) {
            launchAnnouncement(result);
        } else {
            OFHelper.v(tag, "1Flow event flow check failed no announcement launch");
        }
    }

    public void launchAnnouncement(ArrayList<OFAnnouncementIndex> announcementToInit) {
        OFHelper.v(tag, "1Flow eventName[" + triggerEventName + "]Announcement[" + new Gson().toJson(announcementToInit) + "]");

        List<String> idArray = new ArrayList<>();
        for (int i = 0; i < announcementToInit.size(); i++) {
            idArray.add(announcementToInit.get(i).getId());
        }

        OFHelper.v(tag, "1Flow ids[" + idArray + "]");
        OFAnnouncementController.getInstance(context).getAnnouncementDetailFromAPI(TextUtils.join(",", idArray),"");
    }

    private StringBuilder getFileContents1(String fileName) {
        try {

            byte[] buffer;
            try (FileInputStream is = new FileInputStream(fileName)) {

                int size = is.available();
                buffer = new byte[size];
                if(is.read(buffer) > 0){
                    // read
                }
            }
            return new StringBuilder(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }
    }

    private StringBuilder getFileContentsFromLocal1(String fileName) {
        try {
            byte[] buffer;
            try (InputStream is = context.getAssets().open(fileName)) {

                int size = is.available();
                buffer = new byte[size];
                if (is.read(buffer) > 0){
                    // read
                }
            }
            return new StringBuilder(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }
    }
}
