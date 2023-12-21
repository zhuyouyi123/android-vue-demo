package com.seekcy.bracelet.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskProcessorUtil {
    // 创建异步任务处理器
    private static final AsyncTaskProcessor taskProcessor = new AsyncTaskProcessor();

    public static void submit(Runnable runnable) {
        // 创建并提交任务
        taskProcessor.executeTask(runnable);
    }


    private static class AsyncTaskProcessor {

        private final ExecutorService executor;

        public AsyncTaskProcessor() {
            // 创建一个固定大小的线程池
            executor = Executors.newFixedThreadPool(4); // 这里创建了一个包含4个线程的线程池，根据需要可以调整线程数量
        }

        public void executeTask(Runnable task) {
            executor.execute(task);
        }
    }
}
