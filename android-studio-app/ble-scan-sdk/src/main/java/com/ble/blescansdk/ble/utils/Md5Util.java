package com.ble.blescansdk.ble.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Md5Util {
    // MD5変換
    public static String md5(String str) {
        if (str != null && !str.equals("")) {
            str = str.toLowerCase();
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                byte[] md5Byte = md5.digest(str.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte b : md5Byte) {
                    sb.append(HEX[(b & 0xff) / 16]);
                    sb.append(HEX[(b & 0xff) % 16]);
                }
                str = sb.toString();
            } catch (Exception ignored) {
            }
        }
        return str;
    }
}
