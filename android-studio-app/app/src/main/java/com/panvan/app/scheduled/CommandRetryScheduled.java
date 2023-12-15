package com.panvan.app.scheduled;

import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.SdkUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandRetryScheduled {
    private static final String TAG = CommandRetryScheduled.class.getSimpleName();

    private static final ConcurrentMap<String, Long> COMMAND_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Integer> RETRY_TIMES_MAP = new ConcurrentHashMap<>();

    private static final long RETRY_TIME = 500;

    private static CommandRetryScheduled INSTANCE = null;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static CommandRetryScheduled getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new CommandRetryScheduled();
        }
        return INSTANCE;
    }

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Long> entry : COMMAND_MAP.entrySet()) {
                    long diffTime = System.currentTimeMillis() - entry.getValue();
                    // 查看剩余次数
                    Integer times = RETRY_TIMES_MAP.get(entry.getKey());

                    if (diffTime > RETRY_TIME) {
                        if (null != times && times > 0) {
                            times--;
                            RETRY_TIMES_MAP.put(entry.getKey(), times);
                            SdkUtil.retryWriteCommand(entry.getKey());
                            try {
                                TimeUnit.MILLISECONDS.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            LogUtil.info(TAG, "进行重试：" + entry.getKey());
                        } else {
                            // 重试次数不足
                            RETRY_TIMES_MAP.remove(entry.getKey());
                            COMMAND_MAP.remove(entry.getKey());
                            LogUtil.info(TAG, "重试次数不足：" + entry.getKey());
                        }
                    }
                }
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }


    public void add(String command) {
        COMMAND_MAP.put(command, System.currentTimeMillis());
        RETRY_TIMES_MAP.put(command, 2);
    }

    public void remove(String command) {
        command = "68" + command;
        LogUtil.info("删除" + command);
        for (Map.Entry<String, Long> entry : COMMAND_MAP.entrySet()) {
            if (entry.getKey().startsWith(command)) {
                COMMAND_MAP.remove(entry.getKey());
                RETRY_TIMES_MAP.remove(entry.getKey());
            }
        }
    }

    public void remove(byte b) {
        String str = ProtocolUtil.byteToHexStr(b);
        remove(str);
    }
}
