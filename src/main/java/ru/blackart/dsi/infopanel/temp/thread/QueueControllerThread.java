package ru.blackart.dsi.infopanel.temp.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;

import java.util.concurrent.ConcurrentLinkedQueue;


public class QueueControllerThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            ManagerQueueRequests managerQueueRequests = ManagerQueueRequests.getInstance();
            ConcurrentLinkedQueue<RequestDataImpl> requestQueue = managerQueueRequests.getRequestQueue();

            synchronized (requestQueue) {
                while (!requestQueue.isEmpty()) {
//                    System.out.println("-----------#########---------" + requestQueue.size());
                    RequestDataImpl requestDataObjectDown = requestQueue.poll();

                    PoolThreads poolThreads = PoolThreads.getInstance();

                    /*if (requestDataObjectDown.getPoolling().equals("down")) {
                        Thread exDownThread = new Thread(new DownTrapsHandler(requestDataObjectDown));
                        exDownThread.setPriority(Thread.MAX_PRIORITY);
                        poolThreads.execute(exDownThread);
                    } else if (requestDataObjectDown.getPoolling().equals("up")) {
                        Thread exDownThread = new Thread(new UpTrapsHandler(requestDataObjectDown));
                        exDownThread.setPriority(Thread.MAX_PRIORITY);
                        poolThreads.execute(exDownThread);
                    }*/
                }
            }

//            System.out.println("I'm main thread, my name is ------------------ " + Thread.currentThread().getName());

            try {
                Thread.currentThread().sleep(5000);                  //3600000 = 1 час
            } catch (InterruptedException e) {
                Thread.currentThread().interrupted();
                log.info(Thread.currentThread().getName() + " continue run!");
            }
        }
    }
}
