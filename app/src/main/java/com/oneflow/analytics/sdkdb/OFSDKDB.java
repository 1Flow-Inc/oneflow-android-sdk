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

package com.oneflow.analytics.sdkdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.oneflow.analytics.model.adduser.OFAddUserResponse;
import com.oneflow.analytics.model.events.OFRecordEventDAO;
import com.oneflow.analytics.model.events.OFRecordEventsTab;
import com.oneflow.analytics.model.loguser.OFLogDAO;
import com.oneflow.analytics.model.survey.OFSurveyUserInput;
import com.oneflow.analytics.sdkdb.adduser.OFUserDAO;
import com.oneflow.analytics.sdkdb.convertes.OFDataConverterDevice;
import com.oneflow.analytics.sdkdb.convertes.OFDataConverterLocation;
import com.oneflow.analytics.sdkdb.convertes.OFMapConverter;
import com.oneflow.analytics.sdkdb.convertes.OFSurveyUserResponseChildConverter;
import com.oneflow.analytics.sdkdb.survey.OFSubmittedSurveyDAO;
import com.oneflow.analytics.sdkdb.survey.OFSubmittedSurveysTab;
import com.oneflow.analytics.utils.OFConstants;


@Database(entities = {OFRecordEventsTab.class, OFAddUserResponse.class, OFSubmittedSurveysTab.class, OFSurveyUserInput.class},version = 74)

@TypeConverters({OFMapConverter.class, OFDataConverterLocation.class, OFDataConverterDevice.class, OFSurveyUserResponseChildConverter.class})
public abstract class OFSDKDB extends RoomDatabase {

    private static volatile OFSDKDB sdkdb;

    protected OFSDKDB() {

    }

    private static OFSDKDB initialize(final Context context) {

        return Room.databaseBuilder(context, OFSDKDB.class, OFConstants.DBNAME).fallbackToDestructiveMigration().build();
    }

    public static synchronized OFSDKDB getInstance(Context context) {
        if (sdkdb == null) {
            sdkdb = initialize(context);
        }
        return sdkdb;
    }

    public abstract OFRecordEventDAO eventDAO();

    public abstract OFSubmittedSurveyDAO submittedSurveyDAO();

    public abstract OFUserDAO userDAO();

    public abstract OFLogDAO logDAO();
}