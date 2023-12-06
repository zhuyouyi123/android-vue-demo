package com.panvan.app.utils;

public class StringUtils {



    public static String formatBleAddress(String macAddress) {

        macAddress = macAddress.toUpperCase();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < macAddress.length(); i += 2) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(macAddress.substring(i, i + 2));
        }
        return sb.toString();
    }

    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }



}
