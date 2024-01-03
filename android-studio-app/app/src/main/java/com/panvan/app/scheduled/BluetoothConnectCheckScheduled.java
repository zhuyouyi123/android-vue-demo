package com.panvan.app.scheduled;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.ble.dfuupgrade.DfuUpgradeHandle;
import com.ble.dfuupgrade.callback.DisCallback;
import com.panvan.app.Config;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.utils.LogUtil;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluetoothConnectCheckScheduled {
    private static BluetoothConnectCheckScheduled INSTANCE = null;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static BluetoothConnectCheckScheduled getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new BluetoothConnectCheckScheduled();
        }
        return INSTANCE;
    }

    private static final AtomicBoolean ATOMIC_BOOLEAN = new AtomicBoolean(false);

    public void start() {
        if (ATOMIC_BOOLEAN.get()) {
            return;
        }
        ATOMIC_BOOLEAN.set(true);
        scheduledExecutorService.scheduleAtFixedRate(this::check, 5, 10, TimeUnit.SECONDS);
    }

    @SuppressLint("MissingPermission")
    private void check() {
        int connectionState;
        LogUtil.info("当前连接状态:蓝牙自检");
        try {
            if (Objects.isNull(DeviceHolder.DEVICE)) {
                return;
            }

            if (Objects.isNull(DeviceHolder.getInstance().getBleManager())) {
                return;
            }

            if (DfuUpgradeHandle.getInstance().getIsUpgrading()) {
                return;
            }

            BluetoothManager bluetoothManager = (BluetoothManager) Config.mainContext.getSystemService(Context.BLUETOOTH_SERVICE);
            // 设备不支持蓝牙
            if (bluetoothManager == null) {
                return;
            }

            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            // 设备不支持蓝牙
            if (bluetoothAdapter == null) {
                return;
            }

            connectionState = bluetoothManager.getConnectionState(bluetoothAdapter.getRemoteDevice(DeviceHolder.DEVICE.getAddress()), BluetoothProfile.GATT);

            if (BluetoothProfile.STATE_CONNECTED != connectionState) {
                // 连接设备
                DeviceHolder.getInstance().getBleManager().dis(() -> DeviceHolder.getInstance().getBleManager().connectToDevice());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LogUtil.info("当前连接状态", connectionState + "");

    }
}


