package com.ble.blescansdk.ble.enums;

public enum BleConnectStatusEnum {
    DISCONNECT(0, "未连接"),
    CONNECTING(1, "连接中"),
    CONNECTED(2, "已连接"),
    ;

    private final int status;

    private final String desc;

    BleConnectStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static BleConnectStatusEnum getByStatus(int status) {
        for (BleConnectStatusEnum value : values()) {
            if ((status == value.getStatus())) {
                return value;
            }
        }
        return null;
    }
}
