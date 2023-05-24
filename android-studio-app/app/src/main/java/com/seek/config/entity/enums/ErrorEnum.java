package com.seek.config.entity.enums;

import android.content.Context;

import com.ble.blescansdk.ble.BleSdkManager;
import com.seek.config.R;

import java.util.Objects;

public enum ErrorEnum {

    /**
     * 初始化失败 上下文不存在
     */
    CONTEXT_NULL(R.string.init_error_context_is_null, 1001, "init error"),
    /**
     * 蓝牙未打开
     */
    BLUETOOTH_NOT_OPEN(R.string.ble_error_bluetooth_not_open, 1002, "bluetooth is not open"),
    /**
     * 设备不存在
     */
    DEVICE_NOT_EXISTS(R.string.device_not_exists, 1003, "device does not exist"),

    /**
     * 蓝牙已连接
     */
    BLUETOOTH_ALREADY_CONNECTED(R.string.bluetooth_already_connected, 1004, "bluetooth already connected"),

    /**
     * 蓝牙连接超时
     */
    BLUETOOTH_CONNECT_TIMEOUT(R.string.bluetooth_connect_timeout, 1005, "bluetooth connect timeout"),

    /**
     * 蓝牙连接错误
     */
    BLUETOOTH_CONNECT_ERROR(R.string.bluetooth_connect_error, 1006, "bluetooth connect error"),

    /**
     * 蓝牙适配器不可用
     */
    BLUETOOTH_ADAPTER_NOT_AVAILABLE(R.string.bluetooth_adapter_not_available, 1007, "bluetoothAdapter not available"),

    /**
     * 设备地址无效
     */
    THE_DEVICE_ADDRESS_IS_INVALID(R.string.the_device_address_is_invalid, 1008, "the device address is invalid"),

    /**
     * 无法获取粗略信息
     */
    ACCESS_COARSE_LOCATION_NOT_EXIST(R.string.permission_error_access_coarse_location, 1009, "access coarse location not exist"),

    /**
     * gps获取位置信息
     */
    LOCATION_INFO_SWITCH_NOT_OPEN(R.string.location_info_switch_not_open, 1010, "位置信息开关未打开"),

    /**
     * 未初始化uuid
     */
    NOT_INIT_UUID(R.string.not_init_uuid, 1011, "未初始化UUID"),

    /**
     * 写入参数错误
     */
    WRITE_PARAMS_ERROR(R.string.params_error,1014,"参数错误"),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(R.string.unknown_error, -1, "unknown error"),

    ;

    private final int code;
    private final int errorCode;
    private final String desc;

    ErrorEnum(int code, int errorCode, String desc) {
        this.code = code;
        this.errorCode = errorCode;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDesc() {
        return desc;
    }

    public static String getFailMessage(int code) {
        Context context = BleSdkManager.getContext();
        if (Objects.isNull(context)) {
            return CONTEXT_NULL.getDesc();
        }
        for (ErrorEnum errorStatusEnum : ErrorEnum.values()) {
            if (errorStatusEnum.getErrorCode() == code) {
                return context.getString(errorStatusEnum.getCode());
            }
        }
        return context.getString(UNKNOWN_ERROR.getCode());
    }
}
