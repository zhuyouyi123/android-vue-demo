package com.ble.blescansdk.ble.enums;

public enum SKYDeviceTypeEnum {
    DEVICE_TYPE_IBEACON(1), DEVICE_TYPE_FOOT_RING(2), DEVICE_TYPE_NULL(3), BLE_DEVICE_TYPE_SPEED_BEACON(4);
    // 定义私有变量
    private int eCode;

    /**
     * 构造函数，枚举类型只能为私有
     *
     * @param eCode
     */
    private SKYDeviceTypeEnum(int eCode) {
        this.eCode = eCode;
    }

    public static SKYDeviceTypeEnum valueOf(int value) { // 手写的从int到enum的转换函数
        switch (value) {
            case 1:
                return DEVICE_TYPE_IBEACON;
            case 2:
                return DEVICE_TYPE_FOOT_RING;
            case 3:
                return DEVICE_TYPE_NULL;
            case 4:
                return BLE_DEVICE_TYPE_SPEED_BEACON;
            default:
                return null;
        }
    }

    /**
     * 手写的从enum到int的转换函数
     *
     * @param deviceType 设备类型
     * @return 类型
     */
    public static int valueOf(SKYDeviceTypeEnum deviceType) {
        if (deviceType == DEVICE_TYPE_IBEACON) {
            return 1;
        } else if (deviceType == DEVICE_TYPE_FOOT_RING) {
            return 2;
        } else if (deviceType == DEVICE_TYPE_NULL) {
            return 3;
        } else if (deviceType == BLE_DEVICE_TYPE_SPEED_BEACON) {
            return 4;
        }
        return 3;
    }

    public int value() {
        return this.eCode;
    }
}
