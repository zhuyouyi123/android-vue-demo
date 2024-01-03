/********************************************************************************************************
 * @file OtaStatusCode.java
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
package com.seekcy.otaupgrade.foundation;

public class OtaStatusCode {

    /**
     * ota success
     */
    public static final int SUCCESS = 0x00;

    /**
     * ota started
     */
    public static final int STARTED = 0x01;


    /**
     * ota busy status when call {@link com.telink.ota.ble.OtaController#startOta(OtaSetting)}
     * at previous ota running
     */
    public static final int BUSY = 0x04;

    /**
     * ota params err
     */
    public static final int FAIL_PARAMS_ERR = 0x10;

    /**
     * connection interrupt when ota running
     */
    public static final int FAIL_CONNECTION_INTERRUPT = 0x11;



    /**
     * version compare err
     */
    public static final int FAIL_VERSION_RSP_ERROR = 0x12;

    /**
     * version compare err
     */
    public static final int FAIL_VERSION_COMPARE_ERR = 0x13;

    /**
     * send command err
     */
    public static final int FAIL_PACKET_SENT_ERR = 0x14;

    /**
     * send command timeout
     */
    public static final int FAIL_PACKET_SENT_TIMEOUT = 0x15;

    /**
     * ota flow timeout
     */
    public static final int FAIL_FLOW_TIMEOUT = 0x16;


    /**
     * not connected
     */
    public static final int FAIL_UNCONNECTED = 0x18;

    /**
     * service not found
     */
    public static final int FAIL_SERVICE_NOT_FOUND = 0x19;

    /**
     * service not found
     */
    public static final int FAIL_CHARACTERISTIC_NOT_FOUND = 0x1A;

    /**
     * check selected firmware bin error
     */
    public static final int FAIL_FIRMWARE_CHECK_ERR = 0x1B;

    /**
     * firmware version request timeout
     */
    public static final int FAIL_FW_VERSION_REQ_TIMEOUT = 0x1C;

    /**
     * received notification err
     */
    public static final int FAIL_OTA_RESULT_NOTIFICATION = 0x1D;

    /**
     * is ota failed
     */
    public static boolean isFailed(int code) {
        return code >= 0x10;
    }

    /**
     * @return is ota complete
     */
    public static boolean isComplete(int code) {
        return isFailed(code) || code == SUCCESS;
    }
}
