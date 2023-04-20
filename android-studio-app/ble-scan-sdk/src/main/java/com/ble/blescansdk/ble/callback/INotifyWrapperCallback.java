package com.ble.blescansdk.ble.callback;

import android.bluetooth.BluetoothGattCharacteristic;

public interface INotifyWrapperCallback <T>{

    void onChanged(T device, BluetoothGattCharacteristic characteristic);

    void onNotifySuccess(T device);

    void onNotifyFailed(T device, int failedCode);

    void onNotifyCanceled(T device);
}
