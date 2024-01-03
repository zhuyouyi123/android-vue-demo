package com.panvan.app.scheduled.task;

import com.panvan.app.service.CommunicationService;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.SdkUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandConsumerQueue {

    private static final long EXECUTOR_TIME = 200;
    private final BlockingQueue<CommandInfo> queue = new LinkedBlockingQueue<>();
    private final ConcurrentMap<String, Boolean> COMMAND_MAP = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean monitorScheduledExecutorStatus = new AtomicBoolean(false);
    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static class CommandInfo {
        private String command;
        private boolean retry;

        public CommandInfo(String command, boolean retry) {
            this.command = command;
            this.retry = retry;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public boolean isRetry() {
            return retry;
        }

        public void setRetry(boolean retry) {
            this.retry = retry;
        }
    }

    public void produce() throws InterruptedException {
        while (true) {
            int capacity = 10;
            if (!COMMAND_MAP.isEmpty()) {
                for (Map.Entry<String, Boolean> entry : COMMAND_MAP.entrySet()) {
                    boolean offerResult = queue.offer(new CommandInfo(entry.getKey(), entry.getValue()));
                    // 检查是否成功将元素添加到队列中
                    if (!offerResult) {
                        LogUtil.error("CommandConsumerQueue无法将元素添加到队列中");
                        // 可以根据需要处理添加失败的情况
                    }
                }
                COMMAND_MAP.clear();
            }

            while (queue.size() >= capacity) {
                LogUtil.info("CommandConsumerQueue", "队列已满，生产者进入等待状态...");
                Thread.sleep(2000);
            }

            // 添加超时机制
            long lastStart = System.currentTimeMillis();
            while (System.currentTimeMillis() - lastStart < 5000 && !queue.isEmpty()) {
                LogUtil.info("CommandConsumerQueue", "队列等待超过5s，生产者进入等待状态...");
                Thread.sleep(500);
            }

            LogUtil.info("CommandConsumerQueue", "生产者生产:" + queue.size());
            Thread.sleep(2000);
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            if (queue.isEmpty()) {
                LogUtil.info("CommandConsumerQueue", "队列为空，消费者进入等待状态...");
                Thread.sleep(2000);
            } else {
                CommandInfo poll = queue.poll();
                if (poll != null) {
                    LogUtil.info("CommandConsumerQueue", "消费者消费: " + poll.getCommand());
                    if (poll.isRetry()) {
                        SdkUtil.writeCommand(poll.getCommand());
                    } else {
                        SdkUtil.retryWriteCommand(poll.getCommand());
                    }
                    Thread.sleep(EXECUTOR_TIME);
                }
            }
        }
    }

    public void start(CommandConsumerQueue example) {
        if (Objects.nonNull(executorService)) {
            executorService.shutdownNow();
        }
        executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> {
            try {
                example.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            try {
                Thread.sleep(50);
                example.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        if (!monitorScheduledExecutorStatus.get()) {
            monitorScheduledExecutorStatus.set(true);
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                LogUtil.info("CommandConsumerQueue", "当前COMMAND_MAP长度：" + COMMAND_MAP.size() + "队列queue长度：" + queue.size());
                int limit = 200;
                if (COMMAND_MAP.size() >= limit || queue.size() >= limit) {
                    start(CommunicationService.getInstance().getCommandConsumerQueue());
                }
            }, 10000, 10000, TimeUnit.MILLISECONDS);
        }
    }

    public void addList(List<String> hexList) {
        for (String hex : hexList) {
            COMMAND_MAP.put(hex, true);
        }
    }

    public void add(String hex, boolean retry) {
        COMMAND_MAP.put(hex, retry);
    }
}
