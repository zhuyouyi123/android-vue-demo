package com.seek.config.entity.enums;

import com.seek.config.R;

public enum BatchConfigFailureEnum {

    /**
     * 命令执行超时
     */
    COMMAND_EXECUTION_TIMEOUT(2001, R.string.command_execution_timeout),

    /**
     * 设备连接失败
     */
    DEVICE_CONNECTING_FAILED(2002, R.string.device_connecting_failed),

    /**
     * 防篡改秘钥错误
     */
    SECRET_KEY_ERROR(2003, R.string.secret_key_error),

    /**
     * 出厂信息读取失败
     */
    FACTORY_INFORMATION_READING_FAILED(2004, R.string.factory_information_reading_failed),

    /**
     * 读取通道信息失败
     */
    FAILED_TO_READ_CHANNEL_INFORMATION(2005, R.string.failed_to_read_channel_information),

    /**
     * 秘钥配置失败
     */
    KEY_CONFIGURATION_FAILED(2006, R.string.key_configuration_failed),

    /**
     * 通道配置失败
     */
    CHANNEL_CONFIGURATION_FAILED(2007, R.string.channel_configuration_failed),

    /**
     * 通知开启失败
     */
    NOTIFICATION_OPENING_FAILED(2008, R.string.notification_opening_failed),

    /**
     * 设备写入数据出错
     */
    DEVICE_WRITING_DATA_ERROR(2009, R.string.device_writing_data_error),

    /**
     * 部分通道无法设置iBeacon协议
     */
    NOT_SUPPORT_I_BEACON(2010, R.string.not_support_i_beacon),

    /**
     * 当前设备不支持配置ACC协议
     */
    DEVICE_NOT_SUPPORT_ACC(2011, R.string.device_not_support_acc),

    /**
     * 设备不能只存在AOA协议
     */
    NOT_ONLY_EXIST_COREAIOT(2012, R.string.not_only_exist_coreaiot),

    /**
     * 至少有一条通道开启始终广播
     */
    AT_LEAST_ONE_ALWAYS_BROADCAST(2013, R.string.at_least_one_always_broadcast),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(-1, R.string.unknown_error),

    ;

    /**
     * 错误编码
     */
    private final int errorCode;

    private final int code;

    /**
     * 初始错误码
     */

    BatchConfigFailureEnum(int errorCode, int code) {
        this.errorCode = errorCode;
        this.code = code;
    }

    public int getErrorCode() {
        return errorCode;
    }


    public int getCode() {
        return code;
    }

    public static int getCodeByErrorCode(int errorCode) {
        for (BatchConfigFailureEnum value : values()) {
            if (value.getErrorCode() == errorCode) {
                return value.getCode();
            }
        }

        return UNKNOWN_ERROR.getCode();

    }

}
