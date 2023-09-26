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

package com.oneflow.analytics.repositories;

import com.oneflow.analytics.model.OFApiInterface;
import com.oneflow.analytics.model.OFGenericResponse;
import com.oneflow.analytics.model.OFRetroBaseService;
import com.oneflow.analytics.model.adduser.OFAddUserReq;
import com.oneflow.analytics.model.adduser.OFAddUserResponse;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandler;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OFAddUserRepo {

    static String tag = "OFAddUserRepo";
    OFMyResponseHandler myResponseHandler;
    public static void addUser(String headerKey, OFAddUserReq aur, OFMyResponseHandlerOneFlow mrh, OFConstants.ApiHitType hitType){

        OFApiInterface connectAPI = OFRetroBaseService.getClient().create(OFApiInterface.class);
        try {
            Call<OFGenericResponse<OFAddUserResponse>> responseCall = null;

            responseCall = connectAPI.addUserComman(headerKey,aur);

            responseCall.enqueue(new Callback<OFGenericResponse<OFAddUserResponse>>() {
                @Override
                public void onResponse(Call<OFGenericResponse<OFAddUserResponse>> call, Response<OFGenericResponse<OFAddUserResponse>> response) {
                    OFHelper.v(tag, "OneFlow add user reached success["+response.isSuccessful()+"]");

                    if (response.isSuccessful()) {

                        OFHelper.v(tag,"OneFlow user created");
                        if(response.body()!=null) {
                            mrh.onResponseReceived(hitType, response.body().getResult(), 0l, "",null,null);
                        }

                    } else {
                        OFHelper.v(tag, "OneFlow response 0[" + response.message() + "]");
                         mrh.onResponseReceived(hitType, null, 0l, response.message(),null,null);

                    }
                }

                @Override
                public void onFailure(Call<OFGenericResponse<OFAddUserResponse>> call, Throwable t) {

                    mrh.onResponseReceived(hitType,null,0l,"Something went wrong",null,null);
                    OFHelper.e(tag,"OneFlow error["+t.toString()+"]");
                    OFHelper.e(tag,"OneFlow errorMsg["+t.getMessage()+"]");

                }
            });
        } catch (Exception ex) {
            OFHelper.e(tag,"Error["+ex.getMessage()+"]");
        }

    }
}
