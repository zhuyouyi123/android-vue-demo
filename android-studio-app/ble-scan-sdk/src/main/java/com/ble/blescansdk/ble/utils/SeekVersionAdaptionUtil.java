package com.ble.blescansdk.ble.utils;

public class SeekVersionAdaptionUtil {

    /**
     * 硬件版本号
     *
     * @author frank
     *
     */
    public class HardwareVersion {
        static public final int HARDWARE_S1_VERSION = 0;
        static public final int HARDWARE_M0_VERSION = 1;
        static public final int HARDWARE_S1P_VERSION = 2;
        static public final int HARDWARE_M0L_VERSION = 3;
        static public final int HARDWARE_M1_VERSION = 4;
        static public final int HARDWARE_S1L_VERSION = 5;
        static public final int HARDWARE_K1L_VERSION = 6;
        static public final int HARDWARE_S1U_VERSION = 7;
        static public final int HARDWARE_M0U_VERSION = 8;
        static public final int HARDWARE_S2U_VERSION = 9;
        static public final int HARDWARE_T1U_VERSION = 18;
        static public final int HARDWARE_S2L_VERSION = 19;
        static public final int HARDWARE_LB6_VERSION = 0x70;
        static public final int HARDWARE_S4LA_VERSION = 20;
        static public final int HARDWARE_S1LA_VERSION = 21;
        static public final int HARDWARE_M0LA_VERSION = 22;
        static public final int HARDWARE_K1LA_VERSION = 23;
        static public final int HARDWARE_S3UEx2_VERSION = 24;
        static public final int HARDWARE_M1LA_V_Ex_VERSION = 25;
        static public final int HARDWARE_S6LA_Ex_1_VERSION = 26;
    }

