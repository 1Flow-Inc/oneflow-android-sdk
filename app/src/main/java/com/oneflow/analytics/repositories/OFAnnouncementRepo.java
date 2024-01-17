package com.oneflow.analytics.repositories;

import com.oneflow.analytics.model.OFApiInterface;
import com.oneflow.analytics.model.OFGenericResponse;
import com.oneflow.analytics.model.OFRetroBaseService;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementDetailResponse;
import com.oneflow.analytics.model.announcement.OFGetAnnouncementResponse;
import com.oneflow.analytics.model.events.OFEventAPIRequest;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OFAnnouncementRepo {

    private OFAnnouncementRepo() {

    }

    static String tag = "AnnouncementRepo";
    public static void getAnnouncement(String headerKey, OFMyResponseHandlerOneFlow mrh, OFConstants.ApiHitType type, String userId){

        OFApiInterface connectAPI = OFRetroBaseService.getClient().create(OFApiInterface.class);
        try {
            Call<OFGenericResponse<OFGetAnnouncementResponse>> responseCall = null;


            responseCall = connectAPI.getAnnouncement(headerKey,userId,"mobile");

            responseCall.enqueue(new Callback<OFGenericResponse<OFGetAnnouncementResponse>>() {
                @Override
                public void onResponse(Call<OFGenericResponse<OFGetAnnouncementResponse>> call, Response<OFGenericResponse<OFGetAnnouncementResponse>> response) {

                    if (response.isSuccessful()) {
                        mrh.onResponseReceived(type,response.body().getResult(),0l,"",null,null);
                    } else {
                        OFHelper.v(tag,"OneFlow response 0["+response.body()+"]");
                    }
                }

                @Override
                public void onFailure(Call<OFGenericResponse<OFGetAnnouncementResponse>> call, Throwable t) {


                    OFHelper.e(tag,"OneFlow error["+t.toString()+"]");
                    OFHelper.e(tag,"OneFlow errorMsg["+t.getMessage()+"]");

                }
            });
        } catch (Exception ex) {
            // error
        }

    }

    public static void getAnnouncementDetail(String headerKey, OFMyResponseHandlerOneFlow mrh, OFConstants.ApiHitType type, String ids, String reserved){

        OFApiInterface connectAPI = OFRetroBaseService.getClient().create(OFApiInterface.class);
        try {
            Call<OFGenericResponse<ArrayList<OFGetAnnouncementDetailResponse>>> responseCall = null;


            responseCall = connectAPI.getAnnouncementDetail(headerKey,ids);

            responseCall.enqueue(new Callback<OFGenericResponse<ArrayList<OFGetAnnouncementDetailResponse>>>() {
                @Override
                public void onResponse(Call<OFGenericResponse<ArrayList<OFGetAnnouncementDetailResponse>>> call, Response<OFGenericResponse<ArrayList<OFGetAnnouncementDetailResponse>>> response) {

                    if (response.isSuccessful()) {
                        mrh.onResponseReceived(type,response.body().getResult(),0l,reserved,null,null);
                    } else {
                        OFHelper.v(tag,"OneFlow response 0["+response.body()+"]");
                    }
                }

                @Override
                public void onFailure(Call<OFGenericResponse<ArrayList<OFGetAnnouncementDetailResponse>>> call, Throwable t) {


                    OFHelper.e(tag,"OneFlow error["+t.toString()+"]");
                    OFHelper.e(tag,"OneFlow errorMsg["+t.getMessage()+"]");

                }
            });
        } catch (Exception ex) {
            // error
        }

    }

}
