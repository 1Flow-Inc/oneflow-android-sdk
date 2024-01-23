package com.oneflow.analytics.model.announcement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oneflow.analytics.model.OFBaseModel;

import java.util.ArrayList;
import java.util.List;

public class OFAnnouncementPropertySubFilters extends OFBaseModel {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("data_type")
    @Expose
    private String dataType;
    @SerializedName("condition")
    @Expose
    private String condition;
    @SerializedName("values")
    @Expose
    private String[] values;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
