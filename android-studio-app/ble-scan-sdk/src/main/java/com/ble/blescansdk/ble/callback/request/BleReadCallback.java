package com.ble.blescansdk.ble.callback.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.entity.BleDevice;

/**
 * 读取数据回调
 * @param <T>
 */
public abstract class BleReadCallback<T extends BleDevice> {

    public abstract void onReadSuccess(T dedvice, BluetoothGattCharacteristic characteristic);

    public abstract void onReadFailed(T device, int failedCode);

}
