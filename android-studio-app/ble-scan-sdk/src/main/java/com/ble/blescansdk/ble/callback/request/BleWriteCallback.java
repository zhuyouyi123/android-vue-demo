package com.ble.blescansdk.ble.callback.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.entity.BleDevice;

/**
 * Created by LiuLei on 2017/10/23.
 */

public abstract class BleWriteCallback<T extends BleDevice> {
    public abstract void onWriteSuccess(T device, BluetoothGattCharacteristic characteristic);

    public abstract void onWriteFailed(T device, int failedCode) ;
}
