package com.seek.config.utils;

import com.ble.blescansdk.ble.utils.StringUtils;

public class RegexUtil {

    private static final String MAC_REGEX = "(([A-F0-9]{2}:){5})[A-F0-9]{2}";

    public static boolean macRegexMatch(String mac) {
        if (StringUtils.isBlank(mac)) {
            return false;
        }
        return mac.matches(MAC_REGEX);
    }
}
