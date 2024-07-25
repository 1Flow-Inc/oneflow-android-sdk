package com.oneflow.analytics;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.oneflow.analytics.controller.OFEventController;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OFSurveyLanderService extends Service implements OFMyResponseHandlerOneFlow {

    WebView wv;
    String triggerEventName;
    String eventData;
    String tag = this.getClass().getName();
    JSONArray eventMapArray;
    int counter = 0;

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

        OFHelper.v(tag, "1Flow webmethod called [" + eventData + "]");
        try {
            eventMapArray = new JSONArray(eventData);

            if (eventMapArray.length() > 0) {
                JSONObject jsInner = eventMapArray.getJSONObject(counter);
                triggerEventName = jsInner.get("name").toString();
            }

            OFHelper.v(tag, "1Flow webmethod called [" + eventMapArray.length() + "]triggerEventName[" + triggerEventName + "]");
            OFHelper.v(tag, "1Flow webmethod called [" + eventMapArray.get(0) + "]");

        } catch (JSONException je) {
            // error
        }


        new OFFilterSurveys(this, this, OFConstants.ApiHitType.filterSurveys, triggerEventName).start();

        setUpHashForActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        OFHelper.e(tag,"1Flow onDestroy");
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
        return START_STICKY;
    }

    private void stopService(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        stopSelf();
    }

    private void checkWebviewFunction(String eventData) {
        OFHelper.v(tag, "1Flow webmethod called 0 [" + eventData + "]");

        wv = new WebView(this);
        StringBuilder jsCode = new StringBuilder();
        OFHelper.v(tag, "1Flow webmethod 11[" + jsCode.length() + "]");



        jsCode = getFileContents1(getCacheDir().getPath() + File.separator + OFConstants.CACHE_FILE_NAME);

        if (jsCode != null) {
            OFHelper.v(tag, "1Flow webmethod 12[" + jsCode.length() + "]");
        }
        if (jsCode == null) {
            jsCode = getFileContentsFromLocal1(OFConstants.CACHE_FILE_NAME);
        }

        if (jsCode != null) {
            StringBuilder jsFunction = new StringBuilder();
            try {
                jsFunction = new StringBuilder("oneFlowFilterSurvey(" + new Gson().toJson(filteredList) + "," + eventData + ")");

            } catch (Exception ex) {
                OFHelper.e(tag, "1Flow error[" + ex.getMessage() + "]");
            }

            StringBuilder jsCallerMethod = new StringBuilder("function oneFlowCallBack(survey){ console.log(\"reached at callback method\"); android.onResultReceived(JSON.stringify(survey));}");
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
    }

    ArrayList<OFGetSurveyListResponse> filteredList;

    @Override
    public void onResponseReceived(OFConstants.ApiHitType hitType, Object obj1, Long reserve, String reserved, Object obj2, Object obj3) {
        switch (hitType) {
            case lastSubmittedSurvey:
                OFHelper.v(tag, "1Flow globalThrottling received[" + obj1 + "]");

                OFGetSurveyListResponse gslrTH = (OFGetSurveyListResponse) obj2;
                if (obj1 == null) {
                    launchSurvey(gslrTH);
                } else {
                    OFSurveyUserInput ofSurveyUserInput = (OFSurveyUserInput) obj1;
                    OFOneFlowSHP ofs1 = OFOneFlowSHP.getInstance(this);
                    OFThrottlingConfig config = ofs1.getThrottlingConfig();
                    OFHelper.v(tag, "1Flow globalThrottling inside else [" + (ofSurveyUserInput.getCreatedOn() < config.getActivatedAt()) + "]");
                    if (ofSurveyUserInput.getCreatedOn() < config.getActivatedAt()) {
                        launchSurvey(gslrTH);
                    }else{
                        stopService();
                    }
                }

                break;
            case filterSurveys:

                OFHelper.v("1Flow", "1Flow filterSurvey came back [" + obj1 + "]");
                if (obj1 != null) {
                    filteredList = (ArrayList<OFGetSurveyListResponse>) obj1;

                    OFHelper.v("1Flow", "1Flow filterSurvey came back size[" + filteredList.size() + "]");

                    if (!filteredList.isEmpty()) {
                        try {
                            String dataLocal ="";
                            dataLocal = eventMapArray.get(0).toString();

                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("data", dataLocal);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } catch (JSONException je) {
                            // error
                        }
                    } else {
                        stopService();
                    }
                }else {
                    stopService();
                }

                break;
            default:
                break;
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


            OFHelper.v(tag, "1Flow JavaScript returns1: [" + resultJson + "][" + eventMapArray.length() + "]");
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
                        OFGetSurveyListResponse result = gson.fromJson(resultJson, OFGetSurveyListResponse.class);
                        handleResult(result);
                    } else {
                        stopService();
                    }
                } else {
                    stopService();
                }
            }

        }

        private void handleResult(OFGetSurveyListResponse result) {
            // Do something with the result
            OFHelper.v(tag, "1Flow JavaScript returns2: " + result.toString());
            if (result != null) {

                throtlingCheck(result);
            } else {
                stopService();
                OFHelper.v(tag, "1Flow event flow check failed no survey launch");
            }
        }


    }


    Long delayDuration;

    public void throtlingCheck(OFGetSurveyListResponse surveyToInit) {
        OFOneFlowSHP ofs = OFOneFlowSHP.getInstance(this);
        OFThrottlingConfig throttlingConfig = ofs.getThrottlingConfig();
        if (throttlingConfig == null) {

            launchSurvey(surveyToInit);
        } else {

            OFHelper.v(tag, "1Flow globalThrottling[" + surveyToInit.getSurveySettings().getOverrideGlobalThrottling() + "]throttlingConfig isActivated[" + throttlingConfig.isActivated() + "]");

            boolean globalThrottling = surveyToInit.getSurveySettings().getOverrideGlobalThrottling();
            if (globalThrottling) {

                launchSurvey(surveyToInit);
            } else {
                if (throttlingConfig.isActivated()) {
                    if (throttlingConfig.getActivatedById().equalsIgnoreCase(surveyToInit.get_id())) {

                        OFHelper.v(tag, "1Flow globalThrottling id matched ");

                        // check in submitted survey list locally if this survey has been submitted then false
                        new OFLogUserDBRepoKT().findLastSubmittedSurveyID(this, this, OFConstants.ApiHitType.lastSubmittedSurvey, triggerEventName, surveyToInit);

                    }else{
                        stopService();
                    }

                } else {
                    launchSurvey(surveyToInit);
                }
            }

        }
    }

    Intent surveyIntent = null;

    public void launchSurvey(OFGetSurveyListResponse surveyToInit) {
        OFHelper.v(tag, "1Flow eventName[" + triggerEventName + "]surveyId[" + new Gson().toJson(surveyToInit) + "]");

        if (surveyToInit.getSurveySettings().getSdkTheme().getWidgetPosition() == null) {
            surveyIntent = new Intent(getApplicationContext(), activityName.get("bottom-center"));
        } else {
            surveyIntent = new Intent(getApplicationContext(), activityName.get(surveyToInit.getSurveySettings().getSdkTheme().getWidgetPosition()));
        }

        OFOneFlowSHP ofs1 = OFOneFlowSHP.getInstance(this);

        HashMap<String, Object> mapValue = new HashMap<>();
        mapValue.put("flow_id", surveyToInit.get_id());
        OFEventController ec = OFEventController.getInstance(this);
        HashMap<String, Object> mapValue1 = new HashMap<>();
        mapValue1.put("survey_id", surveyToInit.get_id());
        ec.storeEventsInDB(OFConstants.AUTOEVENT_SURVEYIMPRESSION, mapValue1, 0);
        ec.storeEventsInDB(OFConstants.AUTOEVENT_FLOWSTARTED, mapValue, 0);

        ofs1.storeValue(OFConstants.SHP_SURVEY_RUNNING, true);
        ofs1.storeValue(OFConstants.SHP_SURVEYSTART, Calendar.getInstance().getTimeInMillis());


        OFThrottlingConfig config = ofs1.getThrottlingConfig();
        //flow correction and time should be in second
        if (config != null) {

            OFHelper.v(tag, "1Flow throttling config not null");
            config.setActivated(true);
            config.setActivatedById(surveyToInit.get_id());
            config.setActivatedAt(System.currentTimeMillis());

            ofs1.setThrottlingConfig(config);

            setupGlobalTimerToDeactivateThrottlingLocally();
        } else {
            OFHelper.v(tag, "1Flow throttling config null");
        }

        surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        surveyIntent.putExtra("SurveyType", surveyToInit);
        surveyIntent.putExtra("eventName", triggerEventName);

        OFHelper.v(tag, "1Flow activity running 3[" + OFSDKBaseActivity.isActive + "]");

        if (!OFSDKBaseActivity.isActive) {

            if (surveyToInit.getSurveyTimeInterval() != null && surveyToInit.getSurveyTimeInterval().getType().equalsIgnoreCase("show_after")) {

                try {
                    delayDuration = surveyToInit.getSurveyTimeInterval().getValue() * 1000;
                } catch (Exception ex) {
                    // error
                }
                OFHelper.v("1Flow", "1Flow activity waiting duration[" + delayDuration + "]");


                ContextCompat.getMainExecutor(this).execute(() -> {
                    OFDelayedSurveyCountdownTimer delaySurvey = OFDelayedSurveyCountdownTimer.getInstance(this, delayDuration, 1000l, surveyIntent);
                    delaySurvey.start();
                });


            } else {
                startActivity(surveyIntent);
            }

            stopService();
        }
    }


    private void setupGlobalTimerToDeactivateThrottlingLocally() {


        OFHelper.v(tag, "1Flow deactivate called ");
        OFThrottlingConfig config = OFOneFlowSHP.getInstance(this).getThrottlingConfig();
        OFHelper.v(tag, "1Flow deactivate called config activated[" + config.isActivated() + "]globalTime[" + config.getGlobalTime() + "]activatedBy[" + config.getActivatedById() + "]");
        if (config.getGlobalTime() != null && config.getGlobalTime() > 0) {
            setThrottlingAlarm(config);
        } else {
            OFHelper.v(tag, "1Flow deactivate called at else");
            config.setActivated(false);
            config.setActivatedById(null);
            OFOneFlowSHP.getInstance(this).setThrottlingConfig(config);
        }


    }

    public void setThrottlingAlarm(OFThrottlingConfig config) {
        OFHelper.v(tag, "1Flow Setting ThrottlingAlarm [" + config.getGlobalTime() + "]");

        OFOneFlowSHP shp = OFOneFlowSHP.getInstance(this);
        shp.storeValue(OFConstants.SHP_THROTTLING_TIME, config.getGlobalTime() * 1000 + System.currentTimeMillis());


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
}
