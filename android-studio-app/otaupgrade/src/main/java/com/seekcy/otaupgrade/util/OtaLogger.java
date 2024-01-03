/********************************************************************************************************
 * @file OtaLogger.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2019
 *
 * @par Copyright (c) 2019, Telink Semiconductor (Shanghai) Co., Ltd. ("TELINK")
 *
 *          Licensed under the Apache License, Version 2.0 (the "License");
 *          you may not use this file except in compliance with the License.
 *          You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *          Unless required by applicable law or agreed to in writing, software
 *          distributed under the License is distributed on an "AS IS" BASIS,
 *          WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *          See the License for the specific language governing permissions and
 *          limitations under the License.
 *******************************************************************************************************/
package com.seekcy.otaupgrade.util;

import android.util.Log;

public class OtaLogger {

    public static String TAG = "Telink-OTA";

    public static boolean ENABLE = true;

    public static boolean isLoggable(int level) {
        if (ENABLE)
            return Log.isLoggable(TAG, level);
        return false;
    }

    public static String getStackTraceString(Throwable th) {
        if (ENABLE)
            return Log.getStackTraceString(th);
        return th.getMessage();
    }

    public static int println(int level, String msg) {
        if (ENABLE)
            return Log.println(level, TAG, msg);
        return 0;
    }

    public static int v(String msg) {
        if (ENABLE)
            return Log.v(TAG, msg);
        return 0;
    }

    public static int v(String msg, Throwable th) {
        if (ENABLE)
            return Log.v(TAG, msg, th);
        return 0;
    }

    public static int d(String msg) {
        if (ENABLE)
            return Log.d(TAG, msg);
        return 0;
    }

    public static int d(String msg, Throwable th) {
        if (ENABLE)
            return Log.d(TAG, msg, th);
        return 0;
    }

    public static int i(String msg) {
        if (ENABLE)
            return Log.i(TAG, msg);
        return 0;
    }

    public static int i(String msg, Throwable th) {
        if (ENABLE)
            return Log.i(TAG, msg, th);
        return 0;
    }

    public static int w(String msg) {
        if (ENABLE)
            return Log.w(TAG, msg);
        return 0;
    }

    public static int w(String msg, Throwable th) {
        if (ENABLE)
            return Log.w(TAG, msg, th);
        return 0;
    }

    public static int w(Throwable th) {
        if (ENABLE)
            return Log.w(TAG, th);
        return 0;
    }

    public static int e(String msg) {
        if (ENABLE)
            return Log.w(TAG, msg);
        return 0;
    }

    public static int e(String msg, Throwable th) {
        if (ENABLE)
            return Log.e(TAG, msg, th);
        return 0;
    }
}
