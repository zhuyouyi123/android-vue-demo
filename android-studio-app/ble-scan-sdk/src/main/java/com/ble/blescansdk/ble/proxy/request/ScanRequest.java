package com.ble.blescansdk.ble.proxy.request;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.os.HandlerCompat;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.annotation.Implement;
import com.ble.blescansdk.ble.callback.IScanWrapperCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.entity.bo.BlueToothDeviceBO;
import com.ble.blescansdk.ble.entity.storage.SeekBeaconStorage;
import com.ble.blescansdk.ble.entity.storage.SeekStandardBeaconStorage;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.enums.ManufacturerEnum;
import com.ble.blescansdk.ble.scan.BleScannerCompat;
import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.PermissionUtil;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Implement(ScanRequest.class)
public class ScanRequest<T extends BleDevice> implements IScanWrapperCallback {

    private BleScanCallback<T> bleScanCallback;

    private static boolean scanning = false;

    /**
     * 扫描到的设备
     */
    private final ConcurrentMap<String, BlueToothDeviceBO> scanDevices = new ConcurrentHashMap<>();

    private final Handler handler = BleHandler.of();

    private static int manufacturer = ManufacturerEnum.SEEK_STANDARD.getCode();


    /**
     * 是否忽略重复的设备
     */
    private static boolean isIgnoreRepeat = false;

    public void startScan(BleScanCallback<T> callback, long scanPeriod) {
        if (callback == null) {
            throw new IllegalArgumentException("BleScanCallback can not be null!");
        }
        bleScanCallback = callback;
        if (!PermissionUtil.checkSelfPermission(BleSdkManager.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            BleLogUtil.e("access coarse location not exist");
            bleScanCallback.onScanFailed(ErrorStatusEnum.ACCESS_COARSE_LOCATION_NOT_EXIST.getErrorCode());
            return;
        }

        if (!BleSdkManager.getBluetoothAdapter().isEnabled()) {
            BleLogUtil.e("bluetooth is not open");
            bleScanCallback.onScanFailed(ErrorStatusEnum.BLUETOOTH_NOT_OPEN.getErrorCode());
            return;
        }

        // 扫描间隔大于0 且未开启连续扫描
        if (scanPeriod >= 0 && !BleSdkManager.getBleOptions().isContinuousScanning()) {
            HandlerCompat.postDelayed(handler, () -> {
                if (scanning) {
                    if (ManufacturerEnum.SEEK.getCode() == manufacturer) {
                        SeekBeaconStorage.getInstance().onRssiMaxDevice();
                    }
                    stopScan();
                }
            }, "stop_token", scanPeriod);
        }

        manufacturer = BleSdkManager.getBleOptions().getManufacturer();

        storageInit(callback);

        BleScannerCompat.getScanner().startScan(this);
    }

    private void storageInit(BleScanCallback<T> callback) {
        if (ManufacturerEnum.SEEK_STANDARD.getCode() == manufacturer) {
            SeekStandardBeaconStorage.getInstance().init(callback);
        } else if (ManufacturerEnum.SEEK.getCode() == manufacturer) {
            SeekBeaconStorage.getInstance().init(callback);
        }
    }

    public void stopScan() {
        if (!BleSdkManager.getBluetoothAdapter().isEnabled()) {
            if (null != bleScanCallback) {
                bleScanCallback.onScanFailed(ErrorStatusEnum.BLUETOOTH_NOT_OPEN.getErrorCode());
            }
            return;
        }
        if (!scanning) {
            return;
        }
        handler.removeCallbacksAndMessages("stop_token");
        BleScannerCompat.getScanner().stopScan();
    }

    /**
     * 扫描状态
     *
     * @return 是否开启扫描
     */
    public static boolean isScanning() {
        return scanning;
    }

    @Override
    public void onStart() {
        scanning = true;
        if (bleScanCallback != null) {
            bleScanCallback.onStatusChange(true);
            isIgnoreRepeat = BleSdkManager.getBleOptions().isIgnoreRepeat();
        }
    }

    @Override
    public void onStop() {
        scanning = false;
        if (bleScanCallback != null) {
            bleScanCallback.onStatusChange(false);
            bleScanCallback = null;
        }
        scanDevices.clear();
        if (ManufacturerEnum.SEEK.getCode() == manufacturer) {
            SeekBeaconStorage.getInstance().release();
        } else if (ManufacturerEnum.SEEK_STANDARD.getCode() == manufacturer) {
            SeekStandardBeaconStorage.getInstance().release();
        }
        BleLogUtil.i("stop scan and clear scanDevices");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String address = device.getAddress().replaceAll(":", "");
        if (Objects.isNull(device)) {
            return;
        }

        BlueToothDeviceBO blueToothDeviceBO = new BlueToothDeviceBO(device, rssi, scanRecord);
        // mac不存在 或者 不忽略重复
        if (!isIgnoreRepeat || !scanDevices.containsKey(address)) {
            scanDevices.put(address, blueToothDeviceBO);
            if (ManufacturerEnum.SEEK.getCode() == manufacturer) {
                SeekBeaconStorage.getInstance().analysis(scanRecord, device, rssi);
            } else if (ManufacturerEnum.SEEK_STANDARD.getCode() == manufacturer) {
                SeekStandardBeaconStorage.getInstance().analysis(scanRecord, device, rssi);
            }

        }
    }


    @Override
    public void onScanFailed(int errorCode) {
        BleLogUtil.e("扫描错误" + errorCode);
    }
}
