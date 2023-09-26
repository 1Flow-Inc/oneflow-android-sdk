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

package com.oneflow.analytics.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Looper;

import com.google.gson.Gson;
import com.oneflow.analytics.model.survey.OFThrottlingConfig;
import com.oneflow.analytics.sdkdb.OFOneFlowSHP;

public class OFMyCountDownTimerThrottling extends CountDownTimer {
    Context mContext;
    static OFMyCountDownTimerThrottling cdt;

    public static synchronized OFMyCountDownTimerThrottling getInstance(Context context, Long duration, Long interval) {
        OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling Constructor called duration[" + duration + "]interval[" + interval + "]");
        Looper.getMainLooper();
        if (cdt == null) {
            cdt = new OFMyCountDownTimerThrottling(context, duration, interval);

        }
        return cdt;
    }

    private OFMyCountDownTimerThrottling(Context context, Long duration, Long interval) {
        super(duration, interval);
        this.mContext = context;
        OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling Constructor called");

    }

    @Override
    public void onTick(long millisUntilFinished) {
        OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling tick called");
    }

    @Override
    public void onFinish() {

        OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling finish called ");
        OFThrottlingConfig config = OFOneFlowSHP.getInstance(mContext).getThrottlingConfig();
        OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling deactivate called config[" + new Gson().toJson(config) + "]");
        if (config != null) {


            OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling deactivate called config global time not null");
            if (config.isActivated()) {

                OFHelper.v("MyCountDownTimerThrottling", "OneFlow Throttling deactivate called time finished");
                config.setActivated(false);
                config.setActivatedById(null);
                OFOneFlowSHP.getInstance(mContext).setThrottlingConfig(config);


            }
        }
    }
}
