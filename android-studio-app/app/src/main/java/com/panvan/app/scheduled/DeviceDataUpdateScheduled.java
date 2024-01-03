package com.panvan.app.scheduled;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.DeviceDataService;
import com.google.gson.Gson;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.StringUtils;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DeviceDataUpdateScheduled {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private static final Gson GSON = new Gson();

    // 定义一个标志位来表示任务的执行状态
    private static final AtomicBoolean isTaskRunning = new AtomicBoolean(false);

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void start() {
        if (isTaskRunning.get()) {
            return;
        }
        isTaskRunning.set(true);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {

            BraceletDevice device = DeviceHolder.DEVICE;
            if (Objects.isNull(device)) {
                LogUtil.info("定时任务执行失败，设备信息为空");
                return;
            }

            if (device.getConnectState() != BleConnectStatusEnum.CONNECTED.getStatus()) {
                LogUtil.info("定时任务执行失败，设备未连接");
                return;
            }

            CommunicationService.getInstance().loadingDeviceHolder();

            BleHandler.of().postDelayed(() -> SharePreferenceUtil.getInstance().shareSet(SharePreferenceConstants.DEVICE_HOLDER_KEY, GSON.toJson(DeviceHolder.getInstance().getInfo())), 1000);

            BleHandler.of().postDelayed(DeviceDataUpdateScheduled::updateDevice, 2000);
        }, 5000, 30000, TimeUnit.MILLISECONDS);
    }

    /**
     * 更新设备信息
     */
    private static void updateDevice() {

        DeviceDataService.getInstance().query(new DeviceDataService.Callback() {
            @Override
            public void success(DeviceDO deviceDO) {
                if (Objects.nonNull(deviceDO)) {
                    DeviceHolder.DeviceInfo info = DeviceHolder.getInstance().getInfo();
                    deviceDO.setBatter(info.getBattery());
                    if (StringUtils.isNotBlank(info.getModel())) {
                        deviceDO.setModel(info.getModel());
                        deviceDO.setFirmwareVersion(info.getFirmwareVersion());
                    }
                    if (StringUtils.isNotBlank(info.getOtaAddress())) {
                        deviceDO.setOtaAddress(info.getOtaAddress());
                        deviceDO.setOtaFirmwareVersion(info.getOtaFirmwareVersion());
                    }

                    // UserDatabase.getInstance().getDeviceDAO().update(deviceDO);
                    DeviceDataService.getInstance().saveDevice(deviceDO);
                }
            }

            @Override
            public void failed() {

            }
        });

    }
}
