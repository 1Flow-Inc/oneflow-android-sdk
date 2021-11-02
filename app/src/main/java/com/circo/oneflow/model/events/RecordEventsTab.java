package com.circo.oneflow.model.events;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.google.gson.annotations.SerializedName;
import com.circo.oneflow.sdkdb.convertes.MapConverter;

import java.util.HashMap;

@Entity (tableName = "recordEvents")
public class RecordEventsTab {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @SerializedName("_id")
    private int id;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String eventName;

    @TypeConverters(MapConverter.class)
    @ColumnInfo(name = "parameters")
    @SerializedName("parameters")
    private HashMap<String,String> dataMap;

    @ColumnInfo(name = "time")
    @SerializedName("time")
    private Long time;

    @ColumnInfo(name = "value")
    @SerializedName("value")
    private String value;

    @ColumnInfo(name = "synced")
    @SerializedName("synced")
    private Integer synced = 0;

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

    public HashMap<String,String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String,String> dataMap) {
        this.dataMap = dataMap;
    }

    public Integer getSynced() {
        return synced;
    }

    public void setSynced(Integer synced) {
        this.synced = synced;
    }
}