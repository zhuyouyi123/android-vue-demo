package com.panvan.app.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.daoobject.CommunicationDataDO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.connect.DeviceConnectHandle;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.entity.vo.DeviceInfoVO;
import com.panvan.app.data.enums.HistoryDataTypeEnum;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.data.holder.statistics.StepStatisticsInfo;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.utils.DateUtil;
import com.panvan.app.utils.HistoryDataAnalysisUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@AppController(path = "communication")
@RequiresApi(api = Build.VERSION_CODES.N)
public class CommunicationController {

    @AppRequestMapper(path = "/init", method = AppRequestMethod.POST)
    public void init() {
        // 未连接时 连接设备
        boolean needConnect = DeviceHolder.getInstance().connectStatus() == DeviceHolder.NOT_CONNECTED
                || DeviceHolder.getInstance().connectStatus() == DeviceHolder.CONNECT_FAILED;
        if (needConnect) {
            String address = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.DEVICE_ADDRESS_KEY);
            if (StringUtils.isBlank(address)) {
                return;
            }

            DeviceConnectHandle.getInstance().bind(address, new ConnectCallback() {
                @Override
                public void success(String address) {

                }

                @Override
                public void failed() {

                }
            });

        } else {
            CommunicationService.getInstance().loadingDeviceHolder();
        }

    }

    @AppRequestMapper(path = "/loading-device-info", method = AppRequestMethod.POST)
    public void loadingDeviceInfo() {
        CommunicationService.getInstance().loadingDeviceHolder();
    }

    @AppRequestMapper(path = "/loading-device-info")
    public RespVO<DeviceInfoVO> getDeviceInfo() {
        String shareGet = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.DEVICE_HOLDER_KEY);
        if (StringUtils.isNotBlank(shareGet)) {
            try {
                DeviceHolder.DeviceInfo deviceInfo = new Gson().fromJson(shareGet, DeviceHolder.DeviceInfo.class);

                LogUtil.info("设备信息时间：" + deviceInfo.getTime());

                if (deviceInfo.getTime() > DateUtil.getTodayStartTime()) {
                    return RespVO.success(new DeviceInfoVO(deviceInfo));
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return RespVO.success();
    }

    @AppRequestMapper(path = "/history/list")
    public RespVO<Object> historyList(String type) {
        HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(type);
        switch (typeEnum) {
            case STEP:
                // 根据类型获取所有数据
                return RespVO.success(HistoryDataAnalysisUtil.getChartDataByType(type).stream()
                        .map(e -> new StepStatisticsInfo<>(e.getDate(), e.getHourlyData()))
                        .collect(Collectors.toList()));
            case CALORIE:
                // 根据类型获取所有数据
                return RespVO.success(HistoryDataAnalysisUtil.getChartDataByType(type));
            case HEART_RATE:
                return RespVO.success(HistoryDataAnalysisUtil.getChartDataByType(type));
            case BLOOD_OXYGEN:
            case TEMPERATURE:
                return RespVO.success(HistoryDataAnalysisUtil.getChartDataByType(type));
            // case ATMOSPHERIC_PRESSURE:
            //     break;
            //     data = HistoryDataAnalysisUtil.analysisTemperature();
            //     break;
            default:
                break;
        }

        return RespVO.success();
    }


}
