package com.panvan.app.data.entity.vo;

import com.panvan.app.data.holder.DeviceHolder;

public class DeviceInfoVO {

    private DeviceHolder.DeviceInfo deviceInfo;

    public DeviceInfoVO(DeviceHolder.DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceHolder.DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceHolder.DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
