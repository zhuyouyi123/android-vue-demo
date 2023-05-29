package com.seek.config.utils;

import android.content.Context;

import com.seek.config.Config;
import com.seek.config.R;

public class I18nUtil {
    /**
     * 提示
     */
    public static final int tips = R.string.tips;
    public static final int sure = R.string.sure;
    public static final int cancel = R.string.cancel;

    /**
     * 打开位置信息开关
     */
    public static final int OPEN_LOCATION_INFO_SWITCH = R.string.open_location_info_switch;

    /**
     * 设备地址格式错误
     */
    public static final int DEVICE_ADDRESS_FORMAT_ERROR = R.string.device_address_format_error;

    /**
     * 设备连接成功
     */
    public static final int CONNECT_SUCCESS = R.string.ble_connect_success;

    /**
     * 配置通道出错
     */
    public static final int CONFIG_CHANNEL_ERROR = R.string.config_channel_error;


    public static final int CONFIG_PARAMS_ERROR = R.string.config_params_error;

    public static final int REQUEST_ERROR = R.string.request_error;

    public static final int PRESS_AGAIN_TO_EXIT = R.string.press_again_to_exit;




    public static String getMessage(int code) {
        Context mainContext = Config.mainContext;
        if (mainContext == null) {
            return "";
        }

        return mainContext.getResources().getString(code);
    }

}
