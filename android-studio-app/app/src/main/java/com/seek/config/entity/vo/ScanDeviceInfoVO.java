package com.seek.config.entity.vo;

public class ScanDeviceInfoVO {
    private String address;

    private Integer rssi;

    public ScanDeviceInfoVO(String address, Integer rssi) {
        this.address = address;
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }
}
