package com.panvan.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.DeviceDataService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.panvan.app.Config;
import com.panvan.app.Receiver.call.CallViewModel;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.connect.DeviceConnectHandle;
import com.panvan.app.data.constants.ActiveForResultConstants;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.data.constants.PermissionsRequestConstants;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.entity.bo.WatchQrCodeBO;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.service.PermissionService;
import com.panvan.app.service.SystemService;

public class ActivityResultUtil {

    private static final String TAG = ActivityResultUtil.class.getSimpleName();

    @SuppressLint("NewApi")
    public static void handleResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActiveForResultConstants.SCAN_QR_CODE_REQUEST_CODE:
                if (null == data) {
                    return;
                }
                HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
                if (obj != null && StringUtils.isNotBlank(obj.originalValue)) {
                    SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_REQUEST_KEY, obj.originalValue);
                    String scanResult = obj.originalValue.toLowerCase();
                    try {
                        WatchQrCodeBO watchQrCodeBO = new Gson().fromJson(scanResult, WatchQrCodeBO.class);

                        DeviceConnectHandle.getInstance().bind(StringUtils.formatBleAddress(watchQrCodeBO.getMac()), new ConnectCallback() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void success(String address) {
                                // 创建设备
                                DeviceDO deviceDO = new DeviceDO();
                                deviceDO.setAddress(address);
                                deviceDO.setInUse(true);
                                DeviceDataService.getInstance().saveDevice(deviceDO);
                            }

                            @Override
                            public void failed() {
                            }
                        });

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case PermissionsRequestConstants.ACTION_NOTIFICATION_LISTENER_SETTINGS_CODE:
                if (PermissionsUtil.isNotificationListenerEnabled()) {

                }
                break;
            case ActiveForResultConstants.REQUEST_INSTALL_PACKAGES_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    SystemService.getInstance().installApp(false);
                }
                break;
            case ActiveForResultConstants.REQUEST_NOTIFY_CODE:
                BleHandler.of().postDelayed(() -> CommunicationService.getInstance().startDufUpgrade(), 8000);
                break;
        }
    }

    public static void handlePermissions(int requestCode, String[] permissions, int[] grantResults) {

        boolean existPermissions = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (existPermissions) {
            LogUtil.info(TAG, String.format("权限%s已被授予", PermissionsRequestConstants.BLUETOOTH_CONNECT_REQUEST_CODE));
        } else {
            LogUtil.info(TAG, String.format("权限%s已被拒绝", PermissionsRequestConstants.BLUETOOTH_CONNECT_REQUEST_CODE));
        }

        switch (requestCode) {
            case PermissionsRequestConstants.BLUETOOTH_CONNECT_REQUEST_CODE:
                Toast.makeText(Config.mainContext, existPermissions ? "正在绑定设备..." : "添加失败", Toast.LENGTH_SHORT).show();
                break;
            case PermissionsRequestConstants.ACCESS_COARSE_LOCATION_CODE:
            case PermissionsRequestConstants.BASE_REQUEST_CODE:
                if (!existPermissions) {
                    Toast.makeText(Config.mainContext, "权限获取失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case PermissionsRequestConstants.CAMERA_PERMISSION_REQUEST_CODE:
                if (existPermissions) {
                    PermissionService.getInstance().scanQr();
                }
                break;
            case PermissionsRequestConstants.READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
            case PermissionsRequestConstants.READ_CALL_LOG_PERMISSION_REQUEST_CODE:
                if (existPermissions) {
                    new Thread(() -> CallViewModel.getInstance().loadConfig()).start();
                }
                break;
            case PermissionsRequestConstants.APP_LIST_PERMISSION_REQUEST_CODE:
                SystemService.getInstance().openPage();
                break;
            default:
                break;
        }

    }
}
