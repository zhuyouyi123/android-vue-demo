package com.seek.config.utils.helper;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.RespVO;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.entity.storage.SeekStandardBeaconStorage;
import com.ble.blescansdk.ble.enums.BeaconCommEnum;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.queue.retry.RetryDispatcher;
import com.ble.blescansdk.ble.utils.AsciiUtil;
import com.ble.blescansdk.ble.utils.BeaconCommUtil;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.google.gson.Gson;
import com.seek.config.Config;
import com.seek.config.entity.bo.ChannelConfigRetryBO;
import com.seek.config.entity.enums.ErrorEnum;
import com.seek.config.utils.I18nUtil;
import com.seek.config.utils.JsBridgeUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import es.dmoral.toasty.Toasty;

public class SeekStandardCommunicationHelper {

    private static final String TAG = SeekStandardCommunicationHelper.class.getSimpleName();

    private static SeekStandardCommunicationHelper instance = null;

    /**
     * 重试map
     */
    private static final ConcurrentHashMap<String, ChannelConfigRetryBO> retryMap = new ConcurrentHashMap<>();

    private static final Handler handle = new Handler();

    private static final Gson GSON = new Gson();

    private static String address = "";
    /**
     * 通知
     */
    private static boolean notify = false;

    /**
     * 是否需要秘钥 -1 未知 0 不需要 1需要
     */
    private static int needSecretKey = -1;


    public static SeekStandardCommunicationHelper getInstance() {
        if (null == instance) {
            instance = new SeekStandardCommunicationHelper();
        }
        return instance;
    }


    private static void initSuccess(String mac) {
        notify = true;
        needSecretKey = -1;
        address = mac;
    }

    private static void initError() {
        notify = false;
        needSecretKey = -1;
        address = "";
    }

    /**
     * 连接设备
     *
     * @param address address
     */
    public void connect(String address) {

        if (SeekStandardDeviceHolder.getInstance().isConnectState()) {
            return;
        }

        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getConnectedDevice(address);

        // 设备已连接
        if (Objects.nonNull(bleDevice) && bleDevice.isConnected()) {
            JsBridgeUtil.pushEvent(JsBridgeUtil.CONNECT_STATUS_CHANGE, JsBridgeUtil.success(bleDevice.getConnectState()));
            return;
        }

        SeekStandardDevice scanDevice = SeekStandardBeaconStorage.getInstance().getScanDevice(address);
        if (Objects.isNull(scanDevice)) {
            scanDevice = new SeekStandardDevice();
            scanDevice.setAddress(address);
            scanDevice.setConnectable(true);
        }

        BleSdkManager.getInstance().connect(scanDevice, connectCallback);
    }

    public void cancelConnect(String address) {

        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getBleDevice(address);

        SeekStandardDeviceHolder.release();

        if (Objects.isNull(bleDevice)) {
            return;
        }

        if (bleDevice.isConnected()) {
            BleSdkManager.getInstance().disconnect(bleDevice);
        }

        BleSdkManager.getInstance().cancelConnecting(bleDevice);

        BleSdkManager.getInstance().releaseGatts();
    }

    /**
     * 开启通知
     *
     * @param address 地址
     */
    public void startNotify(String address) {
        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getBleDevice(address);
        // 如果已连接
        if (bleDevice.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            BleSdkManager.getInstance().startNotify(bleDevice, bleNotifyCallback);
        }
    }

