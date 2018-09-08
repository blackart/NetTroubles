package ru.blackart.dsi.infopanel.utils.searching;

import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Region;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SearchingForRegion implements Searching {
    private Properties regions = new Properties();
    ManagerMainDeviceFilter managerMainDeviceFilter = ManagerMainDeviceFilter.getInstance();
    private String regions_str = "";
    private final DataModel dataModel = DataModel.getInstance();

    public SearchingForRegion(List<Region> regions) {
        for (Region region : regions) {
            this.regions.put(region.getId(), region);
        }
    }

    public List<Devcapsule> search(List<Devcapsule> devcapsules) {
        List<Devcapsule> devc_find = null;
        if (devcapsules == null) {
            devc_find = this.searchEverywhere();
        } else {
            devc_find = new ArrayList<Devcapsule>();

            for (Devcapsule d : devcapsules) {
                if (regions.containsKey(d.getDevice().getRegion().getId()) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                    devc_find.add(d);
                }
            }
        }

        return devc_find;
    }

    public List<Devcapsule> searchEverywhere() {
        List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

        synchronized (dataModel) {
            List<Trouble> troubles = new ArrayList<Trouble>();
            troubles.addAll(dataModel.getTroubleListForName("current").getTroubles());
            troubles.addAll(dataModel.getTroubleListForName("complete").getTroubles());
            troubles.addAll(dataModel.getTroubleListForName("waiting_close").getTroubles());

            for (Trouble t : troubles) {
                for (Devcapsule d : t.getDevcapsules()) {
                    if (regions.containsKey(d.getDevice().getRegion().getId()) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                        devc_find.add(d);
                    }
                }
            }
        }
        return devc_find;
    }

    public SearchingForRegion(String[] queryRegions, List<Region> allRegions) {
        for (Region region : allRegions) {
            for (int i = 0; i < queryRegions.length; i++) {
                if (Integer.valueOf(queryRegions[i]) == region.getId()) {
                    regions.put(region.getId(), region);
                    regions_str += region.getName() + " ; ";
                }
            }
        }
    }

    public String getRegions_str() {
        return regions_str;
    }

    public List<Devcapsule> find() {
        DataModel dataModel = DataModel.getInstance();

        List<Trouble> troubles = new ArrayList<Trouble>();
        troubles.addAll(dataModel.getTroubleListForName("current").getTroubles());
        troubles.addAll(dataModel.getTroubleListForName("complete").getTroubles());
        troubles.addAll(dataModel.getTroubleListForName("waiting_close").getTroubles());

        List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

        for (Trouble t : troubles) {
            for (Devcapsule d : t.getDevcapsules()) {
                if (regions.containsKey(d.getDevice().getRegion().getId()) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                    devc_find.add(d);
                }
            }
        }
        return devc_find;
    }

    public List<Devcapsule> findInData(List<Devcapsule> devcapsules) {
        List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

        for (Devcapsule d : devcapsules) {
            if (regions.containsKey(d.getDevice().getRegion().getId()) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                devc_find.add(d);
            }
        }

        return devc_find;
    }
}
