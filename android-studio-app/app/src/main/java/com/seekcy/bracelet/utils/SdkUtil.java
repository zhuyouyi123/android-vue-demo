package com.seekcy.bracelet.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.enums.ManufacturerEnum;
import com.ble.blescansdk.ble.queue.retry.RetryDispatcher;
import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.ThreadUtils;
import com.ble.dfuupgrade.MyBleManager;
import com.ble.dfuupgrade.callback.ConCallback;
import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.DeviceDataService;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.callback.BleNotifyCallback;
import com.seekcy.bracelet.callback.ConnectCallback;
import com.seekcy.bracelet.callback.NotifyCallback;
import com.seekcy.bracelet.callback.WriteCallback;
import com.seekcy.bracelet.data.constants.JsBridgeConstants;
import com.seekcy.bracelet.data.constants.SharePreferenceConstants;
import com.seekcy.bracelet.data.holder.DeviceHolder;
import com.seekcy.bracelet.scheduled.CommandRetryScheduled;
import com.seekcy.bracelet.service.DeviceService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SdkUtil {

    private static final String TAG = SdkUtil.class.getSimpleName();

    private static volatile boolean canExecute = true;

    @SuppressLint("StaticFieldLeak")
    private static MyBleManager bleManager = null;

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
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
        } else {
            requestPermissions = new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };

        }

        PermissionsUtil.requestBasePermissions(requestPermissions);
    }

    public static void setCanExecute(boolean b) {
        canExecute = b;
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
        DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECTING);
        MyBleManager.getInstance(Config.mainContext).init(device.getAddress(),
                new ConCallback() {
                    @Override
                    public void success(BluetoothGatt gatt) {
                        DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECTED);
                        DeviceHolder.getInstance().getInfo();
                        JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_CONNECTED);
                        // 保存 设备MAC
                        SharePreferenceUtil.getInstance().shareSet(SharePreferenceConstants.DEVICE_ADDRESS_KEY, device.getAddress());
                        callback.success(device.getAddress());
                    }

                    @Override
                    public void failed() {
                        DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECT_FAILED);
                        JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_UN_CONNECTED);
                        callback.failed();
                    }

                    @Override
                    public void timeout(boolean result) {
                        if (!result) {
                            DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECT_FAILED);
                            JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_UN_CONNECTED);
                        }
                    }
                }, BleNotifyCallback.getInstance(), true
        );

        MyBleManager.getInstance(Config.mainContext).connectToDevice();
        DeviceHolder.getInstance().setBleManager(MyBleManager.getInstance(Config.mainContext));
    }

    @SuppressLint("RestrictedApi")
    public static void runOnUiThread(Runnable runnable) {
        ThreadUtils.ui(runnable);
    }


    public static void retryWriteCommand(String hex) {
        if (!canExecute) {
            return;
        }
        if (StringUtils.isBlank(hex)) {
            return;
        }

        if (Objects.isNull(bleManager)) {
            bleManager = DeviceHolder.getInstance().getBleManager();
            if (Objects.isNull(bleManager)) {
                return;
            }
        }
        bleManager.write(ProtocolUtil.hexStrToBytes(hex));
        // BleSdkManager.getInstance().write(DeviceHolder.DEVICE, ProtocolUtil.hexStrToBytes(hex), WriteCallback.getInstance());
    }

    public static void writeCommand(String hex) {
        CommandRetryScheduled.getInstance().add(hex);
        if (Objects.isNull(bleManager)) {
            bleManager = DeviceHolder.getInstance().getBleManager();
            if (Objects.isNull(bleManager)) {
                return;
            }
        }
        bleManager.write(ProtocolUtil.hexStrToBytes(hex));
    }

    public static void writeCommand(byte[] bytes) {
        if (!canExecute) {
            return;
        }
        CommandRetryScheduled.getInstance().add(ProtocolUtil.byteArrToHexStr(bytes));
        if (Objects.isNull(bleManager)) {
            bleManager = DeviceHolder.getInstance().getBleManager();
            if (Objects.isNull(bleManager)) {
                return;
            }
        }
        bleManager.write(bytes);
    }
}
