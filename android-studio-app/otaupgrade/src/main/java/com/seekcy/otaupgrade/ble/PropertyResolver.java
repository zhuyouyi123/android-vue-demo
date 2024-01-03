/********************************************************************************************************
 * @file PropertyResolver.java
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

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/1.
 */

public class PropertyResolver {
    public final static String READ = "read";
    public final static String WRITE = "write";
    public final static String NOTIFY = "notify";
    public final static String INDICATE = "indicate";
    public final static String WRITE_NO_RESPONSE = "write_no_response";

    private Map<String, Boolean> properties;
    private int mProp;

    public PropertyResolver(int prop) {
        this.mProp = prop;
        properties = new HashMap<>();
        properties.put(READ, (prop & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
        properties.put(WRITE, (prop & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0);
        properties.put(NOTIFY, (prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0);
        properties.put(INDICATE, (prop & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0);
        properties.put(WRITE_NO_RESPONSE, (prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0);
    }

    public boolean contains(String key) {
        return properties.containsKey(key) && properties.get(key);
    }

    public String getGattCharacteristicPropDesc() {
        String desc = " ";
        if ((mProp & BluetoothGattCharacteristic.PROPERTY_READ) != 0)
            desc = desc + "read ";
        if ((mProp & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0)
            desc = desc + "write ";
        if ((mProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
            desc = desc + "notify ";
        if ((mProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0)
            desc = desc + "indicate ";
        if ((mProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0)
            desc = desc + "write_no_response ";
        return desc;
    }
}
