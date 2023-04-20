package com.ble.blescansdk.ble.enums;

/**
 * 蓝牙扫描等级
 */
public enum BleScanLevelEnum {

    /**
     * 在低功耗模式下执行蓝牙 LE 扫描。这是默认扫描模式，因为它消耗的电量最少。如果扫描应用程序不在前台，则强制执行此模式。
     */
    SCAN_MODE_LOW_POWER(0),
    /**
     * 在平衡功率模式下执行蓝牙 LE 扫描。扫描结果以在扫描频率和功耗之间提供良好折衷的速率返回。
     */
    SCAN_MODE_BALANCED(1),

    /**
     * 使用最高占空比扫描。建议仅在应用程序在前台运行时使用此模式。
     */
    SCAN_MODE_LOW_LATENCY(2),
    ;

    private final int level;

    BleScanLevelEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }


    public static BleScanLevelEnum getByLevel(int level) {
        BleScanLevelEnum levelEnum = null;
        for (BleScanLevelEnum value : values()) {
            if (value.getLevel() == level) {
                levelEnum = value;
                break;
            }
        }
        return levelEnum;
    }
}
