package com.panvan.app.scheduled;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.db.database.service.CommunicationDataService;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.utils.LogUtil;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeviceHistoryDataUpdateScheduled {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

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
                LogUtil.info("历史数据定时任务执行失败，设备信息为空");
                return;
            }

            LogUtil.info("历史数据定时任务 device连接状态：" + device.getConnectState());

            if (device.getConnectState() != BleConnectStatusEnum.CONNECTED.getStatus()) {
                LogUtil.info("历史数据定时任务执行失败，设备未连接");
            }

        }, 150, 300000, TimeUnit.MILLISECONDS);
    }
}
