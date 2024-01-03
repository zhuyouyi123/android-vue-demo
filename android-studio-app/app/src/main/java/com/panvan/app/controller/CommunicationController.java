package com.panvan.app.controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.dfuupgrade.DfuUpgradeHandle;
import com.ble.dfuupgrade.MyBleManager;
import com.db.database.AppDatabase;
import com.db.database.service.AllDayDataService;
import com.db.database.service.DeviceDataService;
import com.panvan.app.Config;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.connect.DeviceConnectHandle;
import com.panvan.app.data.constants.ActiveForResultConstants;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.entity.dto.SaveFunctionSwitchDTO;
import com.panvan.app.data.entity.vo.ComplianceDaysVO;
import com.panvan.app.data.entity.vo.CurrDayLastInfoVO;
import com.panvan.app.data.entity.vo.DeviceInfoVO;
import com.panvan.app.data.entity.vo.FirstTwoWeekAndMonthDataVO;
import com.panvan.app.data.entity.vo.InitVO;
import com.panvan.app.data.enums.AgreementEnum;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.service.PermissionService;
import com.panvan.app.utils.DataConvertUtil;
import com.panvan.app.utils.SdkUtil;
import com.panvan.app.utils.StringUtils;
import com.seekcy.otaupgrade.OtaHelper;
import com.seekcy.otaupgrade.OtaUpgradeHolder;
import com.seekcy.otaupgrade.callback.UpgradeCallback;
import com.seekcy.otaupgrade.entity.BleDeviceInfoVO;
import com.seekcy.otaupgrade.queue.OtaUpgradeQueue;

import java.io.File;
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
                address = DeviceDataService.getInstance().queryInUse();
                if (StringUtils.isBlank(address)) {
                    return RespVO.success(initVO);
                }
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
        // MyBleManager.getInstance(Config.mainContext).dis();
        DeviceHolder.getInstance().getBleManager().dis(() -> DeviceHolder.getInstance().setBleManager(null));
        DeviceHolder.DEVICE.setConnectState(BluetoothProfile.STATE_DISCONNECTED);
        CommunicationService.getInstance().clearTask();
        CommunicationService.getInstance().removeCurrDayDay();
        return RespVO.success();
    }

    @AppRequestMapper(path = "/write-command", method = AppRequestMethod.POST)
    public RespVO<Void> writeCommand(String hex) {
        if (Objects.isNull(DeviceHolder.DEVICE) || DeviceHolder.DEVICE.getConnectState() != BleConnectStatusEnum.CONNECTED.getStatus()) {
            return RespVO.failure("当前设备未连接");
        }

        if ("B1".equals(hex)) {
            if (!NotificationManagerCompat.from(Config.mainContext).areNotificationsEnabled()) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// android 8.0引导
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", Config.mainContext.getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // android 5.0-7.0
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", Config.mainContext.getPackageName());
                    intent.putExtra("app_uid", Config.mainContext.getApplicationInfo().uid);
                } else {//其它
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", Config.mainContext.getPackageName(), null));
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Activity activity = (Activity) Config.mainContext;
                activity.startActivityForResult(intent, ActiveForResultConstants.REQUEST_NOTIFY_CODE);
                return RespVO.success();
            }
            DfuUpgradeHandle.setIsUpgrading(true);
        }
        SdkUtil.retryWriteCommand(hex);
        return RespVO.success();
    }

    @AppRequestMapper(path = "/function-switch")
    public RespVO<Void> queryFunctionSwitch() {
        CommunicationService.getInstance().queryFunctionSwitch();
        return RespVO.success();
    }

    @AppRequestMapper(path = "/function-switch", method = AppRequestMethod.POST)
    public RespVO<Void> saveFunctionSwitch(SaveFunctionSwitchDTO dto) {
        if (Objects.isNull(dto)) {
            return RespVO.failure("参数为空");
        }

        StringBuilder stringBuilder = new StringBuilder("6802090001");
        stringBuilder.append("07").append(ProtocolUtil.byteToHexStr(dto.getBloodOxygenInterval().byteValue()));
        stringBuilder.append("08").append(dto.getBloodOxygen() ? "01" : "00");


        stringBuilder.append("0B").append(ProtocolUtil.byteToHexStr(dto.getBloodPressureInterval().byteValue()));
        stringBuilder.append("0C").append(dto.getBloodPressure() ? "01" : "00");

        byte[] bytes = ProtocolUtil.hexStrToBytes(stringBuilder.toString());
        byte calcAddSum = ProtocolUtil.calcAddSum(bytes);
        SdkUtil.retryWriteCommand(stringBuilder.toString() + ProtocolUtil.byteToHexStr(calcAddSum) + "16");

        return RespVO.success();
    }

    @AppRequestMapper(path = "/dfu-upgrade", method = AppRequestMethod.POST)
    public RespVO<Void> startDufUpgrade() {
        DfuUpgradeHandle.setIsUpgrading(true);
        DeviceHolder.DEVICE.setConnectState(DeviceHolder.NOT_CONNECTED);
        BleHandler.of().postDelayed(() -> CommunicationService.getInstance().startDufUpgrade(), 6000);
        return RespVO.success();
    }

    /**
     * 固件升级
     *
     * @param address mac地址
     * @return 开始升级
     */
    @AppRequestMapper(path = "/ota-upgrade", method = AppRequestMethod.POST)
    public RespVO<Void> startOtaUpgrade() {
        CommunicationService.getInstance().startOtaUpgrade();
        return RespVO.success();
    }
}
