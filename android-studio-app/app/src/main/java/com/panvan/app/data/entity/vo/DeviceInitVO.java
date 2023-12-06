package com.panvan.app.data.entity.vo;

public class DeviceInitVO {

    /**
     * 设备mac
     */
    private String address;

    /**
     * 第一次使用
     */
    private Boolean firstUse;

    public DeviceInitVO(String address, Boolean firstUse) {
        this.address = address;
        this.firstUse = firstUse;
    }

    public DeviceInitVO() {
        this.firstUse = true;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getFirstUse() {
        return firstUse;
    }

    public void setFirstUse(Boolean firstUse) {
        this.firstUse = firstUse;
    }
}
