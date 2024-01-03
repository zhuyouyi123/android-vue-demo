package com.seekcy.otaupgrade;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

// import androidx.annotation.NonNull;
//
// import com.ble.telinkotalib.ble.GattConnection;
// import com.ble.telinkotalib.ble.OtaController;
// import com.ble.telinkotalib.foundation.OtaProtocol;
// import com.ble.telinkotalib.foundation.OtaSetting;
// import com.ble.telinkotalib.foundation.OtaStatusCode;
// import com.ble.telinkotalib.util.Arrays;
// import com.joysuch.vue_android.db.database.AppDatabase;
// import com.joysuch.vue_android.db.dataobject.OtaUpgradeRecordDO;
// import com.joysuch.vue_android.entity.holder.OtaUpgradeHolder;
// import com.joysuch.vue_android.entity.queue.OtaUpgradeQueue;
// import com.joysuch.vue_android.enums.OtaUpgradeStatusEnum;
// import com.joysuch.vue_android.utils.DateFormatUtil;
// import com.joysuch.vue_android.vue.Config;

import com.seekcy.otaupgrade.ble.GattConnection;
import com.seekcy.otaupgrade.ble.OtaController;
import com.seekcy.otaupgrade.callback.UpgradeCallback;
import com.seekcy.otaupgrade.enums.OtaUpgradeStatusEnum;
import com.seekcy.otaupgrade.foundation.OtaProtocol;
import com.seekcy.otaupgrade.foundation.OtaSetting;
import com.seekcy.otaupgrade.foundation.OtaStatusCode;
import com.seekcy.otaupgrade.queue.OtaUpgradeQueue;
import com.seekcy.otaupgrade.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * OtaHelper
 *
 * @author zhuyouyi
 * @date 2023年02月07日
 */
public class OtaHelper {

    private static final String TAG = "OtaHelper";
    private OtaSetting otaSetting;

    private GattConnection mGattConnection;

    private OtaController mOtaController;

    private BluetoothDevice device;

    private boolean connectState = false;

    private boolean otaRunning = false;

    @SuppressLint("StaticFieldLeak")
    private static OtaHelper INSTANCE = null;

    private String address = null;

    private final static int MSG_PROGRESS = 11;
    private final static int MSG_INFO = 12;
    private final static int MSG_CONNECTION = 13;
    private final static int MSG_MTU_UPDATE = 14;

    @SuppressLint("StaticFieldLeak")
    private static Context context = null;


