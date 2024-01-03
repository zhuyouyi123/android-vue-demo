package com.db.database.daoobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "device", indices = {@Index(value = {"address"})})
public class DeviceDO {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * 设备地址
     */
    private String address;

    /**
     * 型号
     */
    private String model;

    /**
     * 设备电量
     */
    private Integer batter;

    /**
     * 固件版本
     */
    @ColumnInfo(name = "firmware_version")
    private String firmwareVersion;

    @ColumnInfo(name = "ota_firmware_version")
    private String otaFirmwareVersion;

    @ColumnInfo(name = "ota_address")
    private String otaAddress;

    /**
     * 正在使用
     */
    @ColumnInfo(name = "in_use")
    private Boolean inUse;


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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getBatter() {
        return batter;
    }

    public void setBatter(Integer batter) {
        this.batter = batter;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public String getOtaFirmwareVersion() {
        return otaFirmwareVersion;
    }

    public void setOtaFirmwareVersion(String otaFirmwareVersion) {
        this.otaFirmwareVersion = otaFirmwareVersion;
    }

    public String getOtaAddress() {
        return otaAddress;
    }

    public void setOtaAddress(String otaAddress) {
        this.otaAddress = otaAddress;
    }
}
