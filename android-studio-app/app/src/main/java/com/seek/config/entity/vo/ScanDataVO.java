package com.seek.config.entity.vo;

import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;

import java.util.List;

public class ScanDataVO {

    private List<SeekStandardDevice> list;

    private Boolean scanning;

    public List<SeekStandardDevice> getList() {
        return list;
    }

    public void setList(List<SeekStandardDevice> list) {
        this.list = list;
    }

    public Boolean getScanning() {
        return scanning;
    }

    public void setScanning(Boolean scanning) {
        this.scanning = scanning;
    }

    @Override
    public String toString() {
        return "ScanDataVO{" +
                "list=" + list +
                ", scanning=" + scanning +
                '}';
    }
}
