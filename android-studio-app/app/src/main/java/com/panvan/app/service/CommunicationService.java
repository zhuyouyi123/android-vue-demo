package com.panvan.app.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.db.database.AppDatabase;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.service.CommunicationDataService;
import com.db.database.utils.DateUtils;
import com.panvan.app.callback.WriteCallback;
import com.panvan.app.data.enums.AgreementEnum;
import com.panvan.app.data.enums.HistoryDataTypeEnum;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.utils.DateUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.SdkUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CommunicationService {
    private static CommunicationService INSTANCE = null;
    private static final String TAG = CommunicationService.class.getSimpleName();

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static CommunicationService getInstance() {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new CommunicationService();
        return INSTANCE;
    }

    public void loadingDeviceHolder() {
        // 读取电量
        try {
            readBattery();
            TimeUnit.MILLISECONDS.sleep(100);
            readRealTime();
            TimeUnit.MILLISECONDS.sleep(100);
            timing();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void loadingTodayDeviceHistoryData() {
        for (HistoryDataTypeEnum typeEnum : HistoryDataTypeEnum.values()) {
            writeCommand(typeEnum, DateUtil.getCurrentDateHex(0));
        }
    }

    public void loadingBeforeToday() {
        // 这边需要获取历史6天的数据
        for (int i = 1; i < 3; i++) {
            for (HistoryDataTypeEnum typeEnum : HistoryDataTypeEnum.values()) {
                // 如果缓存中存在了 并且数量一致 则不去获取
                ConcurrentMap<String, CommunicationDataDO> concurrentMap = CommunicationDataCache.get(Integer.parseInt(DateUtils.getPreviousDate(i)));

                int packetNum = 0;
                for (Map.Entry<String, CommunicationDataDO> dataDOEntry : concurrentMap.entrySet()) {
                    if (dataDOEntry.getKey().startsWith(typeEnum.getKeyDes())) {
                        packetNum++;
                    }
                }

                if (concurrentMap.isEmpty() || packetNum != typeEnum.getTotalPacket()) {
                    writeCommand(typeEnum, DateUtil.getCurrentDateHex(i));
                }
            }
        }
    }

    /**
     * 写入命令
     *
     * @param typeEnum
     * @param dateHex
     */
    private void writeCommand(HistoryDataTypeEnum typeEnum, String dateHex) {
        try {
            List<String> instructions = HistoryDataTypeEnum.getInstructions(typeEnum, dateHex);
            String formatDate = DateUtils.formatDate(ProtocolUtil.hexStrToBytes(dateHex));
            for (int i = 0; i < instructions.size(); i++) {
                String instruction = instructions.get(i);

                // CommunicationDataDO communicationDataDO = CommunicationDataCache.get(Integer.parseInt(formatDate), typeEnum.getKeyDes(), i + 1);

                // if (Objects.isNull(communicationDataDO) || !communicationDataDO.getCompleteData()) {
                // if (Objects.isNull(communicationDataDO) ) {
                    LogUtil.info("写入数据：" + instruction);
                    SdkUtil.writeCommand(instruction);
                    TimeUnit.MILLISECONDS.sleep(20);
                // }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void readBattery() {
        SdkUtil.writeCommand(AgreementEnum.BATTERY.getRequestCommand(null));
    }

    public void readRealTime() {
        SdkUtil.writeCommand(AgreementEnum.REAL_TIME.getRequestCommand(null));
    }

    private void timing() {
        SdkUtil.writeCommand(AgreementEnum.TIMING.getRequestCommand(null));
    }

    public void readHistoryStepData() {

        List<String> instructions = HistoryDataTypeEnum.getInstructions(HistoryDataTypeEnum.STEP, DateUtil.getCurrentDateHex(1));

        for (String instruction : instructions) {
            try {
                LogUtil.info("写入数据：" + instruction);
                SdkUtil.writeCommand(instruction);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
