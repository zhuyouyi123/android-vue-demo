package com.ble.blescansdk.ble.utils;

public class EddystoneBeaconUrlUtil {

    private static final String PREFIX_0 = "http://www.";
    private static final String PREFIX_1 = "https://www.";
    private static final String PREFIX_2 = "http://";
    private static final String PREFIX_3 = "https://";


    private static final String SUFFIX_0 = ".com/";
    private static final String SUFFIX_1 = ".org/";
    private static final String SUFFIX_2 = ".edu/";
    private static final String SUFFIX_3 = ".net/";
    private static final String SUFFIX_4 = ".info/";
    private static final String SUFFIX_5 = ".biz/";
    private static final String SUFFIX_6 = ".gov/";
    private static final String SUFFIX_7 = ".com";
    private static final String SUFFIX_8 = ".org";
    private static final String SUFFIX_9 = ".edu";
    private static final String SUFFIX_0A = ".net";
    private static final String SUFFIX_0B = ".info";
    private static final String SUFFIX_0C = ".biz";
    private static final String SUFFIX_0D = ".gov";


    public static String getPrefix(String key) {
        switch (key) {
            case "00":
                return PREFIX_0;
            case "01":
                return PREFIX_1;
            case "02":
                return PREFIX_2;
            case "03":
                return PREFIX_3;
            default:
                return "";
        }
    }

    public static String getSuffix(String key) {
        switch (key) {
            case "00":
                return SUFFIX_0;
            case "01":
                return SUFFIX_1;
            case "02":
                return SUFFIX_2;
            case "03":
                return SUFFIX_3;
            case "04":
                return SUFFIX_4;
            case "05":
                return SUFFIX_5;
            case "06":
                return SUFFIX_6;
            case "07":
                return SUFFIX_7;
            case "08":
                return SUFFIX_8;
            case "09":
                return SUFFIX_9;
            case "0A":
                return SUFFIX_0A;
            case "0B":
                return SUFFIX_0B;
            case "0C":
                return SUFFIX_0C;
            case "0D":
                return SUFFIX_0D;
            default:
                return "";
        }
    }
}