    public static boolean isMultiIDsVersion(int hardwareVersion, int firmwareMajor) {
        boolean result = false;

        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S1_VERSION:
                if (firmwareMajor == 0x02) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M0L_VERSION:
                if (firmwareMajor == 0x03) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_S1L_VERSION:
                if (firmwareMajor == 0x02) {
                    result = true;
                }
            case HardwareVersion.HARDWARE_S1U_VERSION:
                if (firmwareMajor == 0x03) {
                    result = true;
                }
                break;
        }
        return result;
    }

    public static boolean isTempteratureSensorContain(int hardwareVersion, int firmwareMajor, int firmwareMinor) {
        boolean result = false;
        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_M0L_VERSION:
                if (firmwareMajor == 1 || firmwareMajor == 3) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_S1L_VERSION:
                if (firmwareMajor == 1) {
                    if (firmwareMinor >= 1) {
                        result = true;
                    }
                } else if (firmwareMajor == 2) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_K1L_VERSION:
                if (firmwareMajor == 1) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_S2L_VERSION:
                if (firmwareMajor == 1) {
                    if (firmwareMinor >= 1) {
                        result = true;
                    }
                }
                break;
            case HardwareVersion.HARDWARE_LB6_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_M1LA_V_Ex_VERSION:
                result = true;
                break;

            default:
                break;
        }
        return result;
    }

    public static boolean isLightSensorContain(int hardwareVersion, int firmwareMajor, int firmwareMinor) {
        boolean result = false;
        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_M0L_VERSION:
                // if (firmwareMajor == 1) {
                // result = true;
                // }
                break;
            case HardwareVersion.HARDWARE_LB6_VERSION:
                break;

            default:
                break;
        }
        return result;
    }

    public static boolean isLedContain(int hardwareVersion, int firmwareMajor, int firmwareMinor) {
        boolean result = false;
        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_M0_VERSION:
                if (firmwareMajor == 1) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M0L_VERSION:
                if (firmwareMajor == 1 || firmwareMajor == 3) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M0U_VERSION:
                if (firmwareMajor == 1) {
                    result = true;
                }
                break;

            default:
                break;
        }
        return result;
    }

    public static String getProductName(int hardwareVersion, int firmwareMajor, int firmwareMinor) {
        String result = "";
        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S1_VERSION:
                if (firmwareMajor == 1) {
                    result = "S1";
                } else if (firmwareMajor == 2) {
                    result = "S1-Multi";
                }
                break;
            case HardwareVersion.HARDWARE_M0_VERSION:
                result = "M0";
                break;
            case HardwareVersion.HARDWARE_S1P_VERSION:
                result = "S1P";
                break;
            case HardwareVersion.HARDWARE_M0L_VERSION:
                if (firmwareMajor == 1) {
                    result = "M0L";
                } else {
                    result = "M0L-Multi";
                }
                break;
            case HardwareVersion.HARDWARE_M1_VERSION:
                result = "M1";
                break;
            case HardwareVersion.HARDWARE_S1L_VERSION:
                if (firmwareMajor == 1) {
                    result = "S1L";
                } else if (firmwareMajor == 2) {
                    result = "S1L-Multi";
                }
                break;
            case HardwareVersion.HARDWARE_K1L_VERSION:
                result = "K1L";
                break;
            case HardwareVersion.HARDWARE_S1U_VERSION:
                result = "S1U";
                break;
            case HardwareVersion.HARDWARE_M0U_VERSION:
                result = "M0U";
                break;
            case HardwareVersion.HARDWARE_S2U_VERSION:
                result = "S2U";
                break;
            case HardwareVersion.HARDWARE_T1U_VERSION:
                result = "T1U";
                break;
            case HardwareVersion.HARDWARE_S2L_VERSION:
                result = "S2L";
                break;
            case HardwareVersion.HARDWARE_S4LA_VERSION:
                if (firmwareMajor == 5) {
                    result = "S4LA_RTC";
                } else {
                    result = "S4LA";
                }
                break;
            case HardwareVersion.HARDWARE_S1LA_VERSION:
                if (firmwareMajor == 5) {
                    result = "S1LA_RTC";
                } else {
                    result = "S1LA";
                }
                break;
            case HardwareVersion.HARDWARE_M0LA_VERSION:
                if (firmwareMajor == 5) {
                    result = "M0LA_RTC";
                } else {
                    result = "M0LA";
                }
                break;
            case HardwareVersion.HARDWARE_K1LA_VERSION:
                if (firmwareMajor == 5) {
                    result = "K1LA_RTC";
                } else {
                    result = "K1LA";
                }
                break;
            case HardwareVersion.HARDWARE_S3UEx2_VERSION:
                result = "S3U_Ex2";
                break;
            case HardwareVersion.HARDWARE_M1LA_V_Ex_VERSION:
                result = "M1LA_V_Ex";
                break;
            case HardwareVersion.HARDWARE_S6LA_Ex_1_VERSION:
                result = "S6LA_Ex_1";
                break;
            default:
                break;
        }
        return result;
    }

    public static boolean isIntervalFiveSecondProtocol(int hardwareVersion, int softwareMajorVersion) {
        boolean result = false;

        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S1_VERSION:
            case HardwareVersion.HARDWARE_M0_VERSION:
            case HardwareVersion.HARDWARE_S1P_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0L_VERSION:
            case HardwareVersion.HARDWARE_S1L_VERSION:
                if (softwareMajorVersion == 3) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M1_VERSION:
                break;
            case HardwareVersion.HARDWARE_K1L_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1U_VERSION:
            case HardwareVersion.HARDWARE_M0LA_VERSION:
            case HardwareVersion.HARDWARE_K1LA_VERSION:
            case HardwareVersion.HARDWARE_S1LA_VERSION:
            case HardwareVersion.HARDWARE_S4LA_VERSION:
            case HardwareVersion.HARDWARE_S2L_VERSION:
            case HardwareVersion.HARDWARE_T1U_VERSION:
            case HardwareVersion.HARDWARE_S2U_VERSION:
            case HardwareVersion.HARDWARE_S3UEx2_VERSION:
            case HardwareVersion.HARDWARE_M0U_VERSION:
            case HardwareVersion.HARDWARE_M1LA_V_Ex_VERSION:
            case HardwareVersion.HARDWARE_S6LA_Ex_1_VERSION:
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    // CC2640 支持OAD
    public static boolean isLA(int hardwareVersion, int softwareMajorVersion, int softwareMinorVersion) {
        boolean result = false;

        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S4LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_S1LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_M0LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_K1LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_S3UEx2_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_M1LA_V_Ex_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_S6LA_Ex_1_VERSION:
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    // CC2640
    public static boolean isLARTC(int hardwareVersion, int softwareMajorVersion, int softwareMinorVersion) {
        boolean result = false;

        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S4LA_VERSION:
                if (softwareMajorVersion == 5) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_S1LA_VERSION:
                if (softwareMajorVersion == 5) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M0LA_VERSION:
                if (softwareMajorVersion == 5) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_K1LA_VERSION:
                if (softwareMajorVersion == 5) {
                    result = true;
                }
                break;
            default:
                break;
        }
        return result;
    }

    static public boolean isEncryptedVersion2(int hardwareVersion, int softwareMajorVersion, int softwareMinorVersion) {
        boolean result = false;

        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S1_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1P_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0L_VERSION:
                if (softwareMajorVersion == 3) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M1_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1L_VERSION:
                if (softwareMajorVersion == 3) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_K1L_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1U_VERSION:
                if (softwareMajorVersion == 3) {
                    result = true;
                }
                break;
            case HardwareVersion.HARDWARE_M0U_VERSION:
                break;
            case HardwareVersion.HARDWARE_S2U_VERSION:
                break;
            case HardwareVersion.HARDWARE_T1U_VERSION:
                break;
            case HardwareVersion.HARDWARE_S2L_VERSION:
                break;
            case HardwareVersion.HARDWARE_S4LA_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1LA_VERSION:
                break;
            case HardwareVersion.HARDWARE_K1LA_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0LA_VERSION:
                break;
            case HardwareVersion.HARDWARE_S3UEx2_VERSION:
                break;
            default:
                break;
        }
        return result;
    }

    static public boolean isEncryptedVersion3(int hardwareVersion, int softwareMajorVersion, int softwareMinorVersion) {
        boolean result = false;

        switch (hardwareVersion) {
            case HardwareVersion.HARDWARE_S1_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1P_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0L_VERSION:
                break;
            case HardwareVersion.HARDWARE_M1_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1L_VERSION:
                break;
            case HardwareVersion.HARDWARE_K1L_VERSION:
                break;
            case HardwareVersion.HARDWARE_S1U_VERSION:
                break;
            case HardwareVersion.HARDWARE_M0U_VERSION:
                break;
            case HardwareVersion.HARDWARE_S2U_VERSION:
                break;
            case HardwareVersion.HARDWARE_T1U_VERSION:
                break;
            case HardwareVersion.HARDWARE_S2L_VERSION:
                break;
            case HardwareVersion.HARDWARE_S4LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_S1LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_M0LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_K1LA_VERSION:
                result = true;
                break;
            case HardwareVersion.HARDWARE_S3UEx2_VERSION:
                break;
            default:
                break;
        }
        return result;
    }
}