    /**
     * 写入数据
     * 参数 @_1_3_0_!
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void write(String key, String data) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getBleDevice(address);

        BeaconCommEnum commEnum = BeaconCommEnum.getByValue(key);
        if (null != commEnum) {
            // 秘钥校验挥着修改秘钥
            String instructions;
            if (BeaconCommEnum.CHECK_SECRET_REQUEST == commEnum) {
                SharePreferenceUtil.getInstance().shareSet(SharePreferenceUtil.LAST_USE_SECRET_KEY, data);
                instructions = commEnum.getInstructions(data, data);
            } else if (BeaconCommEnum.UPDATE_SECRET_KEY_REQUEST == commEnum) {
                String secretKey = SeekStandardDeviceHolder.getInstance().getSecretKey();
                SharePreferenceUtil.getInstance().shareSet(SharePreferenceUtil.LAST_USE_SECRET_KEY, data);
                SeekStandardDeviceHolder.getInstance().setSecretKey(data);
                instructions = commEnum.getInstructions(secretKey, data);
            } else {
                instructions = commEnum.getInstructions(SeekStandardDeviceHolder.getInstance().getSecretKey(), data);
            }
            BleLogUtil.i(TAG, "写入指令：" + instructions);
            addRetryMap(bleDevice, instructions, commEnum);
            BleSdkManager.getInstance().write(bleDevice, instructions, bleWriteCallback);
        }
    }

    private static void addRetryMap(SeekStandardDevice bleDevice, String instructions, BeaconCommEnum commEnum) {
        ChannelConfigRetryBO channelConfigRetryBO = new ChannelConfigRetryBO();
        BleLogUtil.e("RETRY_HELPER", "add retry map:" + " instruction:" + instructions);
        channelConfigRetryBO.setDevice(bleDevice);
        channelConfigRetryBO.setInstruct(instructions);
        channelConfigRetryBO.setAgreementNumber(commEnum.getCode());
        String[] split = instructions.split("_");
        String channelNumber = "NULL";
        if (split.length > 2 && split[2].equals("10")) {
            channelNumber = split[4];
        }
        channelConfigRetryBO.setChannelNumber(channelNumber);

        String key = commEnum.getCode() + "_" + channelNumber;

        retryMap.put(key, channelConfigRetryBO);

        addRetryTask(key);

        BleLogUtil.e("RETRY_HELPER", "add retry map key::" + key);
    }

    private static void removeRetryMap(String receiveMsg) {
        BleLogUtil.e("RETRY_HELPER", "remove retry map:" + " receiveMsg:" + receiveMsg);
        String[] split = receiveMsg.split("_");
        String agreementNumber = split[1];

        String channelNumber = "NULL";
        if (split.length > 2 && agreementNumber.equals("10")) {
            channelNumber = split[3];
        }

        String key = agreementNumber + "_" + channelNumber;

        retryMap.remove(key);

        BleLogUtil.e("RETRY_HELPER", "remove retry map key:" + key);
    }

    public static void addRetryTask(String key) {
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isBlank(key)) {
                    return;
                }
                if (!retryMap.containsKey(key)) {
                    return;
                }

                ChannelConfigRetryBO channelConfigRetryBO = retryMap.get(key);

                if (null == channelConfigRetryBO) {
                    retryMap.remove(key);
                    return;
                }
                int retryCount = channelConfigRetryBO.getRetryCount();

                if (retryCount == 0) {
                    retryMap.remove(key);
                    return;
                }
                channelConfigRetryBO.setRetryCount(retryCount - 1);
                retryMap.put(key, channelConfigRetryBO);

                BleSdkManager.getInstance().write(channelConfigRetryBO.getDevice(), channelConfigRetryBO.getInstruct(), bleWriteCallback);
                addRetryTask(key);
                BleLogUtil.e("RETRY_HELPER", "retry write:" + key);
            }
        }, 500);
    }

    /**
     * 连接回调
     */
    private final BleConnectCallback<SeekStandardDevice> connectCallback = new BleConnectCallback<SeekStandardDevice>() {
        @Override
        public void onConnectChange(SeekStandardDevice bleDevice, int i) {
            // 还在尝试重新连接
            int retryCountByAddress = RetryDispatcher.getInstance().getRetryCountByAddress(bleDevice.getAddress());

            if (retryCountByAddress == -1 && bleDevice.getConnectState() == 0) {
                JsBridgeUtil.pushEvent(JsBridgeUtil.CONNECT_STATUS_CHANGE, JsBridgeUtil.success(0));
            }
        }

        @Override
        public void onConnectSuccess(SeekStandardDevice device) {
//            if (device.isConnected()) {
//                Toasty.success(Config.mainContext, I18nUtil.getMessage(I18nUtil.CONNECT_SUCCESS)).show();
//            }
            JsBridgeUtil.pushEvent(JsBridgeUtil.CONNECT_STATUS_CHANGE, JsBridgeUtil.success(device.getConnectState()));
        }

        @Override
        public void onConnectFailed(SeekStandardDevice bleDevice, int errorCode) {
            if (bleDevice.isConnected()) {
                JsBridgeUtil.pushEvent(JsBridgeUtil.CONNECT_STATUS_CHANGE, JsBridgeUtil.success(bleDevice.getConnectState()));
                return;
            }
            if (ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode() == errorCode) {
                int retryCountByAddress = RetryDispatcher.getInstance().getRetryCountByAddress(bleDevice.getAddress());
                if (retryCountByAddress > 0) {
                    RetryDispatcher.getInstance().onConnectFailed(bleDevice, errorCode);
                    return;
                }
            } else if (ErrorStatusEnum.BLUETOOTH_ALREADY_CONNECTED.getErrorCode() == errorCode) {
                Toasty.success(Config.mainContext, ErrorEnum.getFailMessage(errorCode)).show();
                JsBridgeUtil.pushEvent(JsBridgeUtil.CONNECT_STATUS_CHANGE, JsBridgeUtil.success(1));
            }
            Toasty.error(Config.mainContext, ErrorEnum.getFailMessage(errorCode)).show();
            JsBridgeUtil.pushEvent(JsBridgeUtil.CONNECT_STATUS_CHANGE, JsBridgeUtil.fail(bleDevice.getConnectState()));
        }
    };


