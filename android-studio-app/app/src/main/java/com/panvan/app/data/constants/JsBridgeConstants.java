package com.panvan.app.data.constants;

public class JsBridgeConstants {

    public static final int DEFAULT = 0;
    public static final int IN_PROGRESS = 1;
    public static final int SUCCESS = 2;
    public static final int FAILED = -1;

    /**
     * 摄像机弹窗显示
     */
    public static final String POPUP_SHOW = "POPUP_SHOW";

    // 连接中
    public static final int BINDING_STATUS_CONNECTING = 3001;

    // 连接成功
    public static final int BINDING_STATUS_CONNECTED = 3002;

    // 连接失败
    public static final int BINDING_STATUS_UN_CONNECTED = 3003;

    // 请求数据
    public static final int BINDING_STATUS_REQUEST_DATA = 3004;

    // 结束
    public static final int BINDING_STATUS_FINISHED = 3005;

    // 连接错误
    public static final int BINDING_STATUS_FAILED = 3006;

    public static final String DEVICE_BINDING_STATUS = "DEVICE_BINDING_STATUS";

    /**
     * 设备电量
     */
    public static final String DEVICE_BATTERY = "DEVICE_BATTERY";

    /**
     * 实时信息
     */
    public static final String DEVICE_REAL_TIME = "DEVICE_REAL_TIME";

    /**
     * 固件升级
     */
    public static final String FIRMWARE_UPGRADE_KEY = "FIRMWARE_UPGRADE_KEY";
}
