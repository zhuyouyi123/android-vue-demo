package com.seek.config.entity.dto;

public class ScanInitDTO {

    private String sortType;

    private String address;

    private Boolean allDevice;

    private Integer rssiValue;

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getAllDevice() {
        if (null == allDevice) {
            return true;
        }
        return allDevice;
    }

    public void setAllDevice(Boolean allDevice) {
        this.allDevice = allDevice;
    }

    public Integer getRssiValue() {
        if (null == rssiValue) {
            return -100;
        }
        return rssiValue;
    }

    public void setRssiValue(Integer rssiValue) {
        this.rssiValue = rssiValue;
    }
}
