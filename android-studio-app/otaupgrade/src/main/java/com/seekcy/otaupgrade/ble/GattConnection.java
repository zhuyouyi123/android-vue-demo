/********************************************************************************************************
 * @file GattConnection.java
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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.seekcy.otaupgrade.util.Arrays;
import com.seekcy.otaupgrade.util.OtaLogger;

import java.util.List;
import java.util.UUID;

public class GattConnection extends Peripheral {


    private static final int TAG_OTA_ENABLE_NOTIFICATION = 10;
    private static final int TAG_GENERAL_READ = 11;
    private static final int TAG_GENERAL_WRITE = 12;
    private static final int TAG_GENERAL_READ_DESCRIPTOR = 13;
    private static final int TAG_GENERAL_ENABLE_NOTIFICATION = 14;


    private ConnectionCallback mConnectionCallback;


    public GattConnection(Context context) {
        super(context);
    }

    public void setConnectionCallback(ConnectionCallback callback) {
        this.mConnectionCallback = callback;
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice) {
        if (mConnectionCallback != null) {
            mConnectionCallback.onConnectionStateChange(BluetoothGatt.STATE_CONNECTING, this, -1);
        }
        super.connect(bluetoothDevice);
    }

    public void clearAll(boolean disconnect) {
        this.mConnectionCallback = null;
        this.clear();
        if (disconnect) {
            this.forceDisconnect();
        }
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        /*if (mDeviceStateCallback != null) {
            mDeviceStateCallback.onConnectionStateChange(this, BluetoothGatt.STATE_CONNECTED);
        }*/
    }

    @Override
    protected void onDisconnect(int statusCode) {
        super.onDisconnect(statusCode);
        if (isConnectWaiting.get()) {
            this.connect();
        } else {
            if (mConnectionCallback != null) {
                mConnectionCallback.onConnectionStateChange(BluetoothGatt.STATE_DISCONNECTED, this, statusCode);
            }
        }
    }

    @Override
    protected void onServicesDiscovered(List<BluetoothGattService> services) {
        super.onServicesDiscovered(services);
        if (mConnectionCallback != null) {
            mConnectionCallback.onConnectionStateChange(BluetoothGatt.STATE_CONNECTED, this, 0x00);
        }

    }


    @Override
    protected void onNotify(byte[] data, UUID serviceUUID, UUID characteristicUUID, Object tag) {
        super.onNotify(data, serviceUUID, characteristicUUID, tag);
        OtaLogger.d("onNotify: " + Arrays.bytesToHexString(data, ":"));
        if (mConnectionCallback != null) {
            mConnectionCallback.onNotify(data, serviceUUID, characteristicUUID, this);
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        if (status == BluetoothGatt.GATT_SUCCESS && mConnectionCallback != null) {
            mConnectionCallback.onMtuChanged(mtu, this);
        }
    }

    public boolean isNotificationEnable(BluetoothGattCharacteristic characteristic) {
        String key = generateHashKey(characteristic.getService().getUuid(),
                characteristic);
        return mNotificationCallbacks.containsKey(key);
    }

    public void enableNotification(UUID serviceUUID, UUID characteristicUUID) {
        Command cmd = Command.newInstance();
        cmd.serviceUUID = serviceUUID;
        cmd.characteristicUUID = characteristicUUID;
        cmd.type = Command.CommandType.ENABLE_NOTIFY;
        cmd.tag = TAG_GENERAL_ENABLE_NOTIFICATION;
        sendCommand(null, cmd);

        BluetoothGattService service = getService(serviceUUID);
        if (service == null) return;
        BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(characteristicUUID);
        if (gattCharacteristic.getDescriptor(UuidInfo.CFG_DESCRIPTOR_UUID) != null) {
            Command cccCmd = Command.newInstance();
            cccCmd.serviceUUID = serviceUUID;
            cccCmd.characteristicUUID = characteristicUUID;
            cccCmd.descriptorUUID = UuidInfo.CFG_DESCRIPTOR_UUID;
            cccCmd.type = Command.CommandType.WRITE_DESCRIPTOR;
            cccCmd.data = new byte[]{0x01, 0x00};
            sendCommand(null, cccCmd);
        }
    }

    private BluetoothGattService getService(UUID serviceUUID) {
        if (mServices != null) {
            for (BluetoothGattService service : mServices
            ) {
                if (service.getUuid().equals(serviceUUID)) {
                    return service;
                }
            }
        }
        return null;
    }


    public interface ConnectionCallback {
        void onConnectionStateChange(int state, GattConnection gattConnection, int statusCode);

        void onNotify(byte[] data, UUID serviceUUID, UUID characteristicUUID, GattConnection connection);

        void onMtuChanged(int mtu, GattConnection connection);
    }


}
