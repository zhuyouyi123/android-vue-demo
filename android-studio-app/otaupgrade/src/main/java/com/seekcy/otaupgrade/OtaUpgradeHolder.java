package com.seekcy.otaupgrade;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.seekcy.otaupgrade.callback.UpgradeCallback;
import com.seekcy.otaupgrade.entity.BleDeviceInfoVO;
import com.seekcy.otaupgrade.queue.OtaUpgradeQueue;
import com.seekcy.otaupgrade.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * OtaUpgradeHolder
 *
 * @author zhuyouyi
 * @date 2023年02月03日
 */
public class OtaUpgradeHolder {
    /**
     * 蓝牙扫描开关
     */
    public static boolean bleScanSwitch = false;
    /**
     * 存储蓝牙扫描的设备
     */
    public static ConcurrentMap<String, BleDeviceInfoVO> BLE_DEVICES = new ConcurrentHashMap<>();

    public static String ADDRESS = "";

    private static UpgradeCallback upgradeCallback = null;

    /**
     * 筛选条件
     */
    public static String FILTER_CONDITION = "";

    public static void init(String mac) {
        bleScanSwitch = true;
        BLE_DEVICES = new ConcurrentHashMap<>();
        if (StringUtils.isBlank(mac)) {
            FILTER_CONDITION = "";
        } else {
            FILTER_CONDITION = mac;
        }
    }

    public static void stop() {
        bleScanSwitch = false;
    }


    /**
     * 获取设备列表
     *
     * @return List<SKYBeaconScanRaw>
     */
    public static List<BleDeviceInfoVO> devices() {
        return new ArrayList<>(BLE_DEVICES.values());
    }

    public static BleDeviceInfoVO device(String mac) {
        ADDRESS = mac;
        return BLE_DEVICES.get(mac);
    }

    @SuppressLint("MissingPermission")
    public static void startUpgrade(UpgradeCallback callback) {
        upgradeCallback = callback;
        if (OtaUpgradeQueue.isEmpty()) {
            stopOta();
            return;
        }
        BleDeviceInfoVO deviceInfoVO = OtaUpgradeQueue.getFirst();
        if (null == deviceInfoVO) {
            stopOta();
            return;
        }

        BluetoothDevice bluetoothDevice = deviceInfoVO.getBluetoothDevice();
        // 配对
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        defaultAdapter.startDiscovery();
        upgradeCallback.connecting();
        OtaHelper.getInstance().connect(bluetoothDevice);
    }

    public static UpgradeCallback getUpgradeCallback() {
        return upgradeCallback;
    }

    private static void stopOta() {
        upgradeCallback = null;
        // 设置停止ota
        OtaHelper.getInstance().disconnect();
        OtaHelper.getInstance().stopOta();
        OtaHelper.getInstance().setOtaRunning(false);
    }

}
