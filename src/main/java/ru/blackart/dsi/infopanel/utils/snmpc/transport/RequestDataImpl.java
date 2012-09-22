package ru.blackart.dsi.infopanel.utils.snmpc.transport;

public interface RequestDataImpl {
    public String getPoolling();
    public void setPoolling(String poolling);

    public String getDevice();
    public void setDevice(String device);

    public String getDate();
    public void setDate(String date);

    public String getTime();
    public void setTime(String time);

    public String getGroup();
    public void setGroup(String group);

    public String getDesc();
    public void setDesc(String desc);
}
