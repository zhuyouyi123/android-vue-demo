/********************************************************************************************************
 * @file Command.java
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
package com.seekcy.otaupgrade.ble;

import com.seekcy.otaupgrade.util.Arrays;

import java.util.UUID;

public class Command {

    public UUID serviceUUID;
    public UUID characteristicUUID;
    public UUID descriptorUUID;
    public CommandType type;
    public byte[] data;
    public Object tag;
    public int delay;
    public int mtu;

    public Command() {
        this(null, null, CommandType.WRITE);
    }

    public Command(UUID serviceUUID, UUID characteristicUUID, CommandType type) {
        this(serviceUUID, characteristicUUID, type, null);
    }

    public Command(UUID serviceUUID, UUID characteristicUUID, CommandType type,
                   byte[] data) {
        this(serviceUUID, characteristicUUID, type, data, null);
    }

    public Command(UUID serviceUUID, UUID characteristicUUID, CommandType type,
                   byte[] data, Object tag) {

        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.type = type;
        this.data = data;
        this.tag = tag;
    }

    public static Command newInstance() {
        return new Command();
    }

    public void clear() {
        this.serviceUUID = null;
        this.characteristicUUID = null;
        this.descriptorUUID = null;
        this.data = null;
    }

    @Override
    public String toString() {
        String d = "";

        if (data != null)
            d = Arrays.bytesToHexString(this.data, "");

        return "{ tag : " + this.tag + ", type : " + this.type
                + " CHARACTERISTIC_UUID :" + characteristicUUID.toString() + " data: " + d + " delay :" + delay + "}";
    }

    public enum CommandType {
        READ, READ_DESCRIPTOR, WRITE, WRITE_NO_RESPONSE, WRITE_DESCRIPTOR, ENABLE_NOTIFY, DISABLE_NOTIFY, REQUEST_MTU
    }

    public interface Callback {

        void success(Peripheral peripheral, Command command, Object obj);

        void error(Peripheral peripheral, Command command, String errorMsg);

        boolean timeout(Peripheral peripheral, Command command);
    }
}
