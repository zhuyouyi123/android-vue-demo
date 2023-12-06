package com.panvan.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.enums.ManufacturerEnum;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.ThreadUtils;
import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.DeviceDataService;
import com.panvan.app.Config;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.callback.NotifyCallback;
import com.panvan.app.callback.WriteCallback;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.scheduled.CommandRetryScheduled;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SdkUtil {

    private static final String TAG = SdkUtil.class.getSimpleName();

    public static void init() {
        BleSdkManager.getInstance().init(Config.mainContext);

        BleSdkManager.getBleOptions()
                .setManufacturer(ManufacturerEnum.BRACELET)
                .setContinuousScanning(false)
                .setBleScanLevel(BleScanLevelEnum.SCAN_MODE_LOW_LATENCY)
                .setDatabaseSupport(true)
                .setConnectFailedRetryCount(5)
                .setConnectTimeout(3000)
                .setEncryption(false)
                .setDatabaseSupport(false)
                .setLogSwitch(true)
                .setScanPeriod(10000);

        BleSdkManager.getBleOptions().uuid_services_extra = new UUID[5];

        BleSdkManager.getBleOptions().uuid_services_extra[0] = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        // BleSdkManager.getBleOptions().uuid_services_extra[1] = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        // BleSdkManager.getBleOptions().uuid_services_extra[2] = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
        // BleSdkManager.getBleOptions().uuid_services_extra[1] = UUID.fromString("000180d-0000-1000-8000-00805f9b34fb");
        // BleSdkManager.getBleOptions().uuid_services_extra[4] = UUID.fromString("0000190e-0000-1000-8000-00805f9b34fb");
        //
        // BleSdkManager.getBleOptions().uuid_service = UUID.fromString("000180d-0000-1000-8000-00805f9b34fb");

        // 0000180d-0000-1000-8000-00805f9b34fb
        // 00002a37-0000-1000-8000-00805f9b34fb
        // 00002a38-0000-1000-8000-00805f9b34fb

        requestPermissions();
    }

    public static void requestPermissions() {
        String[] requestPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions = new String[]{
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,

            };
        } else {
            requestPermissions = new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

        }

        PermissionsUtil.requestBasePermissions(requestPermissions);
    }


    public interface Callback {

        void result(BraceletDevice device);

        void failed(int errorCode);

    }

    public static void scan(Callback callback) {

        BleSdkManager.getInstance().startScan(new BleScanCallback<BraceletDevice>() {
            @Override
            public void onStatusChange(boolean state) {
                LogUtil.info("设备扫描状态：" + state);
                if (!state) {
                    runOnUiThread(MaskUtil::hide);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                callback.failed(errorCode);
                LogUtil.info("设备扫描出错：" + errorCode);
            }

            @Override
            public void onLeScan(BraceletDevice device, int rssi, byte[] scanRecord) {
                LogUtil.info(TAG, "扫描到设备" + device);
                callback.result(device);
                stopScan();

            }

            @Override
            public void onLeScan(List<BraceletDevice> device) {

            }

            @Override
            public void onRssiMax(BraceletDevice device, int rssi) {

            }
        });
    }

    public static void stopScan() {
        BleSdkManager.getInstance().stopScan();
    }

    public static void connect(BraceletDevice device, ConnectCallback callback) {

        DeviceHolder.DEVICE = device;

        DeviceHolder.getInstance().setConnectStatus(DeviceHolder.CONNECTING);

        BleSdkManager.getInstance().connect(device, new BleConnectCallback<BraceletDevice>() {
            @Override
            public void onConnectChange(BraceletDevice device, int status) {
                // DeviceHolder.getInstance().setConnectStatus();
            }

            @Override
            public void onConnectSuccess(BraceletDevice device) {
                callback.success(device.getAddress());

                DeviceHolder.getInstance().setConnectStatus(DeviceHolder.CONNECTED);
                JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_CONNECTED);
                // 保存 设备MAC
                SharePreferenceUtil.getInstance().shareSet(SharePreferenceConstants.DEVICE_ADDRESS_KEY, device.getAddress());

                BleSdkManager.getInstance().startNotify(device, NotifyCallback.getInstance());
                runOnUiThread(MaskUtil::hide);

            }

            @Override
            public void onConnectFailed(BraceletDevice device, int errorCode) {
                callback.failed();

                DeviceHolder.getInstance().setConnectStatus(DeviceHolder.CONNECT_FAILED);
                JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_UN_CONNECTED);
                runOnUiThread(MaskUtil::hide);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public static void runOnUiThread(Runnable runnable) {
        ThreadUtils.ui(runnable);
    }

    private static final Object OBJECT = new Object();


    public static void retryWriteCommand(String hex) {
        synchronized (OBJECT) {
            BleSdkManager.getInstance().write(DeviceHolder.DEVICE, ProtocolUtil.hexStrToBytes(hex), WriteCallback.getInstance());
        }
    }

    public static void writeCommand(String hex) {
        synchronized (OBJECT) {
            CommandRetryScheduled.getInstance().add(hex);
            BleSdkManager.getInstance().write(DeviceHolder.DEVICE, ProtocolUtil.hexStrToBytes(hex), WriteCallback.getInstance());
        }
    }

    public static void writeCommand(byte[] bytes) {
        synchronized (OBJECT) {
            CommandRetryScheduled.getInstance().add(ProtocolUtil.byteArrToHexStr(bytes));
            BleSdkManager.getInstance().write(DeviceHolder.DEVICE, bytes, WriteCallback.getInstance());
        }
    }
}
