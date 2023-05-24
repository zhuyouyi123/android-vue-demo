package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class IBeacon {
    // 通道类型
    private final String type;

    private String uuid;

    private int major;

    private int minor;

    private int measurePower;

    public IBeacon(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public IBeacon setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public int getMajor() {
        return major;
    }

    public IBeacon setMajor(int major) {
        this.major = major;
        return this;
    }

    public int getMinor() {
        return minor;
    }

    public IBeacon setMinor(int minor) {
        this.minor = minor;
        return this;
    }

    public int getMeasurePower() {
        return measurePower;
    }

    public IBeacon setMeasurePower(int measurePower) {
        this.measurePower = measurePower;
        return this;
    }
}
