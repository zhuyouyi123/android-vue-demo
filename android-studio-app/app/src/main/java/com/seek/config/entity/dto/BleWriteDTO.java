package com.seek.config.entity.dto;

public class BleWriteDTO {

    private String key;

    private String data;

    private String address;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BleWriteDTO{" +
                "key='" + key + '\'' +
                '}';
    }
}
