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

package com.oneflow.analytics.model.events;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OFRecordEventDAO {

    @Insert
    long insertAll(OFRecordEventsTab ret);

    @Query("Select * from RecordEvents")
    List<OFRecordEventsTab> getAllPendingRecordedEvents();

    @Query("Select name from RecordEvents where created_on<:surveyTime")

    String[] getEventBeforeSurveyFetchedOld(Long surveyTime);

    @Query("Select name from RecordEvents where created_on<:surveyTime")
    String[] getEventBeforeSurveyFetchedOld1(Long surveyTime);

    @Query("Select name from RecordEvents where created_on between :startingFrom and :surveyTime")
    String[] getEventBeforeSurveyFetched(Long startingFrom, Long surveyTime);

    @Query("Select name from RecordEvents where created_on>:surveyTime order by created_on")
    String[] getEventBeforeSurvey3Sec(Long surveyTime);

    @Query("Delete from RecordEvents where _id in (:idList)")
    Integer deleteSyncedEvents(Integer[] idList);

}
