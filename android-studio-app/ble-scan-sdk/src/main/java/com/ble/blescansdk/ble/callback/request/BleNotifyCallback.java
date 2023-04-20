package com.ble.blescansdk.ble.callback.request;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 *
 * Created by LiuLei on 2017/10/23.
 */

public abstract class BleNotifyCallback<T> {
    /**
     *  MCU data sent to the app when the data callback call is setNotify
     * @param device ble device object
     * @param characteristic  characteristic
     */
    public abstract void onChanged(T device, BluetoothGattCharacteristic characteristic);

    public abstract void onNotifySuccess(T device);

    public abstract void onNotifyFailed(T device, int failedCode);

    public abstract void onNotifyCanceled(T device);

}
