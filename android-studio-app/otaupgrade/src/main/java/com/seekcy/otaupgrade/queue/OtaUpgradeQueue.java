package com.seekcy.otaupgrade.queue;

import android.util.Log;


import com.seekcy.otaupgrade.entity.BleDeviceInfoVO;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OtaUpgradeQueue {
    /**
     * 待升级队列
     */

    private static final Map<String, BleDeviceInfoVO> upgradeMap = new HashMap<>();
    private static final Map<String, Integer> retryMap = new HashMap<>();

    public static void add(BleDeviceInfoVO scanRaw) {
        if (Objects.isNull(scanRaw)) {
            return;
        }
        upgradeMap.put(scanRaw.getMac(), scanRaw);
        retryMap.put(scanRaw.getMac(), 2);
    }

    public static boolean isEmpty() {
        return upgradeMap.isEmpty();
    }

    public static BleDeviceInfoVO getFirst() {
        for (String key : upgradeMap.keySet()) {
            Integer retryTimes = retryMap.get(key);
            if (null == retryTimes || retryTimes == 0) {
                upgradeMap.remove(key);
                retryMap.remove(key);
            } else {
                BleDeviceInfoVO bleDeviceInfoVO = upgradeMap.get(key);
                if (null != bleDeviceInfoVO) {
                    retryTimes--;
                    if (retryTimes == 0) {
                        Log.i("OtaUpgradeQueue", "当前为重试" + key);
                    }
                    retryMap.put(key, retryTimes);
                    return bleDeviceInfoVO;
                }
            }

        }
        return null;
    }

    /**
     * 判断是否为重试
     *
     * @param mac 设备mac
     * @return 重试
     */
    public static boolean isRetry(String mac) {
        boolean containsKey = retryMap.containsKey(mac);
        if (containsKey) {
            Integer integer = retryMap.get(mac);
            return null != integer && integer == 0;
        }
        return true;
    }

    public static BleDeviceInfoVO getByMac(String mac) {
        if (upgradeMap.containsKey(mac)) {
            return upgradeMap.get(mac);
        }
        return null;
    }

    public static void deleteByMac(String mac) {
        Log.i("OtaUpgradeQueue", "deleteByMac: " + mac);
        upgradeMap.remove(mac);
        retryMap.remove(mac);
    }

    public static void clear() {
        upgradeMap.clear();
    }

}
