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


    public static String convertString(String str) {
        String s = "";
        if (isBlank(str)) {
            return s;
        }

        char[] chars = str.toCharArray();

        StringBuilder strBuilder = new StringBuilder(s);
        for (int i = 0; i < chars.length; i++) {
            strBuilder.append(chars[i]);
            if (i == 0) {
                continue;
            }
            if (i == chars.length - 1) {
                break;
            }
            if ((i + 1) % 2 == 0) {
                strBuilder.append(".");
            }
        }
        return strBuilder.toString();
    }
}
