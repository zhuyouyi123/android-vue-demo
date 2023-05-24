package com.ble.blescansdk.ble.enums;

public enum ErrorStatusEnum {

    /**
     * 初始化失败 上下文不存在
     */
    CONTEXT_NULL(1001),
    /**
     * 蓝牙未打开
     */
    BLUETOOTH_NOT_OPEN(1002),
    /**
     * 设备不存在
     */
    DEVICE_NOT_EXISTS(1003),

    /**
     * 蓝牙已连接
     */
    BLUETOOTH_ALREADY_CONNECTED(1004),

    /**
     * 蓝牙连接超时
     */
    BLUETOOTH_CONNECT_TIMEOUT(1005),

    /**
     * 蓝牙连接错误
     */
    BLUETOOTH_CONNECT_ERROR(1006),

    /**
     * 蓝牙适配器不可用
     */
    BLUETOOTH_ADAPTER_NOT_AVAILABLE(1007),

    /**
     * 设备地址无效
     */
    THE_DEVICE_ADDRESS_IS_INVALID(1008),

    /**
     * 无法获取粗略信息
     */
    ACCESS_COARSE_LOCATION_NOT_EXIST(1009),

    /**
     * gps获取位置信息
     */
    LOCATION_INFO_SWITCH_NOT_OPEN(1010),

    /**
     * 未初始化uuid
     */
    NOT_INIT_UUID(1011),

    /**
     * uuid 不存在
     */
    NOT_EXIST_UUID(1012),

    /**
     * 读取失败
     */
    READ_ERROR(1013),

    /**
     * 写入参数错误
     */
    WRITE_PARAMS_ERROR(1014),

    /**
     *秘钥错误
     */
    SECRET_KEY_ERROR(1015),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(-1),

    ;

    private final int errorCode;

    ErrorStatusEnum(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
