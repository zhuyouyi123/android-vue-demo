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
    public static final int openLocationInfoSwitch = R.string.open_location_info_switch;



    public static String getMessage(int code) {
        Context mainContext = Config.mainContext;
        if (mainContext == null) {
            return "";
        }

        return mainContext.getResources().getString(code);
    }

}
