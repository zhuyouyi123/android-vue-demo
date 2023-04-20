package com.ble.blescansdk.ble.enums;

public enum ManufacturerEnum {
    /**
     * 真趣使用
     */
    SEEK(0, "SEEK"),

    /**
     * 标品信标
     */
    SEEK_STANDARD(1, "SEEK_STANDARD"),
    ;

    private final int code;

    private final String name;

    ManufacturerEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
