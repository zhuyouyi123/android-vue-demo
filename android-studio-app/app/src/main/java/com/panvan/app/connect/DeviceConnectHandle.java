package com.panvan.app.connect;

import android.widget.Toast;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.dfuupgrade.MyBleManager;
import com.ble.dfuupgrade.callback.ConCallback;
import com.db.database.AppDatabase;
import com.panvan.app.Config;
import com.panvan.app.callback.ConnectCallback;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.utils.JsBridgeUtil;
import com.panvan.app.utils.LogUtil;
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
        }
    }
}
