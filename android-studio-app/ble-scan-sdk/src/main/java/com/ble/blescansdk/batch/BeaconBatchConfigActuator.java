package com.ble.blescansdk.batch;

import com.ble.blescansdk.batch.callback.BeaconConfigCallback;
import com.ble.blescansdk.batch.entity.BeaconConfig;
import com.ble.blescansdk.batch.helper.BeaconConfigHelper;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.db.enums.BatchConfigResultEnum;
import com.ble.blescansdk.db.enums.BatchConfigTypeEnum;
import com.ble.blescansdk.db.helper.BatchConfigRecordHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeaconBatchConfigActuator {
    private static BeaconBatchConfigActuator INSTANCE = null;

    /**
     * 执行器状态
     */
    private boolean state;

    private static String secretKey;
    private static String oldSecretKey;

    public static BeaconBatchConfigActuator getInstance() {

        if (null == INSTANCE) {
            INSTANCE = new BeaconBatchConfigActuator();
        }

        return INSTANCE;
    }

    /**
     * 待升级map
     */
    private static final ConcurrentMap<String, String> TO_BE_CINFIGURED_MAP = new ConcurrentHashMap<>();

    /**
     * 执行结果
     */
    private static final ConcurrentMap<String, ExecutorResult> EXECUTOR_RESULT_MAP = new ConcurrentHashMap<>();

    private static List<BeaconConfig> beaconConfigList = new ArrayList<>();

    /**
     * 待升级队列
     */
    public static BlockingQueue<RunnableTask> workQueue = null;
    /**
     * 任务执行定时器
     */
    private static ScheduledExecutorService scheduledExecutorService = null;
    private static ExecutorService executorService = null;

    public static int CURR_OPERATION_TYPE = BatchConfigTypeEnum.CHANNEL.getCode();

    public static final int BATCH_CONFIG_CHANNEL = BatchConfigTypeEnum.CHANNEL.getCode();
    public static final int BATCH_CONFIG_SECRET_KEY = BatchConfigTypeEnum.SECRET_KEY.getCode();


    public static final BeaconConfigCallback CALLBACK = new BeaconConfigCallback() {
        @Override
        public void success(String address) {
            System.out.println("执行成功------");
            TO_BE_CINFIGURED_MAP.remove(address);
            EXECUTOR_RESULT_MAP.put(address, ExecutorResult.success());
            BatchConfigRecordHelper.save(address, BatchConfigResultEnum.SUCCESS, BatchConfigTypeEnum.getByCode(CURR_OPERATION_TYPE), 0);
            BeaconConfigHelper.getInstance().restartBeacon();
            BeaconConfigHelper.getInstance().release();
        }

        @Override
        public void fail(String address, int code) {
            System.out.println("执行失败------" + code);
            TO_BE_CINFIGURED_MAP.remove(address);
            EXECUTOR_RESULT_MAP.put(address, ExecutorResult.fail(code));
            BatchConfigRecordHelper.save(address, BatchConfigResultEnum.FAIL, BatchConfigTypeEnum.getByCode(CURR_OPERATION_TYPE), code);
            BeaconConfigHelper.getInstance().restartBeacon();
            BeaconConfigHelper.getInstance().release();
        }
    };

    /**
     * 初始化
     *
     * @param addressList 地址列表
     * @param configs     通道配置信息
     * @param secret      秘钥
     * @return 初始化结果
     */
    public boolean channelConfigInit(List<String> addressList, List<BeaconConfig> configs, String secret) {
        if (CollectionUtils.isEmpty(configs)) {
            return false;
        }

        beaconConfigList = new ArrayList<>(configs);
        CURR_OPERATION_TYPE = BATCH_CONFIG_CHANNEL;
        return initParams(addressList, secret);
    }

    public boolean secretKeyConfigInit(List<String> addressList, String oldSecret, String newSecret) {
        if (StringUtils.isBlank(oldSecret) || StringUtils.isBlank(newSecret)) {
            return false;
        }

        oldSecretKey = oldSecret;
        CURR_OPERATION_TYPE = BATCH_CONFIG_SECRET_KEY;

        return initParams(addressList, newSecret);
    }


    private boolean initParams(List<String> addressList, String secret) {
        if (CollectionUtils.isEmpty(addressList)) {
            return false;
        }
        secretKey = secret;
        workQueue = new LinkedBlockingQueue<>();
        TO_BE_CINFIGURED_MAP.clear();
        EXECUTOR_RESULT_MAP.clear();
        for (String address : addressList) {
            workQueue.add(new RunnableTask(address));
            TO_BE_CINFIGURED_MAP.put(address, address);
            EXECUTOR_RESULT_MAP.put(address, ExecutorResult.executing());
        }

        executorService = Executors.newSingleThreadExecutor();

        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        return true;
    }


    public boolean start() {
        if (null == executorService) {
            return false;
        }
        state = true;
        executorService.submit(() -> {
            while (true) {
                RunnableTask nextTask = workQueue.poll();
                if (null == nextTask) {
                    break;
                }
                nextTask.run();
            }
            executorService.shutdown();
            scheduledExecutorService.shutdown();
            state = false;
        });
        return true;
    }

    /**
     * 获取执行结果
     *
     * @return 结果
     */
    public List<ExecutorResult> getExecutorResultList() {
        return new ArrayList<>(EXECUTOR_RESULT_MAP.values());
    }


    public static class RunnableTask implements Runnable {
        private final String address;

        public RunnableTask(String mac) {
            this.address = mac;
        }

        @Override
        public void run() {
            SeekStandardDevice device = new SeekStandardDevice();
            device.setAddress(address);

            if (CURR_OPERATION_TYPE == BATCH_CONFIG_CHANNEL) {
                BeaconConfigHelper.getInstance().init(device, beaconConfigList, secretKey, "");
            } else {
                BeaconConfigHelper.getInstance().init(device, new ArrayList<>(), secretKey, oldSecretKey);
            }

            BeaconConfigHelper.getInstance().execute();
            while (TO_BE_CINFIGURED_MAP.containsKey(address)) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    BeaconConfigHelper.Result result = BeaconConfigHelper.getInstance().getResult();
                    if (BeaconConfigHelper.EXECUTION_SUCCESS == result.getCode()) {
                        CALLBACK.success(address);
                        break;
                    } else if (BeaconConfigHelper.EXECUTION_FAIL == result.getCode()) {
                        CALLBACK.fail(address, result.getErrorCode());
                        break;
                    }
                    // 等待任务执行结束
                } catch (InterruptedException e) {
                    BeaconConfigHelper.getInstance().restartBeacon();
                    BeaconConfigHelper.getInstance().release();
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static class ExecutorResult {
        private static final int SUCCESS = 0;
        private static final int EXECUTING = 1;
        private static final int FAIL = -1;

        private int state;
        private int errorCode;

        public int getState() {
            return state;
        }

        public ExecutorResult(int state, int errorCode) {
            this.state = state;
            this.errorCode = errorCode;
        }

        public static ExecutorResult success() {
            return new ExecutorResult(SUCCESS, 0);
        }

        public static ExecutorResult fail(int errorCode) {
            return new ExecutorResult(FAIL, errorCode);
        }

        public static ExecutorResult executing() {
            return new ExecutorResult(EXECUTING, 0);
        }

    }


}
