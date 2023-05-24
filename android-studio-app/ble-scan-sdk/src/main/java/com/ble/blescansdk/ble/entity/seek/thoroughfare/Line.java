package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class Line {

    private final String type;

    private String hwid;

    private int measurePower;

    private String secureMessage;

    private short timesTamp;

    public Line(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getHwid() {
        return hwid;
    }

    public Line setHwid(String hwid) {
        this.hwid = hwid;
        return this;
    }

    public int getMeasurePower() {
        return measurePower;
    }

    public Line setMeasurePower(int measurePower) {
        this.measurePower = measurePower;
        return this;
    }

    public String getSecureMessage() {
        return secureMessage;
    }

    public Line setSecureMessage(String secureMessage) {
        this.secureMessage = secureMessage;
        return this;
    }

    public short getTimesTamp() {
        return timesTamp;
    }

    public Line setTimesTamp(short timesTamp) {
        this.timesTamp = timesTamp;
        return this;
    }
}
