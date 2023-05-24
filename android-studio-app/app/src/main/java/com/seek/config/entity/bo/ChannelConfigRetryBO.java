package com.seek.config.entity.bo;

import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;

public class ChannelConfigRetryBO {

    private SeekStandardDevice device;

    private String instruct;

    private int agreementNumber;

    private String channelNumber;

    private int retryCount = 1;

    public SeekStandardDevice getDevice() {
        return device;
    }

    public void setDevice(SeekStandardDevice device) {
        this.device = device;
    }

    public String getInstruct() {
        return instruct;
    }

    public void setInstruct(String instruct) {
        this.instruct = instruct;
    }

    public int getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(int agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
