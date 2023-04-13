package com.seek.config.services.impl;

import static android.os.Build.VERSION_CODES.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.enums.SortTypeEnum;
import com.seek.config.Config;
import com.seek.config.R;
import com.seek.config.entity.dto.ScanInitDTO;
import com.seek.config.entity.enums.ErrorEnum;
import com.seek.config.entity.vo.ScanDataVO;
import com.seek.config.services.BleService;
import com.seek.config.utils.I18nUtil;
import com.seek.config.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dev.utils.app.AccessibilityUtils;
import dev.utils.app.AppUtils;
import dev.utils.app.NotificationUtils;
import dev.utils.app.toast.ToastUtils;
import dev.utils.common.ArrayUtils;
import dev.utils.common.CollectionUtils;
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

        BleSdkManager.getBleOptions().setSortType(SortTypeEnum.getByType(dto.getSortType()));
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


}
