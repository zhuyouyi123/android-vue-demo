package com.seekcy.bracelet.utils;

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


    public static String incrementMacAddress(String macAddress) {
        String[] parts = macAddress.split(":");
        int lastPart = Integer.parseInt(parts[5], 16); // Convert the last part to decimal
        lastPart = (lastPart + 1) & 0xFF; // Increment and ensure it's within a byte

        // Convert back to hexadecimal and format as a MAC address
        parts[5] = String.format("%02X", lastPart);

        // Join the parts back together
        return String.join(":", parts);
    }



}
