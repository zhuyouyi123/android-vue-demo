package com.seek.config.services.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleReadCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.SortTypeEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.seek.config.Config;
import com.seek.config.entity.dto.ScanInitDTO;
import com.seek.config.entity.enums.ErrorEnum;
import com.seek.config.entity.vo.ScanDataVO;
import com.seek.config.services.BleService;
import com.seek.config.utils.SystemUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BleServiceImpl implements BleService {

    private static BleServiceImpl instance = null;

    private static List<SeekStandardDevice> scanDevicesList = new ArrayList<>();


    public static BleServiceImpl getInstance() {
        if (instance == null) {
            instance = new BleServiceImpl();
        }
        return instance;
    }

    @Override
    public void init(ScanInitDTO dto) {
        if (BleSdkManager.isScanning()) {
            BleSdkManager.getInstance().stopScan();
        }

        BleSdkManager.getBleOptions().setSortType(SortTypeEnum.getByType(dto.getSortType()))
                .setFilterInfo(new BleOptions.FilterInfo().setAddress(dto.getAddress()));
    }

    /**
     * 开始扫描
     */
    @Override
    public void startScan() {
        // 如果正在运行 先停止
        if (BleSdkManager.isScanning()) {
            stopScan();
        }
        BleSdkManager.getInstance().startScan(new BleScanCallback<SeekStandardDevice>() {
            @Override
            public void onStatusChange(boolean turnOn) {
            }

            @Override
            public void onScanFailed(int code) {
                scanDevicesList = new ArrayList<>();
                // 扫描失败
                Toasty.error(Config.mainContext.getApplicationContext(), ErrorEnum.getFailMessage(code), 2, false).show();
                if (code == ErrorEnum.LOCATION_INFO_SWITCH_NOT_OPEN.getErrorCode()) {
                    SystemUtil.openGpsLocationSwitch();
                }
            }

            @Override
            public void onLeScan(SeekStandardDevice seekStandardDevice, int i, byte[] bytes) {

            }

            @Override
            public void onLeScan(List<SeekStandardDevice> list) {
                scanDevicesList = list;
            }

            @Override
            public void onRssiMax(SeekStandardDevice seekStandardDevice, int i) {

            }
        });
    }

    /**
     * 停止扫描
     */
    @Override
    public void stopScan() {
        if (BleSdkManager.isScanning()) {
            BleSdkManager.getInstance().stopScan();
        }
    }

    /**
     * 获取扫描设备列表
     *
     * @return 设备列表
     */
    @Override
    public ScanDataVO scanDevices() {
        ScanDataVO vo = new ScanDataVO();
        vo.setScanning(BleSdkManager.isScanning());
        vo.setList(scanDevicesList);
        return vo;
    }

    @Override
    public void connect(String address) {
        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getConnectedDevice(address);

        // 设备已连接
        if (Objects.nonNull(bleDevice) && bleDevice.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            return;
        }
        BleSdkManager.getInstance().releaseGatts();
        SeekStandardDevice device = new SeekStandardDevice();
        device.setAddress(address);
        BleSdkManager.getInstance().connect(device, connectCallback);
    }


    @Override
    public Integer getConnectionStatus(String address) {
        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getConnectedDevice(address);
        if (Objects.isNull(bleDevice)) {
            return BleConnectStatusEnum.DISCONNECT.getStatus();
        }
        return bleDevice.getConnectState();
    }

    @Override
    public void write(String address) {

    }


    private final BleConnectCallback<SeekStandardDevice> connectCallback = new BleConnectCallback<SeekStandardDevice>() {
        @Override
        public void onConnectChange(SeekStandardDevice bleDevice, int i) {
            BleLogUtil.i("连接状态改变" + bleDevice);
        }

        @SuppressLint("CheckResult")
        @Override
        public void onConnectFailed(SeekStandardDevice bleDevice, int errorCode) {
            BleLogUtil.i("连接错误" + bleDevice);
            Toasty.info(Config.mainContext, ErrorEnum.getFailMessage(errorCode));
            BleLogUtil.e("连接错误" + ErrorEnum.getFailMessage(errorCode));
        }
    };

}
