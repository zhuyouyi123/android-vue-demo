package com.seekcy.bracelet.data.enums;

public enum DateUnitEnum {

    YEAR(4),
    MONTH(3),
    WEEK(2),
    DAY(1),
    ;

    private final int code;

    DateUnitEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DateUnitEnum getByCode(int code) {
        for (DateUnitEnum value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return DAY;
    }
}
