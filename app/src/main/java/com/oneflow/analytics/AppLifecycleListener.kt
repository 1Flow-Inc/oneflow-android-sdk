
package com.oneflow.analytics

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.oneflow.analytics.sdkdb.OFOneFlowSHP
import com.oneflow.analytics.utils.OFConstants
import com.oneflow.analytics.utils.OFHelper
import java.util.*

class AppLifecycleListener : LifecycleObserver {
    var context:Context
    var shp:OFOneFlowSHP
    constructor(context: Context){
        this.context = context
        this.shp = OFOneFlowSHP.getInstance(context)
    }

    var tag = "AppLifecycleListener"
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() { // app moved to foreground
       // OFHelper.v(tag, "1Flow app is in Foreground");
        //GetSurveyListResponse surveyItem = checkSurveyTitleAndScreens(tag);

        val appVer: Any?
        appVer = try {
            OneFlow.mContext.packageManager.getPackageInfo(OneFlow.mContext.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }


        OFHelper.v(tag, "1Flow app is in Foreground[" + shp.getBooleanValue(OFConstants.AUTOEVENT_SESSIONSTART, false) + "]");
        val mapvalues = HashMap<String, Any>()
        mapvalues.put("library_version", OFConstants.currentVersion)
        mapvalues.put("app_version", appVer)

        //added this condition to avoid storing start_session twice once alread being called from add user response
        if(!shp.getBooleanValue(OFConstants.AUTOEVENT_SESSIONSTART, false)) {

            shp.storeValue(OFConstants.AUTOEVENT_SESSIONSTART, true)
            OneFlow.recordEvents(OFConstants.AUTOEVENT_SESSIONSTART, mapvalues);
        }else{
            OFHelper.v(tag, "1Flow app is in start_session already recorded");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() { // app moved to background
        OFHelper.v(tag, "1Flow app is in Background [" + shp.getBooleanValue(OFConstants.AUTOEVENT_SESSIONSTART, false) + "]");
        shp.storeValue(OFConstants.AUTOEVENT_SESSIONSTART, false)
    }
}