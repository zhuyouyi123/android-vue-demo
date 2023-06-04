package com.ble.blescansdk.db.dataobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "batch_config_record", indices = {@Index(value = {"address"})})
public class BatchConfigRecordDO {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Integer type;

    /**
     * {@link com.ble.blescansdk.db.enums.BatchConfigResultEnum}
     */
    private Integer result;

    private String address;

    @ColumnInfo(name = "fail_reason")
    private Integer failReason;

    public BatchConfigRecordDO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getFailReason() {
        return failReason;
    }

    public void setFailReason(Integer failReason) {
        this.failReason = failReason;
    }
}
