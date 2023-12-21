package com.ble.dfuupgrade.enums;

public enum FirmwareUpgradeStatusEnum {
    UPGRADE_FAILED(-1, "UPGRADE_FAILED"),
    START_UPGRADE(0, "START_UPGRADE"),
    END_UPGRADE(1, "END_UPGRADE"),
    UPGRADE_SUCCESS(2, "UPGRADE_SUCCESS"),
    UPGRADING(3, "UPGRADING"),
    CONNECTING(4, "CONNECTING"),
    CONNECT_SUCCESS(5, "CONNECT_SUCCESS"),

    ;

    private final int code;

    private final String key;

    FirmwareUpgradeStatusEnum(int code, String key) {
        this.code = code;
        this.key = key;
    }

    public int getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }
}
