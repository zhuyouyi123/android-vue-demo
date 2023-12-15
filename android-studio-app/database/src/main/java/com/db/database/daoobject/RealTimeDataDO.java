package com.db.database.daoobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "real_time_data", indices = {@Index(value = "date_time")})
public class RealTimeDataDO {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "date_time")
    public Integer dateTime;

    @ColumnInfo(name = "params_json")
    public String paramsJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDateTime() {
        return dateTime;
    }

    public void setDateTime(Integer dateTime) {
        this.dateTime = dateTime;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }
}
