package com.ble.blescansdk.ble.entity.storage;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.analysis.AnalysisSeekBeaconHandle;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.SeekBleDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SeekBeaconStorage extends AbstractStorage {

    private static SeekBeaconStorage INSTANCE = null;

    private static final AnalysisSeekBeaconHandle ANALYSIS_SEEK_BEACON_HANDLE = AnalysisSeekBeaconHandle.getInstance();

    public static ConcurrentMap<String, SeekBleDevice> seekMap = new ConcurrentHashMap<>();

    private static BleScanCallback<SeekBleDevice> bleScanCallback;

    private static boolean isIntermittentScanning = BleSdkManager.getBleOptions().isIntermittentScanning();
    // 扫描间隔
    private static final long intermittentTime = BleSdkManager.getBleOptions().getIntermittentTime();

    private static ScheduledExecutorService scheduledExecutorServicePool;

    private static BleOptions.FilterInfo filterInfo = null;

    public static SeekBeaconStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SeekBeaconStorage();
        }
        return INSTANCE;
    }

    public void init(BleScanCallback callback) {
        if (null == callback) {
            return;
        }
        filterInfo = BleSdkManager.getBleOptions().getFilterInfo();
        isIntermittentScanning = BleSdkManager.getBleOptions().isIntermittentScanning();
        bleScanCallback = callback;
        seekMap.clear();
        if (scheduledExecutorServicePool == null) {
            scheduledExecutorServicePool = new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "executor-service-pool-" + r.hashCode()));
        } else {
            if (scheduledExecutorServicePool.isShutdown()) {
                scheduledExecutorServicePool.shutdown();
            }
        }
        if (isIntermittentScanning) {
            startCallBack();
        }
    }


    public void add(SeekBleDevice device) {
        if (Objects.isNull(device)) {
            return;
        }

        String address = device.getAddress();

        seekMap.put(address, device);
    }

    public void analysis(byte[] scanBytes, BluetoothDevice device, boolean isConnectable, int rssi) {
        // 基础信息验证
        if (!filterMacAndRssi(filterInfo, device, isConnectable, rssi)) {
            return;
        }

        SeekBleDevice analysis = ANALYSIS_SEEK_BEACON_HANDLE.analysis(seekMap.get(device.getAddress()), scanBytes, device, isConnectable, rssi);
        if (!filterDevice(analysis)) {
            return;
        }
        add(analysis);
        if (!isIntermittentScanning) {
            bleScanCallback.onLeScan(analysis, rssi, scanBytes);
        }
    }

    public void onRssiMaxDevice() {
        SeekBleDevice seekBleDevice = null;

        boolean needSeekBeacon = null == filterInfo || filterInfo.isNormDevice();
        for (Map.Entry<String, SeekBleDevice> entry : seekMap.entrySet()) {
            SeekBleDevice value = entry.getValue();
            if (null == value) {
                continue;
            }

            if (needSeekBeacon && !value.isSeekBeacon()) {
                continue;
            }

            if (null == seekBleDevice) {
                seekBleDevice = value;
            } else if (seekBleDevice.getRssi() < value.getRssi()) {
                seekBleDevice = value;
            }
        }

        if (null == seekBleDevice) {
            bleScanCallback.onRssiMax(null, -1);
        } else {
            bleScanCallback.onRssiMax(seekBleDevice, seekBleDevice.getRssi());
        }
    }


    private void startCallBack() {
        scheduledExecutorServicePool.scheduleWithFixedDelay(new TimerTask() {
            @Override
            public void run() {
                if (null != seekMap && !seekMap.isEmpty()) {
                    List<SeekBleDevice> list = new ArrayList<>(seekMap.values());
                    bleScanCallback.onLeScan(list);
                    expire();
                } else {
                    bleScanCallback.onLeScan(Collections.emptyList());
                }
            }
        }, 100, intermittentTime, TimeUnit.MILLISECONDS);
    }

    public void release() {
        seekMap.clear();
        if (null != scheduledExecutorServicePool && !scheduledExecutorServicePool.isShutdown()) {
            scheduledExecutorServicePool.shutdownNow();
            scheduledExecutorServicePool = null;
        }
    }

    private boolean filterDevice(SeekBleDevice seekBleDevice) {

        if (Objects.isNull(seekBleDevice)) {
            return false;
        }

        if (Objects.isNull(filterInfo)) {
            return true;
        }

        if (filterInfo.isNormDevice()) {
            return seekBleDevice.isSeekBeacon();
        }

        return true;
    }

    @Override
    public void expire() {

        long deviceSurviveTime = BleSdkManager.getBleOptions().getDeviceSurviveTime();

        // -1 永久保存
        if (deviceSurviveTime == -1) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, SeekBleDevice> entry : seekMap.entrySet()) {
            SeekBleDevice value = entry.getValue();
            String key = entry.getKey();
            if (null == value) {
                seekMap.remove(key);
                continue;
            }
            long diff = currentTime - value.getScanTime();
            if (diff > deviceSurviveTime) {
                seekMap.remove(key);
            }
        }
    }
}
