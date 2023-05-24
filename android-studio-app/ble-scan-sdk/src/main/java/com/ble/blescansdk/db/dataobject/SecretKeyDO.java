package com.ble.blescansdk.db.dataobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "secret_key", indices = {@Index(value = {"address"})})
public class SecretKeyDO {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String address;

    @ColumnInfo(name = "secret_key")
    private String secretKey;

    private Long createTime ;

    public SecretKeyDO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
