package ru.blackart.dsi.infopanel.snmpc.services;

import ru.blackart.dsi.infopanel.snmpc.storage.Storage;

public class ManagerUpDevcListCleaningThread {
    private static ManagerUpDevcListCleaningThread managerUpDevcListCleaningThread;
    private Storage storage;
    private Thread upDevcListCleaningThread;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public static ManagerUpDevcListCleaningThread getInstance() {
        if (managerUpDevcListCleaningThread == null) {
            managerUpDevcListCleaningThread = new ManagerUpDevcListCleaningThread();
        }

        return managerUpDevcListCleaningThread;
    }

    public void startCleaningThread() {
        if ((this.upDevcListCleaningThread == null) || (!this.upDevcListCleaningThread.isAlive())) {
            this.upDevcListCleaningThread = new Thread(new UpDevcListCleaningThread());
            this.upDevcListCleaningThread.setDaemon(true);
            this.upDevcListCleaningThread.start();
        }
    }
}
