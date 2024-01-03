package com.panvan.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.os.Build;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.enums.ManufacturerEnum;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.dfuupgrade.MyBleManager;
import com.ble.dfuupgrade.callback.ConCallback;
import com.panvan.app.Config;
import com.panvan.app.callback.BleNotifyCallback;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.scheduled.CommandRetryScheduled;
import com.panvan.app.service.CommunicationService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SdkUtil {

    private static final String TAG = SdkUtil.class.getSimpleName();

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

    private static ConCallback conCallback = null;

    public static void connect(BraceletDevice device, ConnectCallback callback) {
        DeviceHolder.DEVICE = device;
        DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECTING);
        bleManager = new MyBleManager(Config.mainContext);
        DeviceHolder.getInstance().setBleManager(bleManager);
        conCallback = new ConCallback() {
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
                CommunicationService.getInstance().clearTask();
                callback.failed();
            }

            @Override
            public void timeout(boolean result) {
                if (!result) {
                    DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECT_FAILED);
                    JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_UN_CONNECTED);
                }
            }

            @Override
            public void end() {
                if (Objects.nonNull(DeviceHolder.DEVICE) && DeviceHolder.DEVICE.getConnectState() != BluetoothProfile.STATE_CONNECTED) {
                    DeviceHolder.DEVICE.setConnectState(DeviceHolder.CONNECT_FAILED);
                    JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_UN_CONNECTED);
                    callback.failed();
                }
            }
        };
        bleManager.init(device.getAddress(), conCallback, BleNotifyCallback.getInstance(), true);
        bleManager.startScan();
    }

    public static void retryWriteCommand(String hex) {
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
    }

    public static void writeCommand(String hex) {
        LogUtil.error("写入指令：" + hex);
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
