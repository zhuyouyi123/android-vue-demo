package com.ble.blescansdk.batch.helper;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.batch.BeaconBatchConfigActuator;
import com.ble.blescansdk.batch.entity.BeaconConfig;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.RespVO;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.entity.seek.config.ChannelConfig;
import com.ble.blescansdk.ble.enums.BeaconCommEnum;
import com.ble.blescansdk.ble.enums.BroadcastPowerEnum;
import com.ble.blescansdk.ble.enums.batch.BatchConfigErrorEnum;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
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
import com.ble.blescansdk.db.SdkDatabase;
import com.ble.blescansdk.db.dataobject.SecretKeyDO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeaconConfigHelper {
    private static final String TAG = BeaconConfigHelper.class.getSimpleName();

    private static final char KEY = 's';

    /**
     * 执行中
     */
    public static final int EXECUTING = 1;
    /**
     * 执行失败
     */
    public static final int EXECUTION_FAIL = -1;
    /**
     * 执行成功
     */
    public static final int EXECUTION_SUCCESS = 2;

    private static BeaconConfigHelper INSTANCE = null;

    private static SeekStandardDevice device;
    private static List<BeaconConfig> beaconConfigList;

    /**
     * 配置通道数量
     */
    private static int CONFIG_CHANNEL_NUM = 0;

    private static Result result;

    private static String SECRET_KEY = "CDEFGH";
    private static String OLD_SECRET_KEY = "CDEFGH";
    private static String DATABASE_SECRET_KEY = "";

    private static final Handler handle = new Handler(Looper.getMainLooper());

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static ScheduledExecutorService checkSelfScheduledExecutorService = Executors.newScheduledThreadPool(1);

    private static final ConcurrentMap<String, Long> TIME_OUT_MAP = new ConcurrentHashMap<>();

    /**
     * 读取通道信息序号 范围0-5
     */
    private static int READ_CHANNEL_INFO_INDEX = 0;

    public static BeaconConfigHelper getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new BeaconConfigHelper();
        }
        return INSTANCE;
    }

    public void init(SeekStandardDevice seekStandardDevice, List<BeaconConfig> beaconConfigs, String secretKey, String oldSecretKey) {
        device = seekStandardDevice;
        beaconConfigList = beaconConfigs;

        for (int i = 0; i < beaconConfigList.size(); i++) {
            beaconConfigList.get(i).setChannelNumber(i);
        }

        READ_CHANNEL_INFO_INDEX = 0;
        CONFIG_CHANNEL_NUM = 0;
        SECRET_KEY = secretKey;
        if (StringUtils.isNotBlank(oldSecretKey)) {
            OLD_SECRET_KEY = oldSecretKey;
        }
        if (StringUtils.isBlank(DATABASE_SECRET_KEY)) {
            if (SdkDatabase.supportDatabase) {
                SecretKeyDO secretKeyDO = SdkDatabase.getInstance().getSecretKeyDAO().queryFirst(device.getAddress());
                if (null == secretKeyDO) {
                    DATABASE_SECRET_KEY = "NULL";
                } else {
                    DATABASE_SECRET_KEY = secretKeyDO.getSecretKey();
                }
            }
        }
        result = new Result(EXECUTING, 0);
    }

    public void execute() {
        // 建立连接
        BleSdkManager.getInstance().connect(device, connectCallback);
        if (null != scheduledExecutorService && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        if (null != checkSelfScheduledExecutorService && !checkSelfScheduledExecutorService.isShutdown()) {
            checkSelfScheduledExecutorService.shutdown();
        }
        checkSelfScheduledExecutorService = Executors.newScheduledThreadPool(1);

        startCheckSelfTimeout();

    }

    public void release() {
        if (null != device) {
            BleSdkManager.getInstance().disconnect(device);
            BleSdkManager.getInstance().releaseGatts();
        }
        device = null;
        beaconConfigList = new ArrayList<>();
        SECRET_KEY = "CDEFGH";
        OLD_SECRET_KEY = "";
        DATABASE_SECRET_KEY = "";
        if (null != scheduledExecutorService && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
        if (null != checkSelfScheduledExecutorService && !checkSelfScheduledExecutorService.isShutdown()) {
            checkSelfScheduledExecutorService.shutdown();
        }
        TIME_OUT_MAP.clear();
        READ_CHANNEL_INFO_INDEX = 0;
        CONFIG_CHANNEL_NUM = 0;
        SeekStandardDeviceHolder.release();
    }

    public void startCheckSelfTimeout() {
        checkSelfScheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (Map.Entry<String, Long> longEntry : TIME_OUT_MAP.entrySet()) {
                if (System.currentTimeMillis() - longEntry.getValue() > 5000) {
                    BleLogUtil.i("TIME_OUT_MAP_TIMEOUT:" + longEntry.getKey());
                    result = Result.fail(BatchConfigErrorEnum.COMMAND_EXECUTION_TIMEOUT.getErrorCode());
                }
            }
        }, 3000, 3000, TimeUnit.MILLISECONDS);
    }

    public Result getResult() {
        return result;
    }


    private final BleConnectCallback<SeekStandardDevice> connectCallback = new BleConnectCallback<SeekStandardDevice>() {
        /**
         * @param device ble device object
         * @param status 连接状态
         */
        @Override
        public void onConnectChange(SeekStandardDevice device, int status) {
            Log.i(TAG, "onConnectChange: " + status);
            // 还在尝试重新连接
            int retryCountByAddress = RetryDispatcher.getInstance().getRetryCountByAddress(device.getAddress());

            if (retryCountByAddress == -1 && device.getConnectState() == 0) {
                result = Result.fail(BatchConfigErrorEnum.DEVICE_CONNECTING_FAILED.getErrorCode());
            }

        }

        @Override
        public void onConnectSuccess(SeekStandardDevice device) {
            Log.i(TAG, "onConnectSuccess: 准备开启通知");
            // 连接成功开启通道
            handle.postDelayed(() -> BleSdkManager.getInstance().startNotify(device, bleNotifyCallback), 1200);
        }

        /**
         * @param device    ble device object
         * @param errorCode errorCode
         */
        @Override
        public void onConnectFailed(SeekStandardDevice device, int errorCode) {
            result = Result.fail(BatchConfigErrorEnum.DEVICE_CONNECTING_FAILED.getErrorCode());
        }
    };

    private static final BleNotifyCallback<SeekStandardDevice> bleNotifyCallback = new BleNotifyCallback<SeekStandardDevice>() {
        /**
         * MCU data sent to the app when the data callback call is setNotify
         *
         * @param device         ble device object
         * @param characteristic characteristic
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onChanged(SeekStandardDevice device, BluetoothGattCharacteristic characteristic) {
            byte[] characteristicValue = characteristic.getValue();

            String receiveMsg = AsciiUtil.convertHexToString(ProtocolUtil.byteArrToHexStr(characteristicValue));

            if (BleSdkManager.isEncryption()) {
                char[] chars = receiveMsg.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    chars[i] ^= KEY;
                }
                receiveMsg = String.valueOf(chars);
            }


            removeTimeoutMap(receiveMsg);

            BeaconCommEnum commEnum = BeaconCommUtil.getCommType(receiveMsg);
            if (null == commEnum) {
                return;
            }

            String[] split = BeaconCommUtil.replaceHeadAndTail(receiveMsg).split("_");

            RespVO handle = null;

            try {
                switch (commEnum) {
                    case CHECK_SECRET_RESULT:
                        // 秘钥检验结果
                        handle = BeaconCommEnum.CHECK_SECRET_RESULT.handle(split);
                        boolean isBatchConfigSecretKey = BeaconBatchConfigActuator.CURR_OPERATION_TYPE == BeaconBatchConfigActuator.BATCH_CONFIG_SECRET_KEY;
                        if (handle.getCode() == 0) {
                            if (StringUtils.isNotBlank(DATABASE_SECRET_KEY) && !"NULL".equals(DATABASE_SECRET_KEY)) {
                                if (isBatchConfigSecretKey) {
                                    OLD_SECRET_KEY = DATABASE_SECRET_KEY;
                                } else {
                                    SECRET_KEY = DATABASE_SECRET_KEY;
                                }
                            }
                            if (isBatchConfigSecretKey) {
                                write(BeaconCommEnum.UPDATE_SECRET_KEY_REQUEST, OLD_SECRET_KEY, SECRET_KEY);
                            } else {
                                // 读取出厂信息
                                write(BeaconCommEnum.READ_FACTORY_VERSION_INFO_REQUEST, "");
                            }
                        } else {
                            // 秘钥检验失败
                            if (StringUtils.isNotBlank(DATABASE_SECRET_KEY)) {
                                String secret = isBatchConfigSecretKey ? OLD_SECRET_KEY : SECRET_KEY;
                                write(BeaconCommEnum.CHECK_SECRET_REQUEST, secret, secret);
                            } else {
                                result = Result.fail(BatchConfigErrorEnum.SECRET_KEY_ERROR.getErrorCode());
                            }
                            DATABASE_SECRET_KEY = "";
                        }
                        break;
                    case READ_FACTORY_VERSION_INFO_RESPONSE:
                        // 读取出厂信息应答
                        handle = BeaconCommEnum.READ_FACTORY_VERSION_INFO_RESPONSE.handle(split);
                        if (handle.getCode() != 0) {
                            result = Result.fail(BatchConfigErrorEnum.FACTORY_INFORMATION_READING_FAILED.getErrorCode());
                        }
                        break;
                    case READ_FACTORY_VERSION_INFO_RESULT:
                        // 读取出厂信息结果
                        handle = BeaconCommEnum.READ_FACTORY_VERSION_INFO_RESULT.handle(split);
                        if (handle.getCode() != 0) {
                            result = Result.fail(BatchConfigErrorEnum.FACTORY_INFORMATION_READING_FAILED.getErrorCode());
                        } else {
                            // 读取通道信息
                            new Thread(new TimerTask() {
                                @Override
                                public void run() {
                                    while (READ_CHANNEL_INFO_INDEX < 6) {
                                        write(BeaconCommEnum.QUERY_CONFIG_AGREEMENT_REQUEST, String.valueOf(READ_CHANNEL_INFO_INDEX));
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        READ_CHANNEL_INFO_INDEX++;
                                    }
                                    // 校验是不是读取全了
                                    List<SeekStandardDeviceHolder.AgreementInfo> agreementInfoList = SeekStandardDeviceHolder.getInstance().getAgreementInfoList();
                                    if (CollectionUtils.isEmpty(agreementInfoList)) {
                                        result = Result.fail(BatchConfigErrorEnum.FAILED_TO_READ_CHANNEL_INFORMATION.getErrorCode());

                                    } else if (agreementInfoList.size() != 6) {
                                        Map<Integer, String> retryMap = new HashMap<>();
                                        for (int i = 0; i < 6; i++) {
                                            retryMap.put(i, "");
                                        }
                                        for (SeekStandardDeviceHolder.AgreementInfo info : agreementInfoList) {
                                            retryMap.remove(info.getChannelNumber());
                                        }
                                        // 进行二次重试
                                        Log.i(TAG, "run: " + retryMap.keySet());
                                        for (Integer integer : retryMap.keySet()) {
                                            write(BeaconCommEnum.QUERY_CONFIG_AGREEMENT_REQUEST, String.valueOf(integer));
                                            try {
                                                TimeUnit.MILLISECONDS.sleep(200);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }).start();
                        }
                        break;
                    case UPDATE_SECRET_KEY_RESULT:
                        // 修改秘钥
                        handle = BeaconCommEnum.UPDATE_SECRET_KEY_RESULT.handle(split);
                        if (handle.getCode() == 0) {
                            result = Result.success();
                        } else {
                            result = Result.fail(BatchConfigErrorEnum.KEY_CONFIGURATION_FAILED.getErrorCode());
                        }
                        break;
                    case CHANNEL_CONFIG_BEACON_RESPONSE:
                        handle = BeaconCommEnum.CHANNEL_CONFIG_BEACON_RESPONSE.handle(split);
                        break;
                    case CHANNEL_CONFIG_BEACON_RESULT:
                        handle = BeaconCommEnum.CHANNEL_CONFIG_BEACON_RESULT.handle(split);
                        if (handle.getCode() == 0) {
                            CONFIG_CHANNEL_NUM--;
                            Log.i(TAG, "onChanged: CONFIG_CHANNEL_NUM" + CONFIG_CHANNEL_NUM);
                            if (CONFIG_CHANNEL_NUM <= 0) {
                                result = Result.success();
                            }
                        } else {
                            result = Result.fail(BatchConfigErrorEnum.CHANNEL_CONFIGURATION_FAILED.getErrorCode());
                        }
                        break;
                    case NEED_SECRET_CONNECT_REQUEST:
                        // 秘钥
                        handle = BeaconCommEnum.NEED_SECRET_CONNECT_REQUEST.handle(split);
                        break;
                    case NEED_SECRET_CONNECT_RESULT:
                        // 秘钥结果
                        handle = BeaconCommEnum.NEED_SECRET_CONNECT_RESULT.handle(split);
                        if (handle.getCode() == 0 && (boolean) handle.getData()) {
                            // 需要秘钥 校验秘
                            if ("NULL".equals(DATABASE_SECRET_KEY)) {
                                DATABASE_SECRET_KEY = "";
                                if (BeaconBatchConfigActuator.CURR_OPERATION_TYPE == BeaconBatchConfigActuator.BATCH_CONFIG_SECRET_KEY && StringUtils.isNotBlank(OLD_SECRET_KEY)) {
                                    write(BeaconCommEnum.CHECK_SECRET_REQUEST, OLD_SECRET_KEY, OLD_SECRET_KEY);
                                } else {
                                    write(BeaconCommEnum.CHECK_SECRET_REQUEST, SECRET_KEY, SECRET_KEY);
                                }
                            } else {
                                write(BeaconCommEnum.CHECK_SECRET_REQUEST, DATABASE_SECRET_KEY, DATABASE_SECRET_KEY);
                            }
                        } else {
                            if (BeaconBatchConfigActuator.CURR_OPERATION_TYPE == BeaconBatchConfigActuator.BATCH_CONFIG_SECRET_KEY) {
                                write(BeaconCommEnum.UPDATE_SECRET_KEY_REQUEST, SECRET_KEY, SECRET_KEY);
                            } else {
                                // 读取出厂信息
                                write(BeaconCommEnum.READ_FACTORY_VERSION_INFO_REQUEST, "");
                            }
                        }
                        break;
                    case QUERY_CONFIG_AGREEMENT_RESULT:
                        handle = BeaconCommEnum.QUERY_CONFIG_AGREEMENT_RESULT.handle(split);
                        List<SeekStandardDeviceHolder.AgreementInfo> agreementInfoList = SeekStandardDeviceHolder.getInstance().getAgreementInfoList();
                        if (CollectionUtils.isNotEmpty(agreementInfoList) && agreementInfoList.size() == 6) {
                            // 通道信息读取完毕
                            if (!checkConfigChannelParams(agreementInfoList)) {
                                return;
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                result = Result.fail(BatchConfigErrorEnum.CHANNEL_CONFIGURATION_FAILED.getErrorCode());
            }
            Log.i(TAG, "onChanged: key:" + commEnum.getValue() + " " + new Gson().toJson(handle));
        }

        @Override
        public void onNotifySuccess(SeekStandardDevice device) {
            // 通知开启成功
            Log.i(TAG, "onNotifySuccess: 通知开启成功");
            // 获取是否需要秘钥
            write(BeaconCommEnum.NEED_SECRET_CONNECT_REQUEST, "");
        }

        @Override
        public void onNotifyFailed(SeekStandardDevice device, int failedCode) {
            Log.i(TAG, "onNotifyFailed: 通知开启失败");
            result = Result.fail(BatchConfigErrorEnum.NOTIFICATION_OPENING_FAILED.getErrorCode());
        }

        @Override
        public void onNotifyCanceled(SeekStandardDevice device) {

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
        }
    };


    public static class Result {
        private final int code;

        private final int errorCode;

        public Result(int code, int errorCode) {
            this.code = code;
            this.errorCode = errorCode;
        }

        public static Result success() {
            return new Result(EXECUTION_SUCCESS, 0);
        }

        public static Result fail(int errorCode) {
            return new Result(EXECUTION_FAIL, errorCode);
        }

        public int getCode() {
            return code;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }


    /**
     * 写入数据
     *
     * @param beaconCommEnum {@link BeaconCommEnum}
     */
    private static void write(BeaconCommEnum beaconCommEnum, String secretKey, String data) {
        try {
            ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
            SeekStandardDevice bleDevice = request.getBleDevice(device.getAddress());
            if (null == bleDevice) {
                result = Result.fail(BatchConfigErrorEnum.DEVICE_WRITING_DATA_ERROR.getErrorCode());
                return;
            }

            if (BeaconCommEnum.UPDATE_SECRET_KEY_REQUEST == beaconCommEnum && StringUtils.isNotBlank(data)) {
                SharePreferenceUtil.getInstance().shareSet(SharePreferenceUtil.LAST_USE_SECRET_KEY, data);
            }

            String instructions = beaconCommEnum.getInstructions(secretKey, data);
            Log.i(TAG, "write: " + instructions);

            String channelNumber = "NULL";
            String[] split = instructions.split("_");
            if (split.length > 2 && split[2].equals("10")) {
                channelNumber = split[4];
            }

            String key = beaconCommEnum.getCode() + "_" + channelNumber;
            TIME_OUT_MAP.put(key, System.currentTimeMillis());
            BleLogUtil.i("TIME_OUT_MAP_ADD:" + key);

            if (BleSdkManager.isEncryption()) {
                BleSdkManager.getInstance().writeEncryption(bleDevice, instructions, bleWriteCallback);
            } else {
                BleSdkManager.getInstance().write(bleDevice, instructions, bleWriteCallback);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = Result.fail(BatchConfigErrorEnum.DEVICE_WRITING_DATA_ERROR.getErrorCode());
        }

    }

    private static void removeTimeoutMap(String receiveMsg) {
        String[] split = receiveMsg.split("_");
        String agreementNumber = split[1];

        String channelNumber = "NULL";
        if (split.length > 2 && agreementNumber.equals("10")) {
            channelNumber = split[3];
        }

        String key = agreementNumber + "_" + channelNumber;

        TIME_OUT_MAP.remove(key);
        BleLogUtil.i("TIME_OUT_MAP_REMOVE:" + key);
    }

    /**
     * 写入数据
     *
     * @param beaconCommEnum {@link BeaconCommEnum}
     */
    private static void write(BeaconCommEnum beaconCommEnum, String data) {
        write(beaconCommEnum, SECRET_KEY, data);
    }

    /**
     * 配置通道
     *
     * @param agreementInfoList 协议列表
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static boolean checkConfigChannelParams(List<SeekStandardDeviceHolder.AgreementInfo> agreementInfoList) {
        Map<Integer, SeekStandardDeviceHolder.AgreementInfo> channelMap = new HashMap<>(6);

        agreementInfoList.forEach(e -> channelMap.put(e.getChannelNumber(), e));

        Set<String> thoroughfareTypeSet = new HashSet<>();
        // 至少有一条通道开启始终广播
        boolean existAlwaysBroadcast = false;
        for (int i = 0; i < beaconConfigList.size(); i++) {
            BeaconConfig beaconConfig = beaconConfigList.get(i);
            if (beaconConfig.getFrameType().equals(ThoroughfareTypeEnum.I_BEACON.getValue())) {
                SeekStandardDeviceHolder.AgreementInfo info = channelMap.get(i);
                if (null == info || !Objects.equals(info.getAgreementType(), ThoroughfareTypeEnum.I_BEACON.getType())) {
                    result = Result.fail(BatchConfigErrorEnum.NOT_SUPPORT_I_BEACON.getErrorCode());
                    return false;
                }
            } else if (ThoroughfareTypeEnum.ACC.getValue().equals(beaconConfig.getFrameType())) {
                if (!SeekStandardDeviceHolder.getInstance().getSupportACC()) {
                    result = Result.fail(BatchConfigErrorEnum.DEVICE_NOT_SUPPORT_ACC.getErrorCode());
                    return false;
                }
            }

            // 触发方式是否支持ACC
            if (beaconConfig.getTriggerSwitch() && beaconConfig.getTriggerCondition() == 3 && !SeekStandardDeviceHolder.getInstance().getSupportACC()) {
                result = Result.fail(BatchConfigErrorEnum.DEVICE_NOT_SUPPORT_ACC.getErrorCode());
                return false;
            }

            if (beaconConfig.getAlwaysBroadcast()) {
                existAlwaysBroadcast = true;
            }
            thoroughfareTypeSet.add(beaconConfigList.get(i).getFrameType());
        }

        if (!existAlwaysBroadcast) {
            result = Result.fail(BatchConfigErrorEnum.AT_LEAST_ONE_ALWAYS_BROADCAST.getErrorCode());
            return false;
        }

        ArrayList<String> arrayList = new ArrayList<>(thoroughfareTypeSet);
        if (arrayList.size() == 1) {
            if (ThoroughfareTypeEnum.CORE_IOT_AOA.getValue().equals(arrayList.get(0))) {
                result = Result.fail(BatchConfigErrorEnum.NOT_ONLY_EXIST_COREAIOT.getErrorCode());
                return false;
            }
        }

        if (agreementInfoList.size() > beaconConfigList.size()) {
            for (int i = beaconConfigList.size(); i < 6; i++) {
                SeekStandardDeviceHolder.AgreementInfo info = channelMap.get(i);
                if (null == info) {
                    continue;
                }
                if (Objects.equals(info.getAgreementType(), ThoroughfareTypeEnum.EMPTY.getType())) {
                    continue;
                }

                BeaconConfig beaconConfig = new BeaconConfig();
                beaconConfig.setChannelNumber(i);
                beaconConfig.setFrameType(ThoroughfareTypeEnum.EMPTY.getValue());
                beaconConfigList.add(beaconConfig);

            }
        }

        CONFIG_CHANNEL_NUM = beaconConfigList.size();
        Log.i(TAG, "checkConfigChannelParams: " + CONFIG_CHANNEL_NUM);
        config(channelMap);
        return true;
    }

    private static void config(Map<Integer, SeekStandardDeviceHolder.AgreementInfo> map) {
        // 配置通道
        new Thread(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                for (BeaconConfig beaconConfig : beaconConfigList) {
                    ChannelConfig config = new ChannelConfig();
                    SeekStandardDeviceHolder.AgreementInfo info = map.get(beaconConfig.getChannelNumber());

                    ThoroughfareTypeEnum thoroughfareTypeEnum = ThoroughfareTypeEnum.getByValue(beaconConfig.getFrameType());
                    config.setFrameType(thoroughfareTypeEnum);
                    config.setChannelNumber(beaconConfig.getChannelNumber());
                    if (thoroughfareTypeEnum == ThoroughfareTypeEnum.EMPTY) {
                        write(BeaconCommEnum.CHANNEL_CONFIG_BEACON_REQUEST, config.getSendMessage());
                        continue;
                    } else if (ThoroughfareTypeEnum.I_BEACON == thoroughfareTypeEnum) {
                        if (null != info) {
                            final boolean responseSwitch = info.getAgreementData().get(3).equals("1");
                            config.setResponseSwitch(responseSwitch);
                            if (responseSwitch && info.getAgreementData().size() > 4) {
                                config.setDeviceName(info.getAgreementData().get(4));
                            }
                            config.setBroadcastData(info.getAgreementData());
                        }
                    } else {
                        config.setBroadcastData(beaconConfig.getBroadcastDataList());
                        config.setDeviceName(beaconConfig.getDeviceName());
                    }

                    config.setBroadcastInterval(beaconConfig.getBroadcastInterval());
                    config.setCalibrationDistance(beaconConfig.getCalibrationDistance());
                    config.setBroadcastPower(BroadcastPowerEnum.getByPower(beaconConfig.getBroadcastPower()).getLevel());
                    config.setTriggerSwitch(beaconConfig.getTriggerSwitch());
                    config.setTriggerCondition(beaconConfig.getTriggerCondition());
                    config.setTriggerBroadcastTime(beaconConfig.getTriggerBroadcastTime());
                    config.setTriggerBroadcastInterval(beaconConfig.getTriggerBroadcastInterval());
                    config.setTriggerBroadcastPower(BroadcastPowerEnum.getByPower(beaconConfig.getTriggerBroadcastPower()).getLevel());
                    config.setAlwaysBroadcast(beaconConfig.getAlwaysBroadcast());

                    write(BeaconCommEnum.CHANNEL_CONFIG_BEACON_REQUEST, config.getSendMessage());

                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    public void restartBeacon() {
        try {
            ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
            SeekStandardDevice bleDevice = request.getBleDevice(device.getAddress());

            if (null != bleDevice && bleDevice.isConnected()) {
                write(BeaconCommEnum.RESTART_BEACON_REQUEST, SECRET_KEY);
                BleLogUtil.i("重启设备命令发送成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
