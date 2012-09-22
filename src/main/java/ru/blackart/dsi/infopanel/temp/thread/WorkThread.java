package ru.blackart.dsi.infopanel.temp.thread;

import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataImpl;

public class WorkThread implements Runnable {
    private RequestDataImpl requestData;

    public WorkThread(RequestDataImpl requestData) {    //Конструктор потока
        this.requestData = requestData;                 //передаём потоку данные запроса
    }

    public void run() {
        ManagerMainDeviceFilter managerDeviceFilter = ManagerMainDeviceFilter.getInstance();
        Device inputDevice = new Device();

        Hostgroup inputDeviceHostgroup = new Hostgroup();
        inputDeviceHostgroup.setNum(Integer.valueOf(this.requestData.getGroup()));

        inputDevice.setName(this.requestData.getDevice());
        inputDevice.setHostgroup(inputDeviceHostgroup);


        /*if (managerDeviceFilter.filterInputDevice(inputDevice)) {
            try {
                if (this.requestData.getPoolling().equals("down")) {
                    LogEngine.getInstance().deviceDown(this.requestData.getPoolling(), this.requestData.getDevice(), this.requestData.getDate(), this.requestData.getTime(), this.requestData.getGroup(), this.requestData.getDesc());
                } else if (this.requestData.getPoolling().equals("up")) {
                    LogEngine.getInstance().deviceUp(this.requestData.getPoolling(), this.requestData.getDevice(), this.requestData.getDate(), this.requestData.getTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/
    }
}
