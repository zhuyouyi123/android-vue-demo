package com.panvan.app.connect;

import android.widget.Toast;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.db.database.AppDatabase;
import com.db.database.UserDatabase;
import com.panvan.app.Config;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.utils.JsBridgeUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.MaskUtil;
import com.panvan.app.utils.PermissionsUtil;
import com.panvan.app.utils.SdkUtil;
import com.panvan.app.utils.StringUtils;

public class DeviceConnectHandle {

    private static DeviceConnectHandle DEVICE_CONNECT_HANDLE = null;

    private String address;

    public static DeviceConnectHandle getInstance() {

        if (null == DEVICE_CONNECT_HANDLE) {
            DEVICE_CONNECT_HANDLE = new DeviceConnectHandle();
        }

        return DEVICE_CONNECT_HANDLE;
    }

    public String getAddress() {
        return address;
    }


    public void bind(String address, ConnectCallback connectCallback) {
        this.address = address;
        AppDatabase.init(Config.mainContext, address);
        JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BINDING_STATUS, JsBridgeConstants.BINDING_STATUS_CONNECTING);

        if (StringUtils.isNotBlank(address)) {
            BraceletDevice braceletDevice = new BraceletDevice();
            braceletDevice.setAddress(address);
            SdkUtil.connect(braceletDevice, connectCallback);
            return;
        }

        // 在你的Activity或者Fragment中
        BleSdkManager.getBleOptions().setFilterInfo(new BleOptions.FilterInfo().setAddress(address));

        // 开始寻找设备
        SdkUtil.scan(new SdkUtil.Callback() {
            @Override
            public void result(BraceletDevice device) {
                LogUtil.info("扫描到了设备");

                SdkUtil.connect(device, connectCallback);
            }

            @Override
            public void failed(int errorCode) {
                // 申请权限
                if (errorCode == ErrorStatusEnum.ACCESS_COARSE_LOCATION_NOT_EXIST.getErrorCode()) {
                    Toast.makeText(Config.mainContext, "需要获取您的大致位置信息以提供相关服务", Toast.LENGTH_SHORT).show();
                    PermissionsUtil.requestAccessCoarseLocation();
                }
            }
        });


    }
}
