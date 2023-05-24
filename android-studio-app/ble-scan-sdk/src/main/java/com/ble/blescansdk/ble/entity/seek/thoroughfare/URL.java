package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class URL {
    // 通道类型
    private final String type;
    private String link;
    private int measurePower;

    public URL(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public URL setLink(String link) {
        this.link = link;
        return this;
    }

    public int getMeasurePower() {
        return measurePower;
    }

    public URL setMeasurePower(int measurePower) {
        this.measurePower = measurePower;
        return this;

    }
}