/********************************************************************************************************
 * @file OtaSetting.java
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

import com.seekcy.otaupgrade.ble.UuidInfo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class OtaSetting implements Serializable {


    /**
     * selected protocol
     */
    public OtaProtocol protocol = OtaProtocol.Legacy;

    /**
     * selected firmware data
     */
    public String firmwarePath;


    /**
     * need to check firmware crc
     */
    public boolean checkFirmwareCrc = false;

    /**
     * selected serviceUUID for OTA
     */
    public UUID serviceUUID = UuidInfo.OTA_SERVICE_UUID;

    /**
     * selected characteristicUUID for OTA
     */
    public UUID characteristicUUID = UuidInfo.OTA_CHARACTERISTIC_UUID;

    /**
     * read interval: read every [x] write packets
     * if value <= 0, no read check will be sent
     */
    public int readInterval = 0;

    /**
     * PDU length used in extend protocol, should be 16 * n (1~15)
     */
    public int pduLength = 16;

    /**
     * version compare used in extend protocol
     */
    public boolean versionCompare = false;

    /**
     * selected firmware bin version
     */
    public byte[] firmwareVersion;


    /**
     * OTA flow timeout
     * default 5 minutes
     */
    public int timeout = 5 * 60 * 1000;


    public boolean sendOTAVersion = true;

    /**
     * send firmware index ?
     */
    public boolean sendFwIndex = false;

    public byte fwIndex = 0x01;

    @Override
    public String toString() {
        return "OtaSetting{" +
                "protocol=" + protocol +
                ", firmwarePath='" + firmwarePath + '\'' +
                ", checkFirmwareCrc=" + checkFirmwareCrc +
                ", serviceUUID=" + serviceUUID +
                ", characteristicUUID=" + characteristicUUID +
                ", readInterval=" + readInterval +
                ", pduLength=" + pduLength +
                ", versionCompare=" + versionCompare +
                ", firmwareVersion=" + Arrays.toString(firmwareVersion) +
                ", timeout=" + timeout +
                ", sendOTAVersion=" + sendOTAVersion +
                ", sendFwIndex=" + sendFwIndex +
                ", fwIndex=" + fwIndex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtaSetting setting = (OtaSetting) o;
        return checkFirmwareCrc == setting.checkFirmwareCrc &&
                readInterval == setting.readInterval &&
                pduLength == setting.pduLength &&
                versionCompare == setting.versionCompare &&
                timeout == setting.timeout &&
                sendOTAVersion == setting.sendOTAVersion &&
                sendFwIndex == setting.sendFwIndex &&
                fwIndex == setting.fwIndex &&
                protocol == setting.protocol &&
                Objects.equals(firmwarePath, setting.firmwarePath) &&
                Objects.equals(serviceUUID, setting.serviceUUID) &&
                Objects.equals(characteristicUUID, setting.characteristicUUID) &&
                Arrays.equals(firmwareVersion, setting.firmwareVersion);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(protocol, firmwarePath, checkFirmwareCrc, serviceUUID, characteristicUUID, readInterval, pduLength, versionCompare, timeout, sendOTAVersion, sendFwIndex, fwIndex);
        result = 31 * result + Arrays.hashCode(firmwareVersion);
        return result;
    }

    public String getFirmwarePath() {
        return firmwarePath;
    }
}
