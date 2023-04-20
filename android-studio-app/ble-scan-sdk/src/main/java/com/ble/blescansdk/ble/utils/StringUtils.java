package com.ble.blescansdk.ble.utils;

/**
 * 判断字符串是否为空
 * 工具类
 * 2021年11月10日10:57:25
 */
public class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
