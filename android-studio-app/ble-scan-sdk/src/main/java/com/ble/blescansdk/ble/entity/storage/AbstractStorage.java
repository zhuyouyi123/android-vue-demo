package com.ble.blescansdk.ble.entity.storage;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.entity.constants.SeekStandardDeviceConstants;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractStorage {

    private static final int BROADCAST_MIN = 90;
    private static final int BROADCAST_MAX = 500;
    private static final int BROADCAST_DIFF_RANGE_1 = 100;
    private static final int BROADCAST_DIFF_RANGE_2 = -100;

    private static final ConcurrentMap<String, Integer> INTERVAL_MAP = new ConcurrentHashMap<>();


    public abstract void expire();

    public abstract void release();

    public boolean filterMacAndRssi(BleOptions.FilterInfo filterInfo, BluetoothDevice device, boolean isConnectable, int rssi) {
        if (Objects.isNull(device)) {
            return false;
        }

        if (null == filterInfo) {
            return true;
        }

        if (filterInfo.isSupportConnectable() && !isConnectable) {
            return false;
        }

        Integer filterInfoRssi = filterInfo.getRssi();
        if (Objects.nonNull(filterInfoRssi)) {
            return rssi >= filterInfoRssi;
        }

        return true;
    }

    /**
     * 计算 设置广播间隔
     *
     * @param currDevice 当前扫描到的设备
     * @param lastDevice 上一包的设备
     * @return 赋值之后信息
     */
    public SeekStandardDevice calcBroadcastInterval(SeekStandardDevice currDevice, SeekStandardDevice lastDevice) {
        if (Objects.isNull(lastDevice)) {
            return currDevice;
        }

        final String address = currDevice.getAddress();

        long broadcastIntervalDiff = currDevice.getScanTime() - lastDevice.getScanTime();
        // 如果是默认的广播间隔 直接赋值
        long broadcastInterval = lastDevice.getBroadcastInterval();

        if (broadcastInterval == SeekStandardDeviceConstants.DEFAULT_BROADCAST_INTERVAL) {
            INTERVAL_MAP.remove(address);
            currDevice.setBroadcastInterval(broadcastIntervalDiff);
        } else if (broadcastInterval >= broadcastIntervalDiff && broadcastIntervalDiff > BROADCAST_MIN) {
            INTERVAL_MAP.remove(address);
            currDevice.setBroadcastInterval(broadcastIntervalDiff);
        } else {
            long diff = broadcastIntervalDiff - broadcastInterval;
            if (diff <= BROADCAST_DIFF_RANGE_1 && diff >= BROADCAST_DIFF_RANGE_2) {
                INTERVAL_MAP.remove(address);
                if (broadcastIntervalDiff <= BROADCAST_MIN) {
                    final int interval = BROADCAST_MIN + new Random().nextInt(10);
                    currDevice.setBroadcastInterval(interval);
                    return currDevice;
                }
                currDevice.setBroadcastInterval(broadcastIntervalDiff);
                return currDevice;
            } else {
                if (broadcastIntervalDiff <= BROADCAST_MIN) {
                    final int interval = BROADCAST_MIN + new Random().nextInt(10);
                    currDevice.setBroadcastInterval(interval);
                    INTERVAL_MAP.remove(address);
                    return currDevice;
                }
                if (diff > BROADCAST_MAX) {
                    if (INTERVAL_MAP.containsKey(address)) {
                        final Integer integer = INTERVAL_MAP.get(address);
                        if (null != integer && integer > 2) {
                            currDevice.setBroadcastInterval(broadcastIntervalDiff);
                            INTERVAL_MAP.remove(address);
                            return currDevice;
                        }
                        INTERVAL_MAP.put(address, null == integer ? 1 : integer + 1);
                    } else {
                        INTERVAL_MAP.put(address, 1);
                    }
                } else {
                    INTERVAL_MAP.remove(address);
                }
                currDevice.setBroadcastInterval(broadcastInterval);
            }
        }
        return currDevice;
    }

    public void clear() {
        INTERVAL_MAP.clear();
    }

}
