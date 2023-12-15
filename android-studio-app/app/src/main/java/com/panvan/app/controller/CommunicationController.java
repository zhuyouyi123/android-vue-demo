package com.panvan.app.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.service.AllDayDataService;
import com.db.database.service.CommunicationDataService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.connect.DeviceConnectHandle;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.entity.vo.ComplianceDaysVO;
import com.panvan.app.data.entity.vo.DeviceInfoVO;
import com.panvan.app.data.enums.HistoryDataTypeEnum;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.data.holder.statistics.StepStatisticsInfo;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.utils.DataConvertUtil;
import com.panvan.app.utils.DateUtil;
import com.panvan.app.utils.HistoryDataAnalysisUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@AppController(path = "communication")
@RequiresApi(api = Build.VERSION_CODES.N)
public class CommunicationController {

    private static final ConnectCallback CALLBACK = new ConnectCallback() {
        @Override
        public void success(String address) {
            DeviceHolder.getInstance().setConnectStatus(DeviceHolder.CONNECTED);
        }

        @Override
        public void failed() {
            DeviceHolder.getInstance().setConnectStatus(DeviceHolder.NOT_CONNECTED);
        }
    };

    @AppRequestMapper(path = "/init", method = AppRequestMethod.POST)
    public void init(Boolean reload) {
        // 未连接时 连接设备
        boolean needConnect = DeviceHolder.getInstance().connectStatus() == DeviceHolder.NOT_CONNECTED
                || DeviceHolder.getInstance().connectStatus() == DeviceHolder.CONNECT_FAILED;
        if (needConnect) {
            String address = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.DEVICE_ADDRESS_KEY);
            if (StringUtils.isBlank(address)) {
                return;
            }
            // 绑定设备
            DeviceConnectHandle.getInstance().bind(address, CALLBACK);
            // 初始化数据
            CommunicationService.getInstance().initData();
        } else {
            loadingDeviceInfo();
            if (Boolean.TRUE.equals(reload)) {
                CommunicationService.getInstance().reloadCommand();
            }
        }

    }

    @AppRequestMapper(path = "/loading-device-info", method = AppRequestMethod.POST)
    public void loadingDeviceInfo() {
        CommunicationService.getInstance().loadingDeviceHolder();
    }

    @AppRequestMapper(path = "/loading-device-info")
    public RespVO<DeviceInfoVO> getDeviceInfo() {
        return RespVO.success(CommunicationService.getInstance().getDeviceInfo());
    }

    @AppRequestMapper(path = "/history/list")
    public RespVO<Object> historyList(String type, Integer dateType, Integer index) {
        return RespVO.success(CommunicationService.getInstance().queryHistoryData(type, dateType, index));
    }

    @AppRequestMapper(path = "/compliance/days")
    public RespVO<ComplianceDaysVO> queryComplianceDays() {
        List<Boolean> booleans = AllDayDataService.getInstance().queryComplianceDays();
        ComplianceDaysVO vo = new ComplianceDaysVO();
        vo.setContinueComplianceDays(DataConvertUtil.countConsecutiveOccurrences(booleans));
        vo.setComplianceDays(DataConvertUtil.countOccurrencesInRange(booleans));

        return RespVO.success(vo);
    }


}
