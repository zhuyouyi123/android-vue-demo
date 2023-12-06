package com.db.database.daoobject;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "communication_data")
public class CommunicationDataDO {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    /**
     * 创建日
     */
    @ColumnInfo(name = "data_date")
    public Integer dataDate;
    /**
     * HistoryDataTypeEnum
     */
    public String type;

    public String data;

    @ColumnInfo(name = "complete_data")
    public Boolean completeData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getDataDate() {
        return dataDate;
    }

    public void setDataDate(Integer dataDate) {
        this.dataDate = dataDate;
    }

    public Boolean getCompleteData() {
        return completeData;
    }

    public void setCompleteData(Boolean completeData) {
        this.completeData = completeData;
    }

    @Override
    public String toString() {
        return "CommunicationDataDO{" +
                "id=" + id +
                ", dataDate='" + dataDate + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", completeData=" + completeData +
                '}';
    }
}
