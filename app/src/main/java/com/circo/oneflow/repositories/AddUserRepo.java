package com.circo.oneflow.repositories;

import android.content.Context;

import com.circo.oneflow.model.ApiInterface;
import com.circo.oneflow.model.GenericResponse;
import com.circo.oneflow.model.RetroBaseService;
import com.circo.oneflow.model.adduser.AddUserRequest;
import com.circo.oneflow.model.adduser.AddUserResultResponse;
import com.circo.oneflow.sdkdb.OneFlowSHP;
import com.circo.oneflow.utils.Constants;
import com.circo.oneflow.utils.Helper;
import com.circo.oneflow.utils.MyResponseHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserRepo {

    static String tag = "AddUserRepo";
    MyResponseHandler myResponseHandler;
    public static void addUser(AddUserRequest aur, Context context, MyResponseHandler mrh, Constants.ApiHitType hitType){

        ApiInterface connectAPI = RetroBaseService.getClient().create(ApiInterface.class);
        try {
            Call<GenericResponse<AddUserResultResponse>> responseCall = null;



            responseCall = connectAPI.addUserComman(new OneFlowSHP(context).getStringValue(Constants.APPIDSHP),aur);

            responseCall.enqueue(new Callback<GenericResponse<AddUserResultResponse>>() {
                @Override
                public void onResponse(Call<GenericResponse<AddUserResultResponse>> call, Response<GenericResponse<AddUserResultResponse>> response) {
                    Helper.v(tag, "OneFlow reached success["+response.isSuccessful()+"]");
                    Helper.v(tag, "OneFlow reached success raw["+response.raw()+"]");
                    Helper.v(tag, "OneFlow reached success errorBody["+response.errorBody()+"]");
                    Helper.v(tag, "OneFlow reached success message["+response.message()+"]");

                    if (response.isSuccessful()) {
                        Helper.v(tag,"OneFlow response["+response.body().toString()+"]");
                        Helper.v(tag,"OneFlow response["+response.body().getSuccess()+"]");
                        Helper.v(tag,"OneFlow response["+response.body().getMessage()+"]");
                        Helper.v(tag,"OneFlow response["+response.body().getResult().getAnalytic_user_id()+"]");

                        new OneFlowSHP(context).setUserDetails(response.body().getResult());
                        mrh.onResponseReceived(hitType,null,0);

                       /* AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Helper.v(tag,"OneFlow inserting data");
                                db.userDAO().insertUser(response.body().getResult());
                                Helper.v(tag,"OneFlow inserted data");
                            }
                        }); */

                        Helper.v(tag,"OneFlow record inserted...");
                    } else {
                        Helper.v(tag,"OneFlow response 0["+response.body()+"]");
                       /* Helper.v(tag,"OneFlow response 1["+response.body().getMessage()+"]");
                        Helper.v(tag,"OneFlow response 2["+response.body().getSuccess()+"]");*/

                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<AddUserResultResponse>> call, Throwable t) {

                    Helper.e(tag,"OneFlow error["+t.toString()+"]");
                    Helper.e(tag,"OneFlow errorMsg["+t.getMessage()+"]");

                }
            });
        } catch (Exception ex) {

        }

    }
}