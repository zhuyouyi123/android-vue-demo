package com.ble.blescansdk.ble.entity.storage;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.analysis.AnalysisSeekStandardBeaconHandle;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.entity.seek.StandardThoroughfareInfo;
import com.ble.blescansdk.ble.enums.SortTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SeekStandardBeaconStorage extends AbstractStorage {

    public static SeekStandardBeaconStorage INSTANCE = null;

    public static ConcurrentMap<String, SeekStandardDevice> deviceMap = new ConcurrentHashMap<>();

    private static final AnalysisSeekStandardBeaconHandle analysisHandle = AnalysisSeekStandardBeaconHandle.getInstance();
    // 是否是间歇性扫描
    private static boolean isIntermittentScanning = BleSdkManager.getBleOptions().isIntermittentScanning();
    // 回调
    private static BleScanCallback<SeekStandardDevice> bleScanCallback;
    // 扫描间隔
    private static final long scanPeriod = BleSdkManager.getBleOptions().getScanPeriod();

    private static ScheduledExecutorService scheduledExecutorServicePool;
    // 过滤信息
    private static final BleOptions.FilterInfo filterInfo = BleSdkManager.getBleOptions().getFilterInfo();

    public static SeekStandardBeaconStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SeekStandardBeaconStorage();
        }
        return INSTANCE;
    }


    public void init(BleScanCallback callback) {
        if (null == callback) {
            return;
        }
        isIntermittentScanning = BleSdkManager.getBleOptions().isIntermittentScanning();
        bleScanCallback = callback;
        deviceMap.clear();
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

    public void add(SeekStandardDevice device) {
        if (Objects.isNull(device)) {
            return;
        }

        String address = device.getAddress();

        SeekStandardDevice seekStandardDevice = deviceMap.get(address);

        if (null == seekStandardDevice) {
            deviceMap.put(address, device);
        } else if (seekStandardDevice.getRssi() < device.getRssi()) {
            List<StandardThoroughfareInfo> thoroughfares = seekStandardDevice.getThoroughfares();
            if (null != thoroughfares && thoroughfares.size() > 0) {
                device.setThoroughfares(thoroughfares);
            }
            deviceMap.put(address, device);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void analysis(byte[] scanBytes, BluetoothDevice device, int rssi) {
        // 基础信息验证
        if (!filterMacAndRssi(filterInfo, device, rssi)) {
            return;
        }
        SeekStandardDevice analysis = analysisHandle.analysis(scanBytes, device, rssi);

        if (!filterDevice(analysis)) {
            return;
        }

        if (!isIntermittentScanning) {
            bleScanCallback.onLeScan(analysis, rssi, scanBytes);
        } else {
            // 添加到map中
            add(analysis);
        }
    }

    private void startCallBack() {
        scheduledExecutorServicePool.scheduleWithFixedDelay(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                if (null != deviceMap && !deviceMap.isEmpty()) {
                    List<SeekStandardDevice> list = new ArrayList<>(deviceMap.values());
                    bleScanCallback.onLeScan(sort(list));
                    expire();
                } else {
                    bleScanCallback.onLeScan(Collections.emptyList());
                }
            }
        }, scanPeriod, scanPeriod, TimeUnit.MILLISECONDS);
    }


    private boolean filterDevice(SeekStandardDevice seekStandardDevice) {

        if (Objects.isNull(filterInfo)) {
            return true;
        }

        if (filterInfo.isNormDevice()) {
            return seekStandardDevice.isLocSmart();
        }

        return true;
    }

    @Override
    public void release() {
        deviceMap.clear();
        if (null != scheduledExecutorServicePool && !scheduledExecutorServicePool.isShutdown()) {
            scheduledExecutorServicePool.shutdownNow();
            scheduledExecutorServicePool = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<SeekStandardDevice> sort(List<SeekStandardDevice> devices) {
        SortTypeEnum sortType = BleSdkManager.getBleOptions().getSortType();
        switch (sortType) {
            case MAC_RISE:
                return devices.stream().sorted(Comparator.comparing(SeekStandardDevice::getAddress)).collect(Collectors.toList());
            case MAC_FALL:
                return devices.stream().sorted(Comparator.comparing(SeekStandardDevice::getAddress).reversed()).collect(Collectors.toList());
            case RSSI_RISE:
                return devices.stream().sorted(Comparator.comparingInt(SeekStandardDevice::getRssi)).collect(Collectors.toList());
            case RSSI_FALL:
                return devices.stream().sorted(Comparator.comparingInt(SeekStandardDevice::getRssi).reversed()).collect(Collectors.toList());
            case BATTERY_RISE:
                return devices.stream().sorted(Comparator.comparingInt(SeekStandardDevice::getBattery)).collect(Collectors.toList());
            case BATTERY_FALL:
                return devices.stream().sorted(Comparator.comparingInt(SeekStandardDevice::getBattery).reversed()).collect(Collectors.toList());
            default:
                return devices;
        }
    }

    @Override
    public void expire() {
        long deviceSurviveTime = BleSdkManager.getBleOptions().getDeviceSurviveTime();
        // -1 永久保存
        if (deviceSurviveTime == -1) {
            return;
        }
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, SeekStandardDevice> entry : deviceMap.entrySet()) {
            SeekStandardDevice value = entry.getValue();
            String key = entry.getKey();
            if (null == value) {
                deviceMap.remove(key);
                continue;
            }
            long diff = currentTime - value.getScanTime();
            if (diff > deviceSurviveTime) {
//                BleLogUtil.i("设备过期，被清除：" + key + " 超时时间：" + diff);
                deviceMap.remove(key);
            }
        }
    }
}
