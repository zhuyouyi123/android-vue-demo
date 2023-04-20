package com.ble.blescansdk.ble.enums;

public enum SKYBeaconPowerEnum {
    /*
     * ! 配置时txPower的值 注意：不同版本的beacon对txPower有不同允许范围
     * 版本号（hardwareVersion-firmwareVersionMajor-firmwareVersionMinor）：
     * 5-1-1、5-2-0、3-1-0允许范围是:0,1,2,3,4,5,-3,-6,-9,-12,-15,-18,-21
     * 7-1-0允许范围时：0，－20 其余的版本允许范围是：0,-2,-4,-6,-8,-10 (单位dBm)
     */

    POWER_LEVEL_FALSE(100, "POWER_LEVEL_FALSE"),
    POWER_LEVEL_DEFAULT(0, "POWER_LEVEL_DEFAULT"),
    POWER_LEVEL_PLUS_5(5, "POWER_LEVEL_PLUS_5"),
    POWER_LEVEL_PLUS_4(4, "POWER_LEVEL_PLUS_4"),
    POWER_LEVEL_PLUS_3(3, "POWER_LEVEL_PLUS_3"),
    POWER_LEVEL_PLUS_2(2, "POWER_LEVEL_PLUS_2"),
    POWER_LEVEL_PLUS_1(1, "POWER_LEVEL_PLUS_1"),
    POWER_LEVEL_PLUS_0(0, "POWER_LEVEL_PLUS_0"),
    POWER_LEVEL_MINUS_2(-2, "POWER_LEVEL_MINUS_2"),
    POWER_LEVEL_MINUS_3(-3, "POWER_LEVEL_MINUS_3"),
    POWER_LEVEL_MINUS_4(-4, "POWER_LEVEL_MINUS_4"),
    POWER_LEVEL_MINUS_6(-6, "POWER_LEVEL_MINUS_6"),
    POWER_LEVEL_MINUS_8(-8, "POWER_LEVEL_MINUS_8"),
    POWER_LEVEL_MINUS_9(-9, "POWER_LEVEL_MINUS_9"),
    POWER_LEVEL_MINUS_10(-10, "POWER_LEVEL_MINUS_10"),
    POWER_LEVEL_MINUS_12(-12, "POWER_LEVEL_MINUS_12"),
    POWER_LEVEL_MINUS_15(-15, "POWER_LEVEL_MINUS_15"),
    POWER_LEVEL_MINUS_18(-18, "POWER_LEVEL_MINUS_18"),
    POWER_LEVEL_MINUS_20(-20, "POWER_LEVEL_MINUS_20"),
    POWER_LEVEL_MINUS_21(-21, "POWER_LEVEL_MINUS_21");

    /**
     * 定义私有变量
     */
    private final int eCode;

    private final String name;

    /**
     * 构造函数，枚举类型只能为私有
     *
     * @param eCode
     */
    SKYBeaconPowerEnum(int eCode, String name) {
        this.eCode = eCode;
        this.name = name;
    }

    public int geteCode() {
        return eCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(this.eCode);
    }

    public static SKYBeaconPowerEnum getPower(int power) {
        SKYBeaconPowerEnum result = SKYBeaconPowerEnum.POWER_LEVEL_FALSE;
        switch (power) {
            case 5:
                result = SKYBeaconPowerEnum.POWER_LEVEL_PLUS_5;
                break;
            case 4:
                result = SKYBeaconPowerEnum.POWER_LEVEL_PLUS_4;
                break;
            case 3:
                result = SKYBeaconPowerEnum.POWER_LEVEL_PLUS_3;
                break;
            case 2:
                result = SKYBeaconPowerEnum.POWER_LEVEL_PLUS_2;
                break;
            case 1:
                result = SKYBeaconPowerEnum.POWER_LEVEL_PLUS_1;
                break;
            case 0:
                result = SKYBeaconPowerEnum.POWER_LEVEL_PLUS_0;
                break;
            case -2:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_2;
                break;
            case -3:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_3;
                break;
            case -4:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_4;
                break;
            case -6:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_6;
                break;
            case -8:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_8;
                break;
            case -9:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_9;
                break;
            case -10:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_10;
                break;
            case -12:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_12;
                break;
            case -15:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_15;
                break;
            case -18:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_18;
                break;
            case -20:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_20;
                break;
            case -21:
                result = SKYBeaconPowerEnum.POWER_LEVEL_MINUS_21;
                break;
        }
        return result;
    }

    public static int getPower(SKYBeaconPowerEnum SKYBeaconPowerEnum) {
        int result = 0x7F;
        if (SKYBeaconPowerEnum == POWER_LEVEL_PLUS_5) {
            result = 5;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_PLUS_4) {
            result = 4;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_PLUS_3) {
            result = 3;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_PLUS_2) {
            result = 2;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_PLUS_1) {
            result = 1;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_PLUS_0) {
            result = 0;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_2) {
            result = -2;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_3) {
            result = -3;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_4) {
            result = -4;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_6) {
            result = -6;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_8) {
            result = -8;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_9) {
            result = -9;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_10) {
            result = -10;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_12) {
            result = -12;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_15) {
            result = -15;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_18) {
            result = -18;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_20) {
            result = -20;
        } else if (SKYBeaconPowerEnum == POWER_LEVEL_MINUS_21) {
            result = -21;
        }
        return result;
    }

    /**
     * 根据名称获取tx
     * @param name 名称
     * @return code
     */
    public static int getByName(String name) {
        int code = POWER_LEVEL_DEFAULT.eCode;
        for (SKYBeaconPowerEnum value : values()) {
            if (name.equals(value.getName())) {
                code = value.geteCode();
                break;
            }
        }
        return code;
    }
}
