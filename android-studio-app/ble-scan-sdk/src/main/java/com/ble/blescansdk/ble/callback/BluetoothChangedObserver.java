package com.ble.blescansdk.ble.callback;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ScanRequest;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * 蓝牙状态发生改变 观察者
 */
public class BluetoothChangedObserver extends BroadcastReceiver {

    private IBleStatusCallback bleStatusCallback;

    private final WeakReference<BluetoothChangedObserver> observerWeakReference;


    public BluetoothChangedObserver() {
        observerWeakReference = new WeakReference<>(this);
    }


    public void setBleStatusCallback(IBleStatusCallback bleStatusCallback) {
        this.bleStatusCallback = bleStatusCallback;
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        BleSdkManager.getContext().registerReceiver(this, filter);
    }

    public void unregisterReceiver() {
        try {
            BleSdkManager.getContext().unregisterReceiver(this);
            bleStatusCallback = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            BluetoothChangedObserver changedObserver = observerWeakReference.get();

            int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            if (status == BluetoothAdapter.STATE_ON) {
                // 开启
                if (Objects.nonNull(changedObserver.bleStatusCallback)) {
                    changedObserver.bleStatusCallback.onBluetoothStatusChanged(true);
                }
            } else if (status == BluetoothAdapter.STATE_OFF) {
                // 关闭
                if (Objects.nonNull(changedObserver.bleStatusCallback)) {
                    changedObserver.bleStatusCallback.onBluetoothStatusChanged(false);
                }
                //如果正在扫描，则停止扫描 并释放资源
                ScanRequest scanRequest = Rproxy.getRequest(ScanRequest.class);
                if (ScanRequest.isScanning()) {
                    scanRequest.onStop();
                }
            }
        }
    }
}
