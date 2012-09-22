package ru.blackart.dsi.infopanel.utils.snmpc.transport;

public class RequestDataObject implements RequestDataImpl {
    private String poolling;
    private String device;
    private String date;
    private String time;
    private String group;
    private String desc;

    public String getPoolling() {
        return this.poolling;
    }

    public void setPoolling(String poolling) {
        this.poolling = poolling;
    }

    public String getDevice() {
        return this.device;
    }

    public void setDevice(String device) {
        this.device = device;
    }    

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public RequestDataObject(String poolling, String device, String date, String time, String group, String desc) {
        this.setPoolling(poolling);
        this.setDevice(device);
        this.setDate(date);
        this.setTime(time);
        this.setGroup(group);
        this.setDesc(desc);
    }
}
