package ru.blackart.dsi.infopanel.utils.snmpc.storage;

import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;

import java.io.Serializable;
import java.util.HashMap;

public class Storage implements Serializable {
    private HashMap<String, RequestDataImpl> upDevcList;
    private boolean learning;
    private int trueDownInterval;

    public Storage(boolean learning, int trueDownInterval) {
        this.upDevcList = new HashMap<String, RequestDataImpl>();
        this.learning = learning;
        this.trueDownInterval = trueDownInterval;
    }

    public HashMap<String, RequestDataImpl> getUpDevcList() {
        return upDevcList;
    }

    public void setUpDevcList(HashMap<String, RequestDataImpl> upDevcList) {
        this.upDevcList = upDevcList;
    }

    public boolean isLearning() {
        return learning;
    }

    public void setLearning(boolean learning) {
        this.learning = learning;
    }

    public int getTrueDownInterval() {
        return trueDownInterval;
    }

    public void setTrueDownInterval(int trueDownInterval) {
        this.trueDownInterval = trueDownInterval;
    }
}
