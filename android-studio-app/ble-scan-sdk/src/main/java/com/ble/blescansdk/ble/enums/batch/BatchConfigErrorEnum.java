package com.ble.blescansdk.ble.enums.batch;

public enum BatchConfigErrorEnum {

    /**
     * 命令执行超时
     */
    COMMAND_EXECUTION_TIMEOUT(2001),

    /**
     * 设备连接失败
     */
    DEVICE_CONNECTING_FAILED(2002),

    /**
     * 防篡改秘钥错误
     */
    SECRET_KEY_ERROR(2003),

    /**
     * 出厂信息读取失败
     */
    FACTORY_INFORMATION_READING_FAILED(2004),

    /**
     * 读取通道信息失败
     */
    FAILED_TO_READ_CHANNEL_INFORMATION(2005),

    /**
     * 秘钥配置失败
     */
    KEY_CONFIGURATION_FAILED(2006),

    /**
     * 通道配置失败
     */
    CHANNEL_CONFIGURATION_FAILED(2007),

    /**
     * 通知开启失败
     */
    NOTIFICATION_OPENING_FAILED(2008),

    /**
     * 设备写入数据出错
     */
    DEVICE_WRITING_DATA_ERROR(2009),

    /**
     * 部分通道无法设置iBeacon协议
     */
    NOT_SUPPORT_I_BEACON(2010),

    /**
     * 当前设备不支持配置ACC协议
     */
    DEVICE_NOT_SUPPORT_ACC(2011),

    /**
     * 设备不能只存在AOA协议
     */
    NOT_ONLY_EXIST_COREAIOT(2012),

    /**
     * 至少有一条通道开启始终广播
     */
    AT_LEAST_ONE_ALWAYS_BROADCAST(2013),

    ;

    /**
     * 错误编码
     */
    private final int errorCode;

    BatchConfigErrorEnum(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
