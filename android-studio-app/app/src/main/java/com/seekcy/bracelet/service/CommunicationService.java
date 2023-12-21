package com.seekcy.bracelet.service;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.dfuupgrade.DfuUpgradeHandle;
import com.ble.dfuupgrade.callback.IDfuProgressCallback;
import com.ble.dfuupgrade.enums.FirmwareUpgradeStatusEnum;
import com.db.database.AppDatabase;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.callback.DBCallback;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.CommunicationDataService;
import com.db.database.service.RealTimeDataService;
import com.db.database.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.data.constants.JsBridgeConstants;
import com.seekcy.bracelet.data.constants.SharePreferenceConstants;
import com.seekcy.bracelet.data.entity.vo.CurrDayLastInfoVO;
import com.seekcy.bracelet.data.entity.vo.DeviceInfoVO;
import com.seekcy.bracelet.data.entity.vo.FirmwaresUpgradeVO;
import com.seekcy.bracelet.data.entity.vo.FirstTwoWeekAndMonthDataVO;
import com.seekcy.bracelet.data.enums.AgreementEnum;
import com.seekcy.bracelet.data.enums.HistoryDataTypeEnum;
import com.seekcy.bracelet.data.holder.DeviceHolder;
import com.seekcy.bracelet.response.RespVO;
import com.seekcy.bracelet.scheduled.task.WriteCommandTask;
import com.seekcy.bracelet.utils.DataConvertUtil;
import com.seekcy.bracelet.utils.DateUtil;
import com.seekcy.bracelet.utils.HistoryDataAnalysisUtil;
import com.seekcy.bracelet.utils.JsBridgeUtil;
import com.seekcy.bracelet.utils.LogUtil;
import com.seekcy.bracelet.utils.SdkUtil;
import com.seekcy.bracelet.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CommunicationService {
    private static CommunicationService INSTANCE = null;
    private static final String TAG = CommunicationService.class.getSimpleName();

    private static final Gson GSON = new Gson();

    public static CommunicationService getInstance() {
        if (Objects.isNull(INSTANCE))
            INSTANCE = new CommunicationService();
        return INSTANCE;
    }

    /**
     * 统计前两周数据
     *
     * @return
     */
    public FirstTwoWeekAndMonthDataVO statisticalFirstTwoWeekAndMonthData() {
        // 第一次使用时间
        HistoryDataAnalysisUtil.setFirstUseTime();
        Integer currWeek = HistoryDataAnalysisUtil.getStepAveByDateType(true, 0);
        Integer lastWeek = HistoryDataAnalysisUtil.getStepAveByDateType(true, 1);

        Integer currMonth = HistoryDataAnalysisUtil.getStepAveByDateType(false, 0);
        Integer lastMonth = HistoryDataAnalysisUtil.getStepAveByDateType(false, 1);
        FirstTwoWeekAndMonthDataVO vo = new FirstTwoWeekAndMonthDataVO();
        vo.setCurrWeek(currWeek);
        vo.setLastWeek(lastWeek);
        vo.setCurrMonth(currMonth);
        vo.setLastMonth(lastMonth);

        return vo;
    }

    public void loadingDeviceHolder() {
        // 读取电量
        try {
            SdkUtil.writeCommand(AgreementEnum.DEVICE_INFO.getRequestCommand(null));
            TimeUnit.MILLISECONDS.sleep(100);
            readBattery();
            TimeUnit.MILLISECONDS.sleep(500);
            readRealTime();
            TimeUnit.MILLISECONDS.sleep(100);
            timing();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void loadingTodayDeviceHistoryData() {
        for (HistoryDataTypeEnum typeEnum : HistoryDataTypeEnum.getAllEnable()) {
            writeCommand(typeEnum, DateUtil.getCurrentDateHex(0));
        }
    }

    public void loadingBeforeToday() {
        // 这边需要获取历史
        for (int i = 1; i < 3; i++) {
            for (HistoryDataTypeEnum typeEnum : HistoryDataTypeEnum.getAllEnable()) {
                // 如果缓存中存在了 并且数量一致 则不去获取
                writeCommand(typeEnum, DateUtil.getCurrentDateHex(i));
            }
            // 全天数据 只需要历史的
            writeCommand(HistoryDataTypeEnum.TOTAL_DATA, DateUtil.getCurrentDateHex(i));
        }
    }

    /**
     * 写入命令
     *
     * @param typeEnum
     * @param dateHex
     */
    private void writeCommand(HistoryDataTypeEnum typeEnum, String dateHex) {
        List<String> instructions = HistoryDataTypeEnum.getInstructions(typeEnum, dateHex);
        String formatDate = DateUtils.formatDate(ProtocolUtil.hexStrToBytes(dateHex));

        List<String> needList = new ArrayList<>();
        for (int i = 0; i < instructions.size(); i++) {
            String instruction = instructions.get(i);

            CommunicationDataDO communicationDataDO = CommunicationDataCache.get(Integer.parseInt(formatDate), typeEnum.getKeyDes());

            if (null == communicationDataDO || !communicationDataDO.getCompleteData()) {
                needList.add(instruction);
            }
        }

        if (CollectionUtils.isNotEmpty(needList)) {
            new WriteCommandTask(needList).execute();
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

    /**
     * 保存实时信息
     */
    public void saveRealTimeInfo() {
        DeviceHolder.DeviceInfo info = DeviceHolder.getInstance().getInfo();
        RealTimeDataService.getInstance().save(GSON.toJson(info));
    }

    /**
     * 获取deviceInfo
     */
    public DeviceInfoVO getDeviceInfo() {
        DeviceDO deviceDO = DeviceService.getInstance().queryInUseDeviceDO();
        if (Objects.isNull(deviceDO)) {
            return null;
        }
        String paramsJson = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.DEVICE_HOLDER_KEY);
        if (StringUtils.isBlank(paramsJson)) {
            paramsJson = RealTimeDataService.getInstance().query();
        }

        if (StringUtils.isBlank(paramsJson)) {
            return null;
        }

        try {
            DeviceHolder.DeviceInfo deviceInfo = new Gson().fromJson(paramsJson, DeviceHolder.DeviceInfo.class);
            if (StringUtils.isBlank(deviceInfo.getModel())) {
                deviceInfo.setModel(deviceDO.getModel());
                deviceInfo.setFirmwareVersion(deviceDO.getFirmwareVersion());
            }
            LogUtil.info("设备信息时间：" + deviceInfo.getTime());

            if (deviceInfo.getTime() > DateUtil.getTodayStartTime()) {
                DeviceHolder.getInstance().setInfo(deviceInfo);
                return new DeviceInfoVO(deviceInfo);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reloadCommand() {
        CommunicationService.getInstance().loadingTodayDeviceHistoryData();
        CommunicationService.getInstance().loadingBeforeToday();
        BleHandler.of().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommunicationDataService.getInstance().cacheDataInit(new DBCallback() {
                    @Override
                    public void success() {
                        Log.i("CacheScheduled", "刷新数据库成功");
                    }

                    @Override
                    public void failed() {
                        Log.i("CacheScheduled", "刷新数据库失败");
                    }
                });
            }
        }, 20000);
    }

    /**
     * 初始化数据
     */
    public void initData() {
        if (CommunicationDataCache.getInstance().getCacheMap().isEmpty()) {
            CommunicationDataService.getInstance().cacheDataInit(new DBCallback() {
                @Override
                public void success() {

                }

                @Override
                public void failed() {

                }
            });
        }
    }

    /**
     * 查询历史数据
     *
     * @param type     类型
     * @param dateType 数据日期类型 1 天 2 周 3 月
     */
    public Object queryHistoryData(String type, Integer dateType, Integer index) {

        HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(type);

        // 根据类型获取所有数据
        switch (typeEnum) {
            case STEP:
            case BLOOD_PRESSURE:
            case TEMPERATURE:
            case HEART_RATE:
            case BLOOD_OXYGEN:
                return HistoryDataAnalysisUtil.getChartData(type, dateType, index);
            case CALORIE:
                return RespVO.success(HistoryDataAnalysisUtil.statisticalDataByDay(type, index));
            default:
                break;
        }
        return null;
    }

    private void generateTestData() {
        List<CommunicationDataDO> doList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 300; i++) {
            Integer previousIntDate = DateUtils.getPreviousIntDate(i + 3);
            CommunicationDataDO dataDO = new CommunicationDataDO();
            dataDO.setType("04");

            String[] list = new String[24];
            for (int j = 0; j < 24; j++) {
                int i1 = random.nextInt(500);
                list[j] = String.valueOf(i1);
            }

            String join = String.join(",", list);
            dataDO.setData(join);
            dataDO.setDataDate(previousIntDate);
            dataDO.setCompleteData(true);
            doList.add(dataDO);
        }

        AppDatabase.getInstance().getCommunicationDataDAO().insert(doList.toArray(new CommunicationDataDO[0]));

    }

    public CurrDayLastInfoVO queryCurrDayLastInfo() {

        CurrDayLastInfoVO vo = new CurrDayLastInfoVO();
        ConcurrentMap<String, CommunicationDataDO> map = CommunicationDataCache.get(DateUtils.getPreviousIntDate());
        vo.setBloodOxygen(Double.valueOf(getLastValue(map.get(HistoryDataTypeEnum.BLOOD_OXYGEN.getKeyDes()), false, false)).intValue());
        vo.setTemperature(getLastValue(map.get(HistoryDataTypeEnum.TEMPERATURE.getKeyDes()), false, false));
        vo.setHighPressure(Double.valueOf(getLastValue(map.get(HistoryDataTypeEnum.BLOOD_PRESSURE.getKeyDes()), true, false)).intValue());
        vo.setLowPressure(Double.valueOf(getLastValue(map.get(HistoryDataTypeEnum.BLOOD_PRESSURE.getKeyDes()), true, true)).intValue());

        CommunicationDataDO communicationDataDO = map.get(HistoryDataTypeEnum.HEART_RATE.getKeyDes());
        vo.setHeartRate(Double.valueOf(getLastValue(communicationDataDO, false, false)).intValue());
        vo.setHeartRateList(DataConvertUtil.getHeartRateStringToxIntList(communicationDataDO));
        return vo;
    }


    private double getLastValue(CommunicationDataDO communicationDataDO, boolean needParity, boolean isEvenNumber) {
        if (Objects.isNull(communicationDataDO)) {
            return 0.0;
        }
        String data = communicationDataDO.getData();
        String[] numbers = data.split(",");

        for (int i = numbers.length - 1; i >= 0; i--) {
            if (needParity && ((isEvenNumber && i % 2 != 0) || (!isEvenNumber && i % 2 == 0))) {
                continue;
            }
            double num = Double.parseDouble(numbers[i]);
            if (num != 0) {
                return num;
            }
        }

        return 0.0;
    }

    /**
     * dfu固件升级
     */
    public void startDufUpgrade() {
        String address = StringUtils.incrementMacAddress(DeviceHolder.DEVICE.getAddress());
        DfuUpgradeHandle.getInstance().start(Config.mainContext, address, new IDfuProgressCallback() {
            @Override
            public void onDfuCompleted(String deviceAddress) {
                Log.i(TAG, "升级成功" + deviceAddress);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.success(FirmwareUpgradeStatusEnum.UPGRADE_SUCCESS.getKey()));
                reconnect();
            }

            @Override
            public void onDfuAborted(String deviceAddress) {
                Log.i(TAG, "升级失败" + deviceAddress);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.failed(FirmwareUpgradeStatusEnum.UPGRADE_FAILED.getKey(), "升级失败"));
                reconnect();
            }

            @Override
            public void onError(String deviceAddress, int error, int errorType, String message) {
                Log.e(TAG, "升级错误 : " + message);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.failed(FirmwareUpgradeStatusEnum.UPGRADE_FAILED.getKey(), message));
                reconnect();
            }

            @Override
            public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
                Log.i(TAG, "升级进度" + deviceAddress + "" + (50 + (percent / 2)));
                Log.i(TAG, "升级进度" + deviceAddress + "" + percent);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.success(FirmwareUpgradeStatusEnum.UPGRADING.getKey(), percent));
            }

            @Override
            public void onDfuProcessStarted(String deviceAddress) {
                Log.i(TAG, "升级开始" + deviceAddress);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.success(FirmwareUpgradeStatusEnum.START_UPGRADE.getKey()));
            }

            @Override
            public void onDeviceConnecting(String deviceAddress) {
                Log.i(TAG, "升级连接中" + deviceAddress);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.success(FirmwareUpgradeStatusEnum.CONNECTING.getKey()));
            }

            @Override
            public void onDeviceConnected(String deviceAddress) {
                Log.i(TAG, "升级连接成功" + deviceAddress);
                JsBridgeUtil.pushEvent(JsBridgeConstants.FIRMWARE_UPGRADE_KEY, FirmwaresUpgradeVO.success(FirmwareUpgradeStatusEnum.CONNECT_SUCCESS.getKey()));
            }

            @Override
            public void onDeviceDisconnecting(String deviceAddress) {
                Log.i(TAG, "升级断开连接" + deviceAddress);
                reconnect();
            }

            @Override
            public void onDeviceDisconnected(String deviceAddress) {
                Log.i(TAG, "设备未连接" + deviceAddress);
            }
        });
    }

    /**
     * 重新连接设备
     */
    private void reconnect() {
        BleHandler.of().postDelayed(() -> {
            DfuUpgradeHandle.getInstance().dispose(Config.mainContext);
            DeviceHolder.getInstance().getBleManager().connectToDevice();
        }, 5000);
    }
}
