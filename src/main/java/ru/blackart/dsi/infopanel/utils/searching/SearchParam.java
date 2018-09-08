package ru.blackart.dsi.infopanel.utils.searching;

import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;

import java.util.ArrayList;

public class SearchParam {
    private Long startSearchDate;
    private Long endSearchDate;
    private String deviceName;
    private ArrayList<Hoststatus> hostStatuses;
    private ArrayList<Region> regions;

    public Long getStartSearchDate() {
        return startSearchDate;
    }

    public void setStartSearchDate(Long startSearchDate) {
        this.startSearchDate = startSearchDate;
    }

    public Long getEndSearchDate() {
        return endSearchDate;
    }

    public void setEndSearchDate(Long endSearchDate) {
        this.endSearchDate = endSearchDate;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public ArrayList<Hoststatus> getHostStatuses() {
        return hostStatuses;
    }

    public void setHostStatuses(ArrayList<Hoststatus> hostStatuses) {
        this.hostStatuses = hostStatuses;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }
}
