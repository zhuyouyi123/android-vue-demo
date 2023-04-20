package com.ble.blescansdk.ble.entity.bo;

import com.ble.blescansdk.ble.enums.SKYDeviceTypeEnum;

public class SeekDeviceAnalysisBO {

    public SeekDeviceAnalysisBO() {
        this.responseFind = false;
    }

    public SeekDeviceAnalysisBO(SKYDeviceTypeEnum deviceType, boolean responseFind, int responseStartByte, int startByte) {
        this.deviceType = deviceType;
        this.responseFind = responseFind;
        this.responseStartByte = responseStartByte;
        this.startByte = startByte;
    }

    private SKYDeviceTypeEnum deviceType;
    private int responseStartByte;
    private boolean responseFind;
    private int startByte;

    public SKYDeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(SKYDeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }

    public int getResponseStartByte() {
        return responseStartByte;
    }

    public void setResponseStartByte(int responseStartByte) {
        this.responseStartByte = responseStartByte;
    }

    public boolean getResponseFind() {
        return responseFind;
    }

    public void setResponseFind(boolean responseFind) {
        this.responseFind = responseFind;
    }


    public int getStartByte() {
        return startByte;
    }

    public void setStartByte(int startByte) {
        this.startByte = startByte;
    }
}
