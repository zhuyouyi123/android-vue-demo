package com.ble.blescansdk.ble.enums.seekstandard;

public enum SensorEnum {

    NULL(0),

    ACC(1),

    HUM_I_TURE(2),

    PHOTOSENSITIVE(4),

    ;
    private final int code;

    SensorEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
