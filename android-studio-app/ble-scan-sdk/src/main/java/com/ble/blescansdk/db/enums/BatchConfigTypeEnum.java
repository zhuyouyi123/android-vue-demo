package com.ble.blescansdk.db.enums;

public enum BatchConfigTypeEnum {

    CHANNEL(0, "CHANNEL"),

    SECRET_KEY(1, "SECRET_KEY"),

    ;

    private final int code;

    private final String type;

    BatchConfigTypeEnum(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static BatchConfigTypeEnum getByCode(int code) {
        for (BatchConfigTypeEnum value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }

        return CHANNEL;
    }
}
