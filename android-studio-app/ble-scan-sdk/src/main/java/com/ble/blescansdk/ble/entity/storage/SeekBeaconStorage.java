package com.ble.blescansdk.ble.entity.storage;

import android.bluetooth.BluetoothDevice;


import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.analysis.AnalysisSeekBeaconHandle;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.SeekBleDevice;
import com.ble.blescansdk.ble.utils.StringUtils;

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
    private static final long scanPeriod = BleSdkManager.getBleOptions().getScanPeriod();

    private static ScheduledExecutorService scheduledExecutorServicePool;

    private static final BleOptions.FilterInfo filterInfo = BleSdkManager.getBleOptions().getFilterInfo();

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

        SeekBleDevice seekBleDevice = seekMap.get(address);
        if (null == seekBleDevice) {
            seekMap.put(address, device);
        } else if (seekBleDevice.getRssi() < device.getRssi()) {
            seekMap.put(address, device);
        }

    }

    public void analysis(byte[] scanBytes, BluetoothDevice device, int rssi) {
        SeekBleDevice analysis = ANALYSIS_SEEK_BEACON_HANDLE.analysis(scanBytes, device, rssi);
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

        for (Map.Entry<String, SeekBleDevice> entry : seekMap.entrySet()) {
            SeekBleDevice value = entry.getValue();
            if (null == value) {
                continue;
            }

            if (!value.isSeekBeacon()) {
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
        }, scanPeriod, scanPeriod, TimeUnit.MILLISECONDS);
    }

    public void release() {
        seekMap.clear();
        if (null != scheduledExecutorServicePool && !scheduledExecutorServicePool.isShutdown()) {
            scheduledExecutorServicePool.shutdownNow();
            scheduledExecutorServicePool = null;
        }
    }

    public void sort() {

    }

    private boolean filterDevice(SeekBleDevice seekBleDevice) {
        if (null == filterInfo) {
            return true;
        }

        String filterInfoMac = filterInfo.getAddress();
        if (StringUtils.isNotBlank(filterInfoMac)) {
            if (!seekBleDevice.getAddress().equals(filterInfoMac)) {
                return false;
            }
        }

        String filterInfoName = filterInfo.getName();
        if (StringUtils.isNotBlank(filterInfoName)) {
            String name = seekBleDevice.getName();
            if (StringUtils.isBlank(name) || !name.equals(filterInfoName)) {
                return false;
            }
        }

        Integer filterInfoRssi = filterInfo.getRssi();
        if (null != filterInfoRssi && filterInfoRssi > seekBleDevice.getRssi()) {
            return false;
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
