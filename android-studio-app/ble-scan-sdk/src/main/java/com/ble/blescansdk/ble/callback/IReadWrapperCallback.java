package com.ble.blescansdk.ble.callback;

import android.bluetooth.BluetoothGattCharacteristic;

public interface IReadWrapperCallback <T>{

    void onReadSuccess(T device, BluetoothGattCharacteristic characteristic);

    void onReadFailed(T device, int failedCode);
}
