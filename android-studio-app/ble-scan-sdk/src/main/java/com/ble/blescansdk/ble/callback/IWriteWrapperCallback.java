package com.ble.blescansdk.ble.callback;

import android.bluetooth.BluetoothGattCharacteristic;

public interface IWriteWrapperCallback<T> {
    void onWriteSuccess(T device, BluetoothGattCharacteristic characteristic);

    void onWriteFailed(T device, int failedCode);
}
