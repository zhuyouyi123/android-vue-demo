package com.ble.blescansdk.db.enums;

public enum BatchConfigResultEnum {

    SUCCESS(0,"SUCCESS"),

    FAIL(-1,"FAIL"),
    ;

    private final int code;

    private final String type;

    BatchConfigResultEnum(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
