package ru.blackart.dsi.infopanel.temp.thread;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

public class PoolThreads {
    private static PoolThreads poolThreads;
    private PooledExecutor pooledExecutor;


    public static PoolThreads getInstance() {
        if (poolThreads == null) {
            poolThreads = new PoolThreads();
            poolThreads.pooledExecutor = new PooledExecutor(new BoundedBuffer(100), 1000);
            poolThreads.pooledExecutor.setMinimumPoolSize(25);
            poolThreads.pooledExecutor.createThreads(50);
        }

        return poolThreads;
    }

    public void execute(Thread thread) {
        try {
            this.pooledExecutor.execute(thread);            
        } catch (InterruptedException e) {
            e.printStackTrace();  //interrupt operations
        }
    }

}
