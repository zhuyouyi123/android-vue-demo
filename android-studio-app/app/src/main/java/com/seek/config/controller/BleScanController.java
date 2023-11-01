package com.seek.config.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.seek.config.Config;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.ScanDeviceInfoVO;
import com.seek.config.utils.JsBridgeUtil;

import java.util.ArrayList;
import java.util.List;

@AppController(path = "scan")
public class BleScanController {

    @AppRequestMapper(path = "/start", method = AppRequestMethod.POST)
    public RespVO<Void> startScan() {

        if (!checkBluetoothState()) {
            return RespVO.failure("蓝牙未打开");
        }

        if (checkLocationPermission()) {
            return RespVO.failure("请先获取位置权限");
        }

        BleSdkManager.getBleOptions()
                .setBleScanLevel(BleScanLevelEnum.SCAN_MODE_LOW_LATENCY)
                .setContinuousScanning(true)
                .setIntermittentScanning(true)
                .setIntermittentTime(1000)
                .setFilterInfo(new BleOptions.FilterInfo().setNormDevice(true));
        BleSdkManager.getInstance().startScan(new BleScanCallback<SeekStandardDevice>() {

            @Override
            public void onStatusChange(boolean state) {

            }

            @Override
            public void onScanFailed(int errorCode) {

            }

            @Override
            public void onLeScan(SeekStandardDevice device, int rssi, byte[] scanRecord) {

            }

            @Override
            public void onLeScan(List<SeekStandardDevice> device) {
                if (CollectionUtils.isEmpty(device)) {
                    return;
                }

                List<ScanDeviceInfoVO> list = new ArrayList<>();
                for (SeekStandardDevice seekStandardDevice : device) {
                    ScanDeviceInfoVO scanDeviceInfoVO = new ScanDeviceInfoVO(seekStandardDevice.getAddress(), seekStandardDevice.getRssi());
                    scanDeviceInfoVO.setName(seekStandardDevice.getName());
                    list.add(scanDeviceInfoVO);
                }

                JsBridgeUtil.pushEvent(JsBridgeUtil.SCAN_DEVICE_LIST_RESULT, list);
            }

            @Override
            public void onRssiMax(SeekStandardDevice device, int rssi) {

            }
        });

        return RespVO.success();
    }

    @AppRequestMapper(path = "/stop", method = AppRequestMethod.POST)
    public RespVO<Void> stopScan() {
        if (!checkBluetoothState()) {
            return RespVO.failure("蓝牙未打开");
        }

        if (checkLocationPermission()) {
            return RespVO.failure("请先获取位置权限");
        }
        BleSdkManager.getInstance().stopScan();
        return RespVO.success();
    }

    /**
     * 查看蓝牙状态
     *
     * @return
     */
    public boolean checkBluetoothState() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (null != bluetoothAdapter && bluetoothAdapter.isEnabled());
    }

    /**
     * 校验位置权限是否打开
     *
     * @return 开关
     */
    private boolean checkLocationPermission() {
        LocationManager locationManager = (LocationManager) Config.mainContext.getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!providerEnabled) {
            new AlertDialog.Builder(Config.mainContext)
                    .setTitle("获取位置信息失败")
                    .setMessage("未开启位置信息，是否前往开启")
                    .setNegativeButton("取消", (dialog, which) -> Toast.makeText(Config.mainContext, "未开启位置信息，无法使用本服务", Toast.LENGTH_SHORT).show())
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ((Activity) Config.mainContext).startActivityForResult(intent, 1);
                        dialogInterface.dismiss();
                    }).show();
            return true;
        }
        return false;
    }

}
