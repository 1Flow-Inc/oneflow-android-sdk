package com.oneflow.analytics;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneflow.analytics.controller.OFAnnouncementController;
import com.oneflow.analytics.controller.OFSurveyController;
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

public class OFAnnouncementLanderService extends Service {

    WebView wv;
    String triggerEventName;
    String eventData;
    String tag = this.getClass().getName();
    JSONArray eventMapArray;
    int counter = 0;

    ArrayList<OFAnnouncementIndex> filteredList;

    @Override
    public void onCreate() {
        super.onCreate();
//        OFHelper.e(tag,"1Flow onCreate");
    }

    private void intiData(Intent intent) {
        if(intent == null){
            stopService();
            return;
        }
        eventData = intent.getStringExtra("eventData");

        if(eventData == null){
            stopService();
            return;
        }

        OFHelper.v(tag, "1Flow webmethod called [" + eventData + "]");
        try {
            eventMapArray = new JSONArray(eventData);
            OFHelper.v(tag, "1Flow webmethod called [" + eventMapArray.get(0) + "]");

        } catch (JSONException je) {
            // error
        }

        filteredList = (ArrayList<OFAnnouncementIndex>) intent.getSerializableExtra("listData");

        OFHelper.v(tag, "1Flow AnnouncementLander [" + filteredList.size() + "]");

        try {
            checkWebviewFunction(eventMapArray.get(0).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        OFHelper.e(tag,"1Flow onDestroy");
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        OFHelper.e(tag,"1Flow onStartCommand");
        intiData(intent);
        return START_NOT_STICKY;
    }

    private void stopService(){
        OFSurveyController.getInstance(this).getEventForStartUp();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        stopSelf();
    }

    private void stopServiceForOpenedAnnouncement(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        stopSelf();
    }

    private void checkWebviewFunction(String eventData) {
        OFHelper.v(tag, "1Flow webmethod called 0 [" + eventData + "]");

        try {
            wv = new WebView(this);
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

                if(wv == null){
                    stopService();
                    return;
                }
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
                            stopService();

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
                stopService();
            }
        } catch (Exception e) {
            // Handle the exception, possibly by showing an error message to the user
            stopService();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String data = msg.getData().getString("data");
            OFHelper.v(tag, "1Flow handler data[" + data + "]");
            checkWebviewFunction(data);
        }
    };

    public class MyJavaScriptInterface {


        public MyJavaScriptInterface() {
            // Constructor
        }

        @JavascriptInterface
        public void onResultReceived(String resultJson) {


            OFHelper.v(tag, "1Flow JavaScript returns1: [" + resultJson + "]");
            if (resultJson.equalsIgnoreCase("null")) {
                if (eventMapArray.length() > 1) {
                    OFHelper.v(tag, "1Flow JavaScript returns1: [" + counter + "]");
                    if ((++counter) < eventMapArray.length()) {

                        try {
                            wv = null;
                            OFHelper.v(tag, "1Flow JavaScript returns1: [" + counter + "][" + eventMapArray.get(counter).toString() + "]");

                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("data", eventMapArray.get(counter).toString());
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        } catch (JSONException je) {
                            stopService();
                        }
                    } else {
                        stopService();
                    }
                } else {
                    stopService();
                }
            } else {
                if (!OFHelper.validateString(resultJson).equalsIgnoreCase("NA")) {
                    if (!resultJson.equalsIgnoreCase("undefined")) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<OFAnnouncementIndex>>() {
                        }.getType();
                        ArrayList<OFAnnouncementIndex> result = gson.fromJson(resultJson,type);
                        handleResult(result);
                    } else {
                        stopService();
                    }
                } else {
                    stopService();
                }
            }

        }

        private void handleResult(ArrayList<OFAnnouncementIndex> result) {
            // Do something with the result
            OFHelper.v(tag, "1Flow JavaScript returns2: " + result.toString());
            if (result != null) {
                launchAnnouncement(result);
            } else {
                stopService();
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
        OFAnnouncementController.getInstance(this).getAnnouncementDetailFromAPI(TextUtils.join(",", idArray),"");
        stopServiceForOpenedAnnouncement();
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

}
