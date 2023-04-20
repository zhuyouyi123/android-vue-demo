/*
 * Copyright (C)  aicareles, Android-BLE Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ble.blescansdk.ble.scan;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import com.ble.blescansdk.ble.callback.IScanWrapperCallback;

public abstract class BleScannerCompat {

    private static BleScannerCompat mInstance;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    IScanWrapperCallback scanWrapperCallback;


    public static BleScannerCompat getScanner() {
        if (mInstance != null)
            return mInstance;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return mInstance = new BluetoothScannerImplLollipop();
        return mInstance = new BluetoothScannerImplJB();
    }

    public void startScan(IScanWrapperCallback scanWrapperCallback) {
        this.scanWrapperCallback = scanWrapperCallback;
        scanWrapperCallback.onStart();
    }

    public void stopScan() {
        if (scanWrapperCallback != null) {
            scanWrapperCallback.onStop();
        }
    }

}
