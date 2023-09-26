package com.oneflow.analytics.repositories

import android.content.Context
import com.oneflow.analytics.model.events.OFRecordEventsTab
import com.oneflow.analytics.sdkdb.OFSDKDB
import com.oneflow.analytics.utils.OFConstants.ApiHitType
import com.oneflow.analytics.utils.OFHelper
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class OFEventDBRepoKT {
    fun insertEvents(context: Context, eventName: String?, data: Map<String?, Any?>?, value: Int, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?) {
        OFHelper.v("EventDBRepoKT.insertEvents", "1Flow reached at insertEvent method")

        val job = Job()

        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            val ret = OFRecordEventsTab()
            ret.uuid = OFHelper.mongoObjectId();
            ret.eventName = eventName
            ret.dataMap = data
            ret.value = value.toString()
            ret.time = Calendar.getInstance().timeInMillis / 1000
            ret.synced = 0
            ret.createdOn = Calendar.getInstance().timeInMillis
            val inserted = sdkdb.eventDAO()?.insertAll(ret)
            OFHelper.v("EventDBRepoKT.insertEvents", "1Flow inserting events[" + eventName + "][" + inserted + "]");
            mrh.onResponseReceived(type, inserted, 0L, eventName, null, null)
        }



    }

    fun deleteEvents(context: Context, ids: Array<Int?>?, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?) {
        OFHelper.v("EventDBRepoKT.DeleteEvents", "1Flow reached at delete method")

        val job = Job()

        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            val deleted = sdkdb.eventDAO()?.deleteSyncedEvents(ids)
            mrh.onResponseReceived(type, deleted, 0L, "", null, null)
        }
    }

    fun fetchEvents(context: Context, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?) {
        OFHelper.v("EventDBRepoKT.fetchEvents", "1Flow reached at fetchEvents method")

        val job = Job()

        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            OFHelper.v("EventDBRepoKT", "1Flow fetching events from db ")
            val recordEvents = sdkdb.eventDAO().getAllPendingRecordedEvents();//getAllUnsyncedEvents()
            OFHelper.v("EventDBRepoKT", "1Flow fetching events from db size["+recordEvents.size+"]")
            mrh.onResponseReceived(type, recordEvents, 0L, "", null, null)
        }


    }

    fun fetchEventsBeforeSurvey(context: Context, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?) {
        OFHelper.v("EventDBRepoKT.fetchEventsBeforeSurvey", "1Flow reached at fetchEventsBeforeSurvey method")

        val job = Job()

        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            //val beforeSurveyEvent = sdkdb.eventDAO()?.getEventBeforeSurvey3Sec(Calendar.getInstance().timeInMillis - 10000) // -3000 added for before 3sec logic, changed to 10 sec as new requirement on 30-01-23

            val beforeSurveyEvent = sdkdb.eventDAO()?.getEventBeforeSurveyFetched(Calendar.getInstance().timeInMillis - (1000 * 30), Calendar.getInstance().timeInMillis) // now passing range of time to tackle with orphan event which has not been deleted

            mrh.onResponseReceived(type, beforeSurveyEvent, 0L, "", null, null)
        }

    }


}