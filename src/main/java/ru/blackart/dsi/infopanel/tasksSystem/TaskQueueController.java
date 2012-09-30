package ru.blackart.dsi.infopanel.tasksSystem;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskQueueController {
    private static TaskQueueController taskQueueController;
    private BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 150, 10000, TimeUnit.MILLISECONDS, blockingQueue);

    public static TaskQueueController getInstance() {
        if (taskQueueController == null) {
            taskQueueController = new TaskQueueController();
        }

        return taskQueueController;
    }


    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        return this.threadPoolExecutor;
    }
}
