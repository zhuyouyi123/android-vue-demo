package com.panvan.app.data.entity.vo.device;

public class DeviceVO {
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
    private String firmwareVersion;

    private Boolean inUse;

    private Boolean bound;

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

    public Boolean getBound() {
        return bound;
    }

    public void setBound(Boolean bound) {
        this.bound = bound;
    }
}
