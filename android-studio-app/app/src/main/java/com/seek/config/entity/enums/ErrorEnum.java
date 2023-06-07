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
    WRITE_PARAMS_ERROR(R.string.params_error, 1014, "参数错误"),

    /**
     * 命令执行超时
     */
    COMMAND_EXECUTION_TIMEOUT(R.string.command_execution_timeout, 2001, "Command execution timeout"),

    /**
     * 设备连接失败
     */
    DEVICE_CONNECTING_FAILED(R.string.device_connecting_failed, 2002, "Device connection failed"),

    /**
     * 防篡改秘钥错误
     */
    SECRET_KEY_ERROR( R.string.secret_key_error,2003, "Anti tamper key error"),

    /**
     * 出厂信息读取失败
     */
    FACTORY_INFORMATION_READING_FAILED(R.string.factory_information_reading_failed, 2004, "Factory information reading failed"),

    /**
     * 读取通道信息失败
     */
    FAILED_TO_READ_CHANNEL_INFORMATION(R.string.failed_to_read_channel_information, 2005, "Failed to read channel information"),

    /**
     * 秘钥配置失败
     */
    KEY_CONFIGURATION_FAILED(R.string.key_configuration_failed, 2006, "Key configuration failed"),

    /**
     * 通道配置失败
     */
    CHANNEL_CONFIGURATION_FAILED(R.string.channel_configuration_failed, 2007, "Channel configuration failed"),

    /**
     * 通知开启失败
     */
    NOTIFICATION_OPENING_FAILED(R.string.notification_opening_failed, 2008, "Notification opening failed"),

    /**
     * 设备写入数据出错
     */
    DEVICE_WRITING_DATA_ERROR(R.string.device_writing_data_error, 2009, "Device writing data error"),

    /**
     * 部分通道无法设置iBeacon协议
     */
    NOT_SUPPORT_I_BEACON(R.string.not_support_i_beacon, 2010, "Some channels cannot set the iBeacon protocol"),

    /**
     * 当前设备不支持配置ACC协议
     */
    DEVICE_NOT_SUPPORT_ACC(R.string.device_not_support_acc, 2011, "The current device does not support configuring the ACC protocol"),

    /**
     * 设备不能只存在AOA协议
     */
    NOT_ONLY_EXIST_COREAIOT(R.string.not_only_exist_coreaiot, 2012, "The device cannot only have Coreaiot protocol"),

    /**
     * 至少有一条通道开启始终广播
     */
    AT_LEAST_ONE_ALWAYS_BROADCAST(R.string.at_least_one_always_broadcast, 2013, "Always broadcast when at least one channel is open"),

    /**
     * 广播内容错误
     */
    BROADCAST_CONTENT_REPEAT(R.string.broadcast_content_repeat, 2014, "Repeated broadcast content"),

    /**
     * 设备不能只存在AOA协议
     */
    AT_LEAST_ONE_CHANNEL_EXISTS(R.string.at_least_one_channel_exists, 2015, "At least one channel exists"),

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
