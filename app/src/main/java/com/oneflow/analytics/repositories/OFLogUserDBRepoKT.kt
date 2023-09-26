package com.oneflow.analytics.repositories

import android.content.Context
import com.oneflow.analytics.model.survey.OFGetSurveyListResponse
import com.oneflow.analytics.model.survey.OFSurveyUserInput
import com.oneflow.analytics.sdkdb.OFSDKDB
import com.oneflow.analytics.utils.OFConstants.ApiHitType
import com.oneflow.analytics.utils.OFHelper
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OFLogUserDBRepoKT {

    fun insertUserInputs(context: Context, sui: OFSurveyUserInput, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?) {
        OFHelper.v("OFLogUserDBRepoKT.insertUserInputs", "OneFlow SurveyInput reached at insertUserInput method [" + sui.user_id + "]")

        val job = Job()

        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            var inserted = sdkdb.logDAO().insertUserInput(sui)
            OFHelper.v("OFLogUserDBRepoKT", "OneFlow SurveyInput inserted["+inserted+"]")
            mrh.onResponseReceived(type, sui, inserted, "", null, null)
        }


    }

    fun fetchSurveyInput(context: Context, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?) {
        OFHelper.v("LogDBRepo.fetchSurveyInputList", "OneFlow reached at fetchSurveyEvents method")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            val sui = sdkdb.logDAO().getOfflineUserInput()
            mrh.onResponseReceived(type, sui, 0L, "", null, null)
        }

    }

    // update record once data is sent to server
    /*fun updateSurveyInput(context: Context, mrh: OFMyResponseHandlerOneFlow?, type: ApiHitType?, syncNew: Boolean?, id: Int?) {
        OFHelper.v("LogDBRepo.updateSurveyInput", "OneFlow reached at updateSurveyInput method")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb: OFSDKKOTDB = OFSDKKOTDB.getDatabase(context, scope)
            var updated = sdkdb.logDAOKT()?.updateUserInput(syncNew, id)

            OFHelper.v("LogDBRepo.updateSurveyInput", "OneFlow update called["+updated+"]id["+id+"]synced["+syncNew+"]")

        }

    }*/
    fun updateSurveyInput(context: Context, mrh: OFMyResponseHandlerOneFlow?, type: ApiHitType?, syncNew: Boolean?, id: String?) {
        OFHelper.v("LogDBRepo.updateSurveyInput", "OneFlow reached at updateSurveyInput method")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            try {
                val sdkdb = OFSDKDB.getInstance(context)
                var updated = sdkdb.logDAO()?.updateUserInput(syncNew, id)

                OFHelper.v("LogDBRepo.updateSurveyInput", "OneFlow update called[" + updated + "]id[" + id + "]synced[" + syncNew + "]")

                if (mrh != null) {
                    mrh.onResponseReceived(type, updated, 0L, "", "", "")
                }
            }catch (ex: Exception){
                // error
            }
        }

    }

    // update user id for surveys before log
   /* fun updateSurveyUserId(context: Context, mrh: OFMyResponseHandlerOneFlow, userId: String?, hitType: ApiHitType?) {
        OFHelper.v("LogDBRepo.updateSurveyUserId", "OneFlow updating empty user id")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKKOTDB.getDatabase(context, scope)
            val inserted = sdkdb.logDAOKT()?.updateUserID(userId)
            mrh.onResponseReceived(hitType, inserted, 0L, "", null, null)
        }

    }*/
    fun updateSurveyUserId(context: Context, mrh: OFMyResponseHandlerOneFlow, userId: String?, hitType: ApiHitType?) {
        OFHelper.v("LogDBRepo.updateSurveyUserId", "OneFlow updating empty user id")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            val inserted = sdkdb.logDAO()?.updateUserID(userId)
            mrh.onResponseReceived(hitType, inserted, 0L, "", null, null)
        }

    }

    fun findSurveyForID(context: Context, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?, gslr: OFGetSurveyListResponse?, id: String, userId: String, eventName: String?) {
        OFHelper.v("LogDBRepo.findSurveyForID", "OneFlow reached at updateSurveyInput method")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            val surveyUserInput = sdkdb.logDAO()?.getSurveyForID(id, userId)
            val ret:Long? = if (surveyUserInput != null) {
                surveyUserInput.createdOn
            } else {
                0L
            }
            OFHelper.e("LogDBRepo.updateSurveyUserId", "OneFlow find Survey for id[" + ret + "]eventName[" + eventName + "]")
            mrh.onResponseReceived(type, gslr, 0L, "", ret, eventName)
        }


    }

    fun findLastSubmittedSurveyID(context: Context, mrh: OFMyResponseHandlerOneFlow, type: ApiHitType?, eventName: String?, surveyToInit :OFGetSurveyListResponse){
        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val sdkdb = OFSDKDB.getInstance(context)
            val surveyUserInput = sdkdb.logDAO()?.getLastSyncedSurveyId()
            try {
                val ofSurveyUserInput = surveyUserInput as OFSurveyUserInput
                OFHelper.v("OFLogUserDBRepoKT", "OneFlow fetching Last submitted survey[" + ofSurveyUserInput.createdOn + "]")
                OFHelper.v("OFLogUserDBRepoKT", "OneFlow fetching Last submitted survey[" + ofSurveyUserInput.synced + "]")
                mrh.onResponseReceived(type, surveyUserInput, 0L, eventName, surveyToInit, null)
            }catch (ex: Exception){
                mrh.onResponseReceived(type, null, 0L, eventName, surveyToInit, null)
            }
        }
    }

}