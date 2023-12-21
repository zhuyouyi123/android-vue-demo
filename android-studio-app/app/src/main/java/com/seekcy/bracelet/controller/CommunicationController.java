package com.seekcy.bracelet.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.dfuupgrade.DfuUpgradeHandle;
import com.ble.dfuupgrade.MyBleManager;
import com.ble.dfuupgrade.callback.ConCallback;
import com.db.database.AppDatabase;
import com.db.database.service.AllDayDataService;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.annotation.AppRequestMethod;
import com.seekcy.bracelet.callback.ConnectCallback;
import com.seekcy.bracelet.connect.DeviceConnectHandle;
import com.seekcy.bracelet.data.constants.SharePreferenceConstants;
import com.seekcy.bracelet.data.entity.vo.ComplianceDaysVO;
import com.seekcy.bracelet.data.entity.vo.CurrDayLastInfoVO;
import com.seekcy.bracelet.data.entity.vo.DeviceInfoVO;
import com.seekcy.bracelet.data.entity.vo.FirstTwoWeekAndMonthDataVO;
import com.seekcy.bracelet.data.entity.vo.InitVO;
import com.seekcy.bracelet.data.enums.AgreementEnum;
import com.seekcy.bracelet.data.holder.DeviceHolder;
import com.seekcy.bracelet.response.RespVO;
import com.seekcy.bracelet.service.CommunicationService;
import com.seekcy.bracelet.service.PermissionService;
import com.seekcy.bracelet.utils.DataConvertUtil;
import com.seekcy.bracelet.utils.SdkUtil;
import com.seekcy.bracelet.utils.StringUtils;

import java.util.List;
import java.util.Objects;

@AppController(path = "communication")
@RequiresApi(api = Build.VERSION_CODES.N)
public class CommunicationController {

    private static long refreshTime = System.currentTimeMillis();

    @AppRequestMapper(path = "/init", method = AppRequestMethod.POST)
    public RespVO<InitVO> init(Boolean reload) {
        InitVO initVO = PermissionService.getInstance().checkPermission();

        // 未连接时 连接设备
        boolean needConnect = Objects.isNull(DeviceHolder.DEVICE) || DeviceHolder.DEVICE.getConnectState() != DeviceHolder.CONNECTED;
        if (needConnect) {
            String address = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.DEVICE_ADDRESS_KEY);
            if (StringUtils.isBlank(address)) {
                return RespVO.success(initVO);
            }
            if (initVO.getBluetoothEnable() && initVO.getLocateEnable()) {
                // 绑定设备
                DeviceConnectHandle.getInstance().bind(address, new ConnectCallback() {
                    @Override
                    public void success(String address) {

                    }

                    @Override
                    public void failed() {

                    }
                });
            } else {
                AppDatabase.init(Config.mainContext, address);
            }
            // 初始化数据
            CommunicationService.getInstance().initData();
        } else {
            loadingDeviceInfo();
            if (Boolean.TRUE.equals(reload)) {
                // 防止刷新太快
                if (System.currentTimeMillis() - refreshTime < 5000) {
                    return RespVO.success(initVO);
                }
                refreshTime = System.currentTimeMillis();
                CommunicationService.getInstance().reloadCommand();
            }
        }
        return RespVO.success(initVO);
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

    @AppRequestMapper(path = "/week-and-month")
    public RespVO<FirstTwoWeekAndMonthDataVO> queryWeekAndMonthData() {
        FirstTwoWeekAndMonthDataVO vo = CommunicationService.getInstance().statisticalFirstTwoWeekAndMonthData();
        return RespVO.success(vo);
    }

    @AppRequestMapper(path = "/curr-day-last-info")
    public RespVO<CurrDayLastInfoVO> queryCurrDayLastInfo() {
        return RespVO.success(CommunicationService.getInstance().queryCurrDayLastInfo());
    }


    @AppRequestMapper(path = "/reset", method = AppRequestMethod.POST)
    public RespVO<Void> reset() {
        if (Objects.isNull(DeviceHolder.DEVICE) || DeviceHolder.DEVICE.getConnectState() != BleConnectStatusEnum.CONNECTED.getStatus()) {
            return RespVO.failure("当前设备未连接");
        }
        SdkUtil.writeCommand(AgreementEnum.RESET.getRequestCommand(null));
        return RespVO.success();
    }

    @AppRequestMapper(path = "/write-command", method = AppRequestMethod.POST)
    public RespVO<Void> writeCommand(String hex) {
        if (Objects.isNull(DeviceHolder.DEVICE) || DeviceHolder.DEVICE.getConnectState() != BleConnectStatusEnum.CONNECTED.getStatus()) {
            return RespVO.failure("当前设备未连接");
        }
        SdkUtil.retryWriteCommand(hex);
        return RespVO.success();
    }

    @AppRequestMapper(path = "/dfu-upgrade", method = AppRequestMethod.POST)
    public RespVO<Void> startDufUpgrade() {
        BleHandler.of().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommunicationService.getInstance().startDufUpgrade();
            }
        }, 5000);
        return RespVO.success();
    }
}
