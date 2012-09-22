package ru.blackart.dsi.infopanel.temp.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataObject;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagerQueueRequests {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static ManagerQueueRequests managerQueueRequests;
    private ConcurrentLinkedQueue<RequestDataImpl> requestQueue;
    private Thread queueControllerThread;
    private final ThreadGroup group = new ThreadGroup("manager_thread");

    public static ManagerQueueRequests getInstance() {
        if (managerQueueRequests == null) {
            managerQueueRequests = new ManagerQueueRequests();
            managerQueueRequests.requestQueue= new ConcurrentLinkedQueue<RequestDataImpl>();
        }

        return managerQueueRequests;
    }

    public synchronized void addDataToQueue(String poolling, String device, String date, String time, String group, String desc) {
        if (desc == null) desc = "";
        requestQueue.add(new RequestDataObject(poolling, device, date, time, group, desc));

    }

    public ConcurrentLinkedQueue<RequestDataImpl> getRequestQueue() {
        return requestQueue;
    }

    public void start() {
        if ((this.queueControllerThread == null) || !this.queueControllerThread.isAlive()) {
            System.out.println("Null: " + (this.queueControllerThread == null?"true":"false " + this.queueControllerThread.getName() + " alive: " + (this.queueControllerThread.isAlive()?"true":"false") + " interrupted: " + (this.queueControllerThread.isInterrupted()?"true":"false")));
            this.queueControllerThread = new Thread(group, new QueueControllerThread());
            this.queueControllerThread.setDaemon(true);
            this.queueControllerThread.start();
            log.info("Thread " + this.queueControllerThread.getName() + " starting");
            log.info("Active count thread if group - " + String.valueOf(group.activeCount()));
        }
    }

    public void interrupt() {
        log.info("Try interrupt thread " + this.queueControllerThread.getName() + " ...");
        this.queueControllerThread.interrupt();
        log.info("Active count thread if group - " + String.valueOf(group.activeCount()));
    } 

    public void restart() {
        this.interrupt();
        this.start();
    }
}
