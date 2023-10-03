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

package com.oneflow.analytics.model;

import com.oneflow.analytics.utils.OFConstants;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OFRetroBaseService {

    static String urlPrefix = "https://";


    static String baseDomainDev = "dev-sdk.1flow.app/api/2021-06-15/";
    static String baseDomainBeta = "beta-sdk.1flow.app/api/2021-06-15/";
    static String baseDomainProd = "api-sdk.1flow.app/api/2021-06-15/";

    static String BASE_URL;
    protected static HashMap<String, String> baseUrl = new HashMap<>();

    private OFRetroBaseService() {
        // Constructor
    }

    public static Retrofit getClient() {
        Retrofit retrofit = null;

        baseUrl.put("dev",urlPrefix+baseDomainDev);
        baseUrl.put("beta",urlPrefix+baseDomainBeta);
        baseUrl.put("prod",urlPrefix+baseDomainProd);

        BASE_URL = baseUrl.get(OFConstants.MODE);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient clientProd = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientProd)
                .build();


        return retrofit;
    }

}
