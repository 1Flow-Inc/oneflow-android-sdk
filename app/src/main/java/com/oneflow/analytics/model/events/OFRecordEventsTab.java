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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.sdkdb.convertes.OFMapConverter;

import java.util.HashMap;

@Entity (tableName = "RecordEvents")
public class OFRecordEventsTab {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @SerializedName("_id")
    private int id;

    @ColumnInfo(name = "uu_id")
    @SerializedName("uu_id")
    private String uuid;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String eventName;

    @TypeConverters(OFMapConverter.class)
    @ColumnInfo(name = "parameters")
    @SerializedName("parameters")
    private HashMap<String,Object> dataMap;

    @ColumnInfo(name = "time")
    @SerializedName("time")
    private Long time;

    @ColumnInfo(name = "value")
    @SerializedName("value")
    private String value;

    @ColumnInfo(name = "synced")
    @SerializedName("synced")
    private Integer synced = 0;

    @ColumnInfo(name = "created_on")
    @SerializedName("created_on")
    private Long createdOn;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public HashMap<String,Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String,Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Integer getSynced() {
        return synced;
    }

    public void setSynced(Integer synced) {
        this.synced = synced;
    }
}
