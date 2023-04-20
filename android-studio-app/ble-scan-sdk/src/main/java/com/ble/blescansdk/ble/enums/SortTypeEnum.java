package com.ble.blescansdk.ble.enums;

import com.ble.blescansdk.ble.utils.StringUtils;

public enum SortTypeEnum {

    RSSI_RISE("rssi_rise"),
    RSSI_FALL("rssi_fall"),
    MAC_RISE("mac_rise"),
    MAC_FALL("mac_fall"),
    BATTERY_RISE("battery_rise"),
    BATTERY_FALL("battery_fall"),
    ;
    private final String type;

    SortTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SortTypeEnum getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return RSSI_FALL;
        }
        SortTypeEnum sortTypeEnum = null;
        for (SortTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                sortTypeEnum = value;
                break;
            }
        }
        return sortTypeEnum;
    }
}
