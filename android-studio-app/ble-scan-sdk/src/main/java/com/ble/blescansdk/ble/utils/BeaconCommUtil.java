package com.ble.blescansdk.ble.utils;

import com.ble.blescansdk.ble.enums.BeaconCommEnum;

public class BeaconCommUtil {

    public static String strToAscii(String str) {
        byte[] bytes = ProtocolUtil.hexStrToBytes(str);
        if (null == bytes) {
            return null;
        }
        return strToAscii(bytes);
    }

    public static String strToAscii(byte[] bytes) {
        StringBuilder sbu = new StringBuilder();
        for (byte aByte : bytes) {
            String s = Character.toString((char) aByte);
            sbu.append(s);
        }
        String string = sbu.toString();
        return replaceHeadAndTail(string);
    }

    public static String[] strToSplit(byte[] bytes) {
        String ascii = strToAscii(bytes);
        return ascii.split("_");
    }

    public static BeaconCommEnum getCommType(String hex) {
        return getCommType(ProtocolUtil.hexStrToBytes(hex));
    }

    public static BeaconCommEnum getCommType(byte[] bytes) {
        if (null == bytes || bytes.length < 4) {
            return null;
        }
        String ascii = BeaconCommUtil.strToAscii(bytes);
        String[] split = ascii.split("_");
        return BeaconCommEnum.getByCode(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static String replaceHeadAndTail(String hex) {
        if (hex.contains("@_")) {
            hex = hex.replaceAll("@_", "");
        }

        if (hex.contains("_!")) {
            hex = hex.replaceAll("_!", "");
        }
        return hex;
    }
}
