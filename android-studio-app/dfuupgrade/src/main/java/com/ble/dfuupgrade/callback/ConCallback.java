package com.ble.dfuupgrade.callback;

import android.bluetooth.BluetoothGatt;

public interface ConCallback {
    void success(BluetoothGatt gatt);

    void failed();

    void timeout(boolean result);

    void end();
}
