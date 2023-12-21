package com.seekcy.bracelet.connect;

import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.db.database.AppDatabase;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.callback.ConnectCallback;
import com.seekcy.bracelet.data.constants.JsBridgeConstants;
import com.seekcy.bracelet.utils.JsBridgeUtil;
import com.seekcy.bracelet.utils.SdkUtil;
import com.seekcy.bracelet.utils.StringUtils;

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
