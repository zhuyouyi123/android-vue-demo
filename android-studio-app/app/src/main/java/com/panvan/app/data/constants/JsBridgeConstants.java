package com.panvan.app.data.constants;

public class JsBridgeConstants {

    public static final int DEFAULT = 0;
    public static final int IN_PROGRESS = 1;
    public static final int SUCCESS = 2;
    public static final int FAILED = -1;

    /**
     * 摄像机弹窗显示
     */
    public static final String CAMERA_POPUP_SHOW="CAMERA_POPUP_SHOW";

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
     * 步数历史数据
     */
    public static final String STEP_HISTORY_DATA = "STEP_HISTORY_DATA";
    /**
     * 卡路里
     */
    public static final String CALORIE_HISTORY_DATA = "CALORIE_HISTORY_DATA";

}
