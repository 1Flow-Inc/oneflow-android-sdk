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
import com.oneflow.analytics.model.loguser.OFLogUserRequest;
import com.oneflow.analytics.model.loguser.OFLogUserResponse;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;
import com.oneflow.analytics.utils.OFMyResponseHandlerOneFlow;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OFLogUserRepo {

    private OFLogUserRepo() {

    }

    static String tag = "LogUserRepo";

    public static void logUser(String headerKey, OFLogUserRequest lur, OFMyResponseHandlerOneFlow mrh, OFConstants.ApiHitType hitType){

        OFApiInterface connectAPI = OFRetroBaseService.getClient().create(OFApiInterface.class);
        try {
            Call<OFGenericResponse<OFLogUserResponse>> responseCall = null;
            responseCall = connectAPI.logUser(headerKey,lur);

            responseCall.enqueue(new Callback<OFGenericResponse<OFLogUserResponse>>() {
                @Override
                public void onResponse(Call<OFGenericResponse<OFLogUserResponse>> call, Response<OFGenericResponse<OFLogUserResponse>> response) {


                    OFHelper.v(tag,"OneFlow Loguser response["+response.isSuccessful()+"]");
                    if (response.isSuccessful()) {
                        if(response.body()!=null) {
                            mrh.onResponseReceived(hitType, response.body().getResult(), 0l, lur.getUser_id(),null,null);
                        }else{
                            OFHelper.v(tag,"OneFlow Loguser response body is empty");
                        }

                    } else {
                        OFHelper.v(tag,"OneFlow response 0["+response.body()+"]");
                        mrh.onResponseReceived(hitType,null,0l,"",null,null);
                    }
                }

                @Override
                public void onFailure(Call<OFGenericResponse<OFLogUserResponse>> call, Throwable t) {

                    OFHelper.e(tag,"OneFlow error["+t.toString()+"]");
                    OFHelper.e(tag,"OneFlow errorMsg["+t.getMessage()+"]");

                }
            });
        } catch (Exception ex) {
            // error
        }

    }
}
