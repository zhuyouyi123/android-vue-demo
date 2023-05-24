package com.ble.blescansdk.ble.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

public enum BroadcastPowerEnum {

    LEVEL_1(1, -19.5),
    LEVEL_2(2, -13.5),
    LEVEL_3(3, -10),
    LEVEL_4(4, -7),
    LEVEL_5(5, -5),
    LEVEL_6(6, -3.5),
    LEVEL_7(7, -2),
    LEVEL_8(8, -1),
    LEVEL_9(9, 0),
    LEVEL_10(10, 1),
    LEVEL_11(11, 1.5),
    LEVEL_12(12, 2.5),
    ;

    private final int level;
    private final double power;

    BroadcastPowerEnum(int level, double power) {
        this.level = level;
        this.power = power;
    }

    public int getLevel() {
        return level;
    }

    public double getPower() {
        return power;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static BroadcastPowerEnum getByPower(double power) {
        return Arrays.stream(values()).filter(f -> f.getPower() == power)
                .findFirst()
                .orElse(LEVEL_9);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static BroadcastPowerEnum getByLevel(int level) {
        return Arrays.stream(values()).filter(f -> f.getLevel() == level)
                .findFirst()
                .orElse(LEVEL_9);
    }
}
