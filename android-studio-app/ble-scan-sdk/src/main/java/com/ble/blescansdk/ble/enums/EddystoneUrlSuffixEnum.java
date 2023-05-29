package com.ble.blescansdk.ble.enums;

import com.ble.blescansdk.ble.utils.StringUtils;

public enum EddystoneUrlSuffixEnum {

    SUFFIX_0(".com/", "00"),
    SUFFIX_1(".org/", "01"),
    SUFFIX_2(".edu/", "02"),
    SUFFIX_3(".net/", "03"),
    SUFFIX_4(".info/", "04"),
    SUFFIX_5(".biz/", "05"),
    SUFFIX_6(".gov/", "06"),
    SUFFIX_7(".com", "07"),
    SUFFIX_8(".org", "08"),
    SUFFIX_9(".edu", "09"),
    SUFFIX_0A(".net", "0A"),
    SUFFIX_0B(".info", "0B"),
    SUFFIX_0C(".biz", "0C"),
    SUFFIX_0D(".gov", "0D"),
    ;

    private final String key;
    private final String code;

    EddystoneUrlSuffixEnum(String key, String value) {
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
            return SUFFIX_0.getCode();
        }

        EddystoneUrlSuffixEnum urlSuffixEnum = null;

        for (EddystoneUrlSuffixEnum value : values()) {
            if (key.equals(value.getKey())) {
                urlSuffixEnum = value;
                break;
            }
        }

        if (null == urlSuffixEnum) {
            return SUFFIX_0.getCode();
        }
        return urlSuffixEnum.getCode();
    }

    public static String getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return SUFFIX_0.getKey();
        }

        code = code.toUpperCase();

        if (code.length() == 1) {
            code = "0" + code;
        }

        EddystoneUrlSuffixEnum urlPrefixEnum = null;

        for (EddystoneUrlSuffixEnum value : values()) {
            if (code.equals(value.getCode())) {
                urlPrefixEnum = value;
                break;
            }
        }

        if (null == urlPrefixEnum) {
            return SUFFIX_0.getKey();
        }
        return urlPrefixEnum.getKey();
    }
}