    private static final BleNotifyCallback<SeekStandardDevice> bleNotifyCallback = new BleNotifyCallback<SeekStandardDevice>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onChanged(SeekStandardDevice o, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            try {
                byte[] characteristicValue = bluetoothGattCharacteristic.getValue();
                String receiveMsg = AsciiUtil.convertHexToString(ProtocolUtil.byteArrToHexStr(characteristicValue));

                removeRetryMap(receiveMsg);

                BeaconCommEnum commEnum = BeaconCommUtil.getCommType(characteristicValue);
                if (null == commEnum) {
                    return;
                }
                RespVO handle = null;
                // 为了方便排查问题 不直接只使用commEnum调用 不然一句话就可以搞定 现使用Switch
                switch (commEnum) {
                    case CHECK_SECRET_RESULT:
                        // 秘钥检验结果
                        handle = BeaconCommEnum.CHECK_SECRET_RESULT.handle(characteristicValue);
                        break;
                    case READ_FACTORY_VERSION_INFO_RESPONSE:
                        // 读取出厂信息应答
                        handle = BeaconCommEnum.READ_FACTORY_VERSION_INFO_RESPONSE.handle(characteristicValue);
                        break;
                    case READ_FACTORY_VERSION_INFO_RESULT:
                        // 读取出厂信息结果
                        handle = BeaconCommEnum.READ_FACTORY_VERSION_INFO_RESULT.handle(characteristicValue);
                        break;
                    case READ_FEATURE_INFO_RESPONSE:
                        // 读取特征信息应答
                        handle = BeaconCommEnum.READ_FEATURE_INFO_RESPONSE.handle(characteristicValue);
                        break;
                    case READ_FEATURE_INFO_RESULT:
                        // 读取特征信息结果
                        handle = BeaconCommEnum.READ_FEATURE_INFO_RESULT.handle(characteristicValue);
                        break;
                    case RESTORE_FACTORY_SETTINGS_RESPONSE:
                        // 恢复出厂设置应答
                        handle = BeaconCommEnum.RESTORE_FACTORY_SETTINGS_RESPONSE.handle(characteristicValue);
                        break;
                    case RESTORE_FACTORY_SETTINGS_RESULT:
                        // 恢复出厂设置结果
                        handle = BeaconCommEnum.RESTORE_FACTORY_SETTINGS_RESULT.handle(characteristicValue);
                        break;
                    case SHUTDOWN_RESULT:
                        // 关机
                        handle = BeaconCommEnum.SHUTDOWN_RESULT.handle(characteristicValue);
                        break;
                    case REMOVE_SECRET_KEY_RESULT:
                        // 删除秘钥
                        handle = BeaconCommEnum.REMOVE_SECRET_KEY_RESULT.handle(characteristicValue);
                        break;
                    case UPDATE_SECRET_KEY_RESULT:
                        // 修改秘钥
                        handle = BeaconCommEnum.UPDATE_SECRET_KEY_RESULT.handle(characteristicValue);
                        break;
                    case CHANNEL_CONFIG_BEACON_RESPONSE:
                        handle = BeaconCommEnum.CHANNEL_CONFIG_BEACON_RESPONSE.handle(characteristicValue);
                        break;
                    case CHANNEL_CONFIG_BEACON_RESULT:
                        handle = BeaconCommEnum.CHANNEL_CONFIG_BEACON_RESULT.handle(characteristicValue);
                        break;
                    case NEED_SECRET_CONNECT_REQUEST:
                        // 秘钥
                        handle = BeaconCommEnum.NEED_SECRET_CONNECT_REQUEST.handle(characteristicValue);
                        break;
                    case NEED_SECRET_CONNECT_RESULT:
                        // 秘钥结果
                        handle = BeaconCommEnum.NEED_SECRET_CONNECT_RESULT.handle(characteristicValue);
                        break;
                    case QUERY_CONFIG_AGREEMENT_RESULT:
                        handle = BeaconCommEnum.QUERY_CONFIG_AGREEMENT_RESULT.handle(characteristicValue);
                        List<SeekStandardDeviceHolder.AgreementInfo> agreementInfoList = SeekStandardDeviceHolder.getInstance().getAgreementInfoList();
                        if (CollectionUtils.isEmpty(agreementInfoList) || agreementInfoList.size() != 6) {
                            BleLogUtil.i(TAG, "key：" + commEnum.getValue() + " type：" + commEnum.getType() + " received：" + receiveMsg + " result：" + GSON.toJson(handle));
                            return;
                        }
                        break;
                    case NOT_CONNECTABLE_CONFIG_RESULT:
                        handle = BeaconCommEnum.NOT_CONNECTABLE_CONFIG_RESULT.handle(characteristicValue);
                        break;
                    case TRIGGER_RESPONSE_TIME_CONFIG_RESULT:
                        handle = BeaconCommEnum.TRIGGER_RESPONSE_TIME_CONFIG_RESULT.handle(characteristicValue);
                        break;
                    case RESTART_BEACON_RESULT:
                        handle = BeaconCommEnum.RESTART_BEACON_RESULT.handle(characteristicValue);
                        break;
                    default:
                        break;
                }

                BleLogUtil.i(TAG, "key：" + commEnum.getValue() + " type：" + commEnum.getType() + " received：" + receiveMsg + " result：" + GSON.toJson(handle));

                JsBridgeUtil.pushEvent(JsBridgeUtil.NOTIFY_STATUS_CHANGE, JsBridgeUtil.success(handle, commEnum.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
                BleLogUtil.e("handle error");
            }
        }

        @Override
        public void onNotifySuccess(SeekStandardDevice device) {
            BleLogUtil.i("Start Notify Success");
            initSuccess(device.getAddress());
            JsBridgeUtil.pushEvent(JsBridgeUtil.START_NOTIFY_RESULT, JsBridgeUtil.success());
        }

        @Override
        public void onNotifyFailed(SeekStandardDevice device, int failedCode) {
            BleLogUtil.i("Start Notify Failed");
            initError();
            JsBridgeUtil.pushEvent(JsBridgeUtil.START_NOTIFY_RESULT, JsBridgeUtil.fail());
        }

        @Override
        public void onNotifyCanceled(SeekStandardDevice device) {
            BleLogUtil.i("Start Notify Cancelled");
        }
    };


    private static final BleWriteCallback<SeekStandardDevice> bleWriteCallback = new BleWriteCallback<SeekStandardDevice>() {
        @Override
        public void onWriteSuccess(SeekStandardDevice bleDevice, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            BleLogUtil.i("Write Success");
        }

        @Override
        public void onWriteFailed(SeekStandardDevice bleDevice, int code) {
            BleLogUtil.i("Write Failed");
            Toasty.error(Config.mainContext, ErrorEnum.getFailMessage(code)).show();
            JsBridgeUtil.pushEvent(JsBridgeUtil.WRITE_REPLY_RESULT, JsBridgeUtil.ERROR);
        }
    };

}