// package com.panvan.app.scheduled.task;
//
// import com.panvan.app.service.CommunicationService;
// import com.panvan.app.utils.LogUtil;
// import com.panvan.app.utils.SdkUtil;
//
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.Queue;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.ConcurrentMap;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicBoolean;
//
// public class CommandConsumerQueueBak {
//     private final Object lock = new Object();
//     private final Queue<CommandInfo> queue = new LinkedList<>();
//     private final ConcurrentMap<String, Boolean> COMMAND_MAP = new ConcurrentHashMap<>();
//
//     private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
//     private AtomicBoolean monitorScheduledExecutorStatus = new AtomicBoolean(false);
//     private static ExecutorService executorService = Executors.newFixedThreadPool(2);
//
//
//     private long lastStart = System.currentTimeMillis();
//
//     public static class CommandInfo {
//         private String command;
//         private boolean retry;
//
//         public CommandInfo(String command, boolean retry) {
//             this.command = command;
//             this.retry = retry;
//         }
//
//         public String getCommand() {
//             return command;
//         }
//
//         public void setCommand(String command) {
//             this.command = command;
//         }
//
//         public boolean isRetry() {
//             return retry;
//         }
//
//         public void setRetry(boolean retry) {
//             this.retry = retry;
//         }
//     }
//
//     public void produce() throws InterruptedException {
//         while (true) {
//             synchronized (lock) {
//                 int capacity = 10;
//                 if (!COMMAND_MAP.isEmpty()) {
//                     for (Map.Entry<String, Boolean> entry : COMMAND_MAP.entrySet()) {
//                         queue.add(new CommandInfo(entry.getKey(), entry.getValue()));
//                     }
//                     COMMAND_MAP.clear();
//                 }
//
//                 while (queue.size() >= capacity) {
//                     // 更新最后开始时间
//                     lastStart = System.currentTimeMillis();
//                     LogUtil.info("CommandConsumerQueue", "队列已满，生产者进入等待状态...");
//                     lock.wait();
//                 }
//
//                 // 添加超时机制
//                 while (System.currentTimeMillis() - lastStart > 5000 && !queue.isEmpty()) {
//                     // 更新最后开始时间
//                     lastStart = System.currentTimeMillis();
//                     LogUtil.info("CommandConsumerQueue", "队列等待超过5s，生产者进入等待状态...");
//                     lock.wait(100);
//                 }
//
//                 LogUtil.info("CommandConsumerQueue", "生产者生产:" + queue.size());
//                 lock.notifyAll();
//                 Thread.sleep(2000);
//             }
//         }
//     }
//
//     public void consume() throws InterruptedException {
//         while (true) {
//             synchronized (lock) {
//                 while (queue.isEmpty()) {
//                     LogUtil.info("CommandConsumerQueue", "队列为空，消费者进入等待状态...");
//                     lock.wait();
//                 }
//
//                 CommandInfo poll = queue.poll();
//                 if (null == poll) {
//                     continue;
//                 }
//
//                 LogUtil.info("CommandConsumerQueue", "消费者消费: " + poll.getCommand());
//                 if (poll.isRetry()) {
//                     SdkUtil.writeCommand(poll.getCommand());
//                 } else {
//                     SdkUtil.retryWriteCommand(poll.getCommand());
//                 }
//
//                 lock.notifyAll();
//                 Thread.sleep(100);
//             }
//         }
//     }
//
//     public void start(CommandConsumerQueueBak example) {
//         if (Objects.nonNull(executorService)) {
//             if (!executorService.isShutdown()) {
//                 executorService.shutdownNow();
//             }
//             executorService = null;
//         }
//
//         executorService = Executors.newFixedThreadPool(2);
//
//         executorService.execute(() -> {
//             try {
//                 example.produce();
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         });
//
//         executorService.execute(() -> {
//             try {
//                 Thread.sleep(50);
//                 example.consume();
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         });
//
//         if (!monitorScheduledExecutorStatus.get()) {
//             monitorScheduledExecutorStatus.set(true);
//             scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
//                 @Override
//                 public void run() {
//                     LogUtil.info("CommandConsumerQueue", "\r\n当前COMMAND_MAP长度：" + COMMAND_MAP.size() + "\r\n" + "队列queue长度：" + queue.size());
//                     int limit = 200;
//                     if (COMMAND_MAP.size() >= limit || queue.size() >= limit) {
//                         start(CommunicationService.getInstance().getCommandConsumerQueue());
//                     }
//                 }
//             }, 10000, 10000, TimeUnit.MILLISECONDS);
//         }
//     }
//
//     public void addList(List<String> hexList) {
//         for (String hex : hexList) {
//             COMMAND_MAP.put(hex, true);
//         }
//     }
//
//     public void add(String hex, boolean retry) {
//         COMMAND_MAP.put(hex, retry);
//     }
// }
