package com.oneflow.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneflow.analytics.controller.OFAnnouncementController;
import com.oneflow.analytics.controller.OFEventController;
import com.oneflow.analytics.model.announcement.OFAnnouncementIndex;
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse;
import com.oneflow.analytics.model.survey.OFSurveyUserInput;
import com.oneflow.analytics.model.survey.OFThrottlingConfig;
import com.oneflow.analytics.repositories.OFLogUserDBRepoKT;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFDelayedSurveyCountdownTimer;
import com.oneflow.analytics.utils.OFFilterSurveys;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OFAnnouncementLanderActivity extends AppCompatActivity {
    WebView wv;
    String triggerEventName;
    String eventData;
    String tag = this.getClass().getName();
    JSONArray eventMapArray;
    int counter = 0;

    ArrayList<OFAnnouncementIndex> filteredList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        eventData = this.getIntent().getStringExtra("eventData");

        filteredList = (ArrayList<OFAnnouncementIndex>) getIntent().getSerializableExtra("listData");

        OFHelper.v(tag, "1Flow AnnouncementLander [" + filteredList.size() + "]");

//        setUpHashForActivity();
        checkWebviewFunction(eventData);

    }


    private void checkWebviewFunction(String eventData) {
        OFHelper.v(tag, "1Flow webmethod called 0 [" + eventData + "]");

        wv = new WebView(OFAnnouncementLanderActivity.this);
        setContentView(wv);
        StringBuilder jsCode = new StringBuilder();
        OFHelper.v(tag, "1Flow webmethod 11[" + jsCode.length() + "]");



        jsCode = getFileContents1(getCacheDir().getPath() + File.separator + OFConstants.ANN_FILE_NAME);

        if (jsCode != null) {
            OFHelper.v(tag, "1Flow webmethod 12[" + jsCode.length() + "]");
        }
        if (jsCode == null) {
            jsCode = getFileContentsFromLocal1(OFConstants.ANN_FILE_NAME);
        }

        if (jsCode != null) {
            StringBuilder jsFunction = new StringBuilder();
            try {
                jsFunction = new StringBuilder("oneflowAnnouncementFilter(" + new Gson().toJson(filteredList) + "," + eventData + ")");

            } catch (Exception ex) {
                OFHelper.e(tag, "1Flow error[" + ex.getMessage() + "]");
            }

            StringBuilder jsCallerMethod = new StringBuilder("function oneFlowAnnouncementCallBack(announcement){ console.log(\"reached at callback method\"); android.onResultReceived(JSON.stringify(announcement));}");
            StringBuilder finalCode = new StringBuilder(jsCode.toString() + "\n\n" + jsFunction.toString() + "\n\n" + jsCallerMethod.toString());

            OFHelper.v(tag, "1Flow webmethod 14[" + finalCode.length() + "]");

            wv.clearCache(true);
            wv.clearHistory();
            wv.getSettings().setJavaScriptEnabled(true);
            wv.addJavascriptInterface(new MyJavaScriptInterface(), "android");
            wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            wv.getSettings().setDomStorageEnabled(false);
            wv.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                        OFHelper.e(tag, "1Flow webpage JS Error[" + consoleMessage.message() + "]");
                        OFAnnouncementLanderActivity.this.finish();

                    } else {
                        OFHelper.v(tag, "1Flow webpage JS log[" + consoleMessage.message() + "]");
                    }

                    return true;

                }
            });

            wv.evaluateJavascript(finalCode.toString(), result -> {
                // onReceiveValue
            });

        } else {
            OFAnnouncementLanderActivity.this.finish();
        }
    }

    public class MyJavaScriptInterface {


        public MyJavaScriptInterface() {
            // Constructor
        }

        @JavascriptInterface
        public void onResultReceived(String resultJson) {


            OFHelper.v(tag, "1Flow JavaScript returns1: [" + resultJson + "]");
            if (resultJson.equalsIgnoreCase("null")) {
                OFAnnouncementLanderActivity.this.finish();
            } else {
                if (!OFHelper.validateString(resultJson).equalsIgnoreCase("NA")) {
                    if (!resultJson.equalsIgnoreCase("undefined")) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<OFAnnouncementIndex>>() {
                        }.getType();
                        ArrayList<OFAnnouncementIndex> result = gson.fromJson(resultJson,type);
                        handleResult(result);
                    } else {
                        OFAnnouncementLanderActivity.this.finish();
                    }
                } else {
                    OFAnnouncementLanderActivity.this.finish();
                }
            }

        }

        private void handleResult(ArrayList<OFAnnouncementIndex> result) {
            // Do something with the result
            OFHelper.v(tag, "1Flow JavaScript returns2: " + result.toString());
            if (result != null) {
                launchAnnouncement(result);
            } else {
                OFAnnouncementLanderActivity.this.finish();
                OFHelper.v(tag, "1Flow event flow check failed no survey launch");
            }
        }


    }

    public void launchAnnouncement(ArrayList<OFAnnouncementIndex> announcementToInit) {
        OFHelper.v(tag, "1Flow eventName[" + triggerEventName + "]Announcement[" + new Gson().toJson(announcementToInit) + "]");

        List<String> idArray = new ArrayList<>();
        for (int i = 0; i < announcementToInit.size(); i++) {
            idArray.add(announcementToInit.get(i).getId());
        }

        OFHelper.v(tag, "1Flow ids[" + idArray + "]");
        OFAnnouncementController.getInstance(OFAnnouncementLanderActivity.this).getAnnouncementDetailFromAPI(TextUtils.join(",", idArray),"");
        OFAnnouncementLanderActivity.this.finish();
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
            try (InputStream is = getAssets().open(fileName)) {

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

    HashMap<String, Class> activityName;

    public void setUpHashForActivity() {
        activityName = new HashMap<>();

        activityName.put("top-banner", OFSurveyActivityBannerTop.class);
        activityName.put("bottom-banner", OFSurveyActivityBannerBottom.class);

        activityName.put("fullscreen", OFSurveyActivityFullScreen.class);

        activityName.put("top-left", OFSurveyActivityTop.class);
        activityName.put("top-center", OFSurveyActivityTop.class);
        activityName.put("top-right", OFSurveyActivityTop.class);

        activityName.put("middle-left", OFSurveyActivityCenter.class); //name changed
        activityName.put("middle-center", OFSurveyActivityCenter.class); //name changed
        activityName.put("middle-right", OFSurveyActivityCenter.class); //name changed

        activityName.put("bottom-left", OFSurveyActivityBottom.class);
        activityName.put("bottom-center", OFSurveyActivityBottom.class); //default one
        activityName.put("bottom-right", OFSurveyActivityBottom.class);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
