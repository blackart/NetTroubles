package ru.blackart.dsi.infopanel.utils.searching;

import ru.blackart.dsi.infopanel.beans.Region;

import java.util.ArrayList;

public class SearchParam {
    private int startSearchDate;
    private int endSearchDate;
    private String deviceName;
    private ArrayList<Integer> hostStatuses;
    private ArrayList<Region> regions;

    public int getStartSearchDate() {
        return startSearchDate;
    }

    public void setStartSearchDate(int startSearchDate) {
        this.startSearchDate = startSearchDate;
    }

    public int getEndSearchDate() {
        return endSearchDate;
    }

    public void setEndSearchDate(int endSearchDate) {
        this.endSearchDate = endSearchDate;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public ArrayList<Integer> getHostStatuses() {
        return hostStatuses;
    }

    public void setHostStatuses(ArrayList<Integer> hostStatuses) {
        this.hostStatuses = hostStatuses;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }
}
