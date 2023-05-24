package com.ble.blescansdk.ble.enums;

import com.ble.blescansdk.ble.utils.StringUtils;

public enum EddystoneUrlPrefixEnum {
    PREFIX_0("http://www.", "00"),
    PREFIX_1("https://www.", "01"),
    PREFIX_2("http://", "02"),
    PREFIX_3("https://", "03"),
    ;

    private final String key;
    private final String code;

    EddystoneUrlPrefixEnum(String key, String value) {
        this.key = key;
        this.code = value;
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public static String getByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return PREFIX_0.getCode();
        }

        EddystoneUrlPrefixEnum urlPrefixEnum = null;

        for (EddystoneUrlPrefixEnum value : values()) {
            if (key.equals(value.getKey())) {
                urlPrefixEnum = value;
                break;
            }
        }

        if (null == urlPrefixEnum) {
            return PREFIX_0.getCode();
        }
        return urlPrefixEnum.getCode();
    }

    public static String getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return PREFIX_0.getKey();
        }

        if (code.length() == 1) {
            code = "0" + code;
        }

        EddystoneUrlPrefixEnum urlPrefixEnum = null;

        for (EddystoneUrlPrefixEnum value : values()) {
            if (code.equals(value.getCode())) {
                urlPrefixEnum = value;
                break;
            }
        }

        if (null == urlPrefixEnum) {
            return PREFIX_0.getKey();
        }
        return urlPrefixEnum.getKey();
    }
}
