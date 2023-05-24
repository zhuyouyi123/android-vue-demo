package com.ble.blescansdk.ble.entity.storage;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.entity.constants.SeekStandardDeviceConstants;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.Objects;
import java.util.Random;

public abstract class AbstractStorage {

    private static final int BROADCAST_MIN = 90;
    private static final int BROADCAST_DIFF_RANGE_1 = 5;
    private static final int BROADCAST_DIFF_RANGE_2 = -5;


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

        String filterInfoMac = filterInfo.getAddress();
        if (StringUtils.isNotBlank(filterInfoMac)) {
            String address = device.getAddress().replaceAll(":", "");
            if (!address.contains(filterInfoMac.toUpperCase().replaceAll(":", ""))) {
                return false;
            }
        }

        String filterInfoName = filterInfo.getName();
        if (StringUtils.isNotBlank(filterInfoName)) {
            String name = device.getName();
            if (StringUtils.isBlank(name) || !name.contains(filterInfoName)) {
                return false;
            }
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
        long broadcastIntervalDiff = currDevice.getScanTime() - lastDevice.getScanTime();
        // 如果是默认的广播间隔 直接赋值
        long broadcastInterval = lastDevice.getBroadcastInterval();

        if (broadcastInterval == SeekStandardDeviceConstants.DEFAULT_BROADCAST_INTERVAL) {
            currDevice.setBroadcastInterval(broadcastIntervalDiff);
        } else if (broadcastInterval >= broadcastIntervalDiff && broadcastIntervalDiff > BROADCAST_MIN) {
            currDevice.setBroadcastInterval(broadcastIntervalDiff);
        } else {
            long diff = broadcastIntervalDiff - broadcastInterval;
            if (diff <= BROADCAST_DIFF_RANGE_1 && diff >= BROADCAST_DIFF_RANGE_2) {
                if (broadcastIntervalDiff <= BROADCAST_MIN) {
                    currDevice.setBroadcastInterval(BROADCAST_MIN + new Random().nextInt(10));
                } else {
                    currDevice.setBroadcastInterval(broadcastIntervalDiff);
                }
            } else {
                if (broadcastIntervalDiff <= BROADCAST_MIN) {
                    currDevice.setBroadcastInterval(BROADCAST_MIN + new Random().nextInt(10));
                } else {
                    currDevice.setBroadcastInterval(broadcastInterval);
                }
            }
        }
        return currDevice;
    }

}