    public static OtaHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OtaHelper();
        }
        return INSTANCE;
    }

    public void init(Context AcContext, String filePath) {
        this.updateSettingInfo(filePath);
        context = AcContext;
        if (null == mGattConnection) {
            mGattConnection = new GattConnection(AcContext);
            mGattConnection.setConnectionCallback(CONNECT_CB);
        }

        if (null == mOtaController) {
            mOtaController = new OtaController(mGattConnection);
            mOtaController.setOtaCallback(OTA_CB);
        }
    }

    public void connect(BluetoothDevice bluetoothDevice) {
        device = bluetoothDevice;
        setAddress(bluetoothDevice.getAddress());
        mGattConnection.connect(device);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (null != mGattConnection) {
            mGattConnection.disconnect();
            mGattConnection.clearAll(false);
        }
        connectState = false;
    }

    public void stopOta() {
        mOtaController.stopOta(false);
    }

    /**
     * ota升级
     */
    public void otaUpgrade() {
        mOtaController.startOta(otaSetting);
    }

    /**
     * 连接回调
     */
    private final GattConnection.ConnectionCallback CONNECT_CB = new GattConnection.ConnectionCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(int state, GattConnection gattConnection, int statusCode) {
            connectState = state == BluetoothGatt.STATE_CONNECTED;
            mInfoHandler.obtainMessage(MSG_CONNECTION, state, statusCode).sendToTarget();
            Log.i(TAG, "onConnectionStateChange: " + connectState);
            if (!connectState) {
                mOtaController.stopOta(false);
            } else {
                UpgradeCallback upgradeCallback = OtaUpgradeHolder.getUpgradeCallback();
                if (Objects.nonNull(upgradeCallback)) {
                    upgradeCallback.connected();
                }
            }
        }

        @Override
        public void onNotify(byte[] bytes, UUID uuid, UUID uuid1, GattConnection gattConnection) {
            if (mOtaController != null) {
                mOtaController.pushNotification(bytes);
            }
        }

        @Override
        public void onMtuChanged(int mtu, GattConnection gattConnection) {
            mInfoHandler.obtainMessage(MSG_MTU_UPDATE, mtu).sendToTarget();
        }
    };

    /**
     * ota回调
     */
    private final OtaController.GattOtaCallback OTA_CB = new OtaController.GattOtaCallback() {
        @Override
        public void onOtaStatusChanged(int code, String info, GattConnection gattConnection, OtaController otaController) {
            Log.i(TAG, "onOtaStatusChanged: " + code);
            Message message = mInfoHandler.obtainMessage(MSG_INFO);
            message.arg1 = code;
            message.obj = info;
            message.sendToTarget();
        }

        @Override
        public void onOtaProgressUpdate(int i, GattConnection gattConnection, OtaController otaController) {
            mInfoHandler.obtainMessage(MSG_PROGRESS, i).sendToTarget();
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler mInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_PROGRESS) {
                Log.i(TAG, "设备升级进度更新: " + msg.obj + "%");
                UpgradeCallback upgradeCallback = OtaUpgradeHolder.getUpgradeCallback();
                if (Objects.nonNull(upgradeCallback)) {
                    upgradeCallback.progressChange(String.valueOf(msg.obj));
                }
                // OtaUpgradeHolder.updateStateByMac(device.getAddress(), null, OtaUpgradeStatusEnum.UPGRADING.getState(), String.valueOf(msg.obj));
            } else if (msg.what == MSG_INFO) {
                int code = msg.arg1;
                String info = (String) msg.obj;
                if (code == OtaStatusCode.SUCCESS) {
                    Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "设备升级成功：" + device.getAddress());
                    UpgradeCallback upgradeCallback = OtaUpgradeHolder.getUpgradeCallback();
                    if (Objects.nonNull(upgradeCallback)) {
                        upgradeCallback.success();
                    }
                    // OtaUpgradeHolder.updateStateByMac(device.getAddress(), false, OtaUpgradeStatusEnum.UPGRADE_SUCCEEDED.getState(), "100");
                    saveUpgradeRecord(device.getAddress(), OtaUpgradeStatusEnum.UPGRADE_SUCCEEDED.getState(), null);
                    OtaUpgradeQueue.deleteByMac(device.getAddress());
                    // OtaUpgradeHolder.startUpgrade();
                } else if (code == OtaStatusCode.STARTED) {
                    Log.i(TAG, "设备升级开始：" + device.getAddress());
                    UpgradeCallback upgradeCallback = OtaUpgradeHolder.getUpgradeCallback();
                    if (Objects.nonNull(upgradeCallback)) {
                        upgradeCallback.start();
                    }
                } else {
                    logFailMessage(code);
                    Log.i(TAG, "设备升级失败：" + device.getAddress());
                    // OtaUpgradeHolder.updateStateByMac(device.getAddress(), false, OtaUpgradeStatusEnum.UPGRADE_FAILED.getState(), null);
                    saveUpgradeRecord(device.getAddress(), OtaUpgradeStatusEnum.UPGRADE_FAILED.getState(), code);
                    OtaUpgradeQueue.deleteByMac(device.getAddress());
                    UpgradeCallback upgradeCallback = OtaUpgradeHolder.getUpgradeCallback();
                    if (Objects.nonNull(upgradeCallback)) {
                        upgradeCallback.failed("固件升级失败");
                    }
                    // OtaUpgradeHolder.startUpgrade();
                }
            } else if (msg.what == MSG_CONNECTION) {
                int state = msg.arg1;
                if (state == BluetoothGatt.STATE_CONNECTED) {
                    Log.i(TAG, "蓝牙已连接 " + device.getAddress());
                    // OtaUpgradeHolder.updateStateByMac(device.getAddress(), true, OtaUpgradeStatusEnum.WAITING_TO_UPGRADE.getState(), "0");
                    new Handler().postDelayed(() -> otaUpgrade(), 800);
                } else if (state == BluetoothGatt.STATE_CONNECTING) {
                    Log.i(TAG, "蓝牙正在连接中" + device.getAddress());
                    // OtaUpgradeHolder.updateStateByMac(device.getAddress(), false, null, null);
                } else if (state == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.i(TAG, "蓝牙连接已断开" + device.getAddress());
                    // OtaUpgradeHolder.updateStateByMac(device.getAddress(), false, OtaUpgradeStatusEnum.UPGRADE_FAILED.getState(), null);
                    saveUpgradeRecord(device.getAddress(), OtaUpgradeStatusEnum.UPGRADE_FAILED.getState(), OtaStatusCode.FAIL_UNCONNECTED);
                    // OtaUpgradeHolder.startUpgrade();
                } else {
                    Log.i(TAG, "蓝牙无法连接");
                }

            } else if (msg.what == MSG_MTU_UPDATE) {
                int mtu = (int) msg.obj;
                Log.i(TAG, "handleMessage: " + mtu);
            }
        }
    };

    /**
     * 打印错误原因
     *
     * @param code code
     */
    private void logFailMessage(int code) {
        switch (code) {
            case OtaStatusCode.FAIL_PACKET_SENT_TIMEOUT:
                Log.i(TAG, "FAIL_PACKET_SENT_TIMEOUT: 发送数据包超时");
                break;
            case OtaStatusCode.FAIL_FIRMWARE_CHECK_ERR:
                Log.i(TAG, "FAIL_FIRMWARE_CHECK_ERR: 固件校验失败");
                break;
            case OtaStatusCode.FAIL_UNCONNECTED:
                Log.i(TAG, "FAIL_UNCONNECTED: 连接错误");
                break;
            default:
                Log.i(TAG, "ON_KNOWN: 未知失败原因：" + code);
                break;
        }
    }

    public boolean getConnectState() {
        return connectState;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public boolean isOtaRunning() {
        return otaRunning;
    }

    public void setOtaRunning(boolean otaRunning) {
        if (!otaRunning) {
            Log.i(TAG, "setOtaRunning: ota升级结束");
        }
        this.otaRunning = otaRunning;
    }

    private void updateSettingInfo(String path) {
        if (otaSetting == null) {
            otaSetting = new OtaSetting();
        }
        otaSetting.firmwarePath = path;

        StringBuilder info = new StringBuilder();
        info.append("\tservice: ");
        if (otaSetting.serviceUUID != null) {
            info.append(otaSetting.serviceUUID);
        } else {
            info.append("[use default(1912)]");
        }

        info.append("\n\tcharacteristic: ");
        if (otaSetting.characteristicUUID != null) {
            info.append(otaSetting.characteristicUUID.toString());
        } else {
            info.append("[use default(2B12)]");
        }
        info.append("\n\tread interval: ").append(otaSetting.readInterval);
        info.append("\n\tfile path: ");
        if (otaSetting.firmwarePath != null) {
            info.append(otaSetting.firmwarePath);
        } else {
            info.append("error - file not selected");
        }

        info.append("\n\tsend firmware index: ").append(otaSetting.sendFwIndex);
        if (otaSetting.sendFwIndex) {
            info.append("\n\t\tfirmware index: ").append(String.format("%02X", otaSetting.fwIndex));
        }

        boolean isLegacyProtocol = otaSetting.protocol == OtaProtocol.Legacy;
        info.append("\n\tprotocol: ").append(isLegacyProtocol ? "Legacy" : "Extend");
        if (!isLegacyProtocol) {
            info.append("\n\tversion compare: ").append(otaSetting.versionCompare);

            info.append("\n\tbin version: ").append(Arrays.bytesToHexString(otaSetting.firmwareVersion, ":"));

            info.append("\n\tpdu length: ").append(otaSetting.pduLength);
        }
        Log.i(TAG, info.toString());
    }

    public String getConnectionDesc(int connectionState) {
        switch (connectionState) {
            case BluetoothGatt.STATE_DISCONNECTED:
                return "disconnected";

            case BluetoothGatt.STATE_CONNECTING:
                return "connecting...";

            case BluetoothGatt.STATE_CONNECTED:
                return "connected";

            case BluetoothGatt.STATE_DISCONNECTING:
                return "disconnecting...";

            default:
                return "unknown";
        }
    }

    private void saveUpgradeRecord(String mac, Integer result, Integer errorCode) {
        try {
            // if (!OtaUpgradeQueue.isRetry(mac)) {
            //     Log.i(TAG, "添加固件升级记录：" + mac);
            //     // 添加固件升级记录
            //     OtaUpgradeRecordDO recordDO = new OtaUpgradeRecordDO();
            //     recordDO.setUsername(Config.getUsername());
            //     recordDO.setMac(mac);
            //     recordDO.setResult(Objects.isNull(result) ? OtaUpgradeStatusEnum.WAITING_TO_UPGRADE.getState() : result);
            //     if (Objects.nonNull(errorCode)) {
            //         recordDO.setFailCode(errorCode);
            //     }
            //     recordDO.setUpgradeTime(DateFormatUtil.getCurrentFormatDate());
            //     recordDO.setFirmwareName(OtaSettingService.getInstance().get().getFirmwarePath());
            //
            //     new Thread(() -> AppDatabase.getInstance().getOtaUpgradeRecordDAO().insert(recordDO)).start();
            // }
        } catch (Exception e) {
        }
    }
}
