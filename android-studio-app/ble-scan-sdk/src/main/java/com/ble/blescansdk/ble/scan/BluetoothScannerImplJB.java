package com.ble.blescansdk.ble.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.callback.IScanWrapperCallback;


class BluetoothScannerImplJB extends BleScannerCompat {

    @Override
    public void startScan(IScanWrapperCallback scanWrapperCallback) {
        super.startScan(scanWrapperCallback);
        bluetoothAdapter.startLeScan(leScanCallback);
    }

    @Override
    public void stopScan() {
        super.stopScan();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    private final BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> scanWrapperCallback.onLeScan(device, rssi, scanRecord);
}
