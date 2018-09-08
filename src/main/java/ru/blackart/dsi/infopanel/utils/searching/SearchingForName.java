package ru.blackart.dsi.infopanel.utils.searching;

import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.DeviceManager;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;

import java.util.ArrayList;
import java.util.List;

public class SearchingForName implements Searching {
    DeviceManager deviceManager = DeviceManager.getInstance();
    ManagerMainDeviceFilter managerMainDeviceFilter = ManagerMainDeviceFilter.getInstance();
    Device device;

    public Device getDevice() {
        return device;
    }

    public SearchingForName(String name) {
        this.device = this.deviceManager.getDevice(name);
    }

    public List<Devcapsule> search(List<Devcapsule> devcapsules) {
        List<Devcapsule> devc_find = null;
        if (devcapsules == null) {
            devc_find = this.searchEverywhere();
        } else {
            devc_find = new ArrayList<Devcapsule>();

            for (Devcapsule d : devcapsules) {
                if ((d.getDevice().getName().equals(this.device.getName())) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                    devc_find.add(d);
                }
            }
        }
        return devc_find;
    }

    public List<Devcapsule> searchEverywhere() {
        DataModel dataModel = DataModel.getInstance();

        List<Trouble> troubles = new ArrayList<Trouble>();
        troubles.addAll(dataModel.getTroubleListForName("current").getTroubles());
        troubles.addAll(dataModel.getTroubleListForName("complete").getTroubles());
        troubles.addAll(dataModel.getTroubleListForName("waiting_close").getTroubles());

        List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

        for (Trouble t : troubles) {
            for (Devcapsule d : t.getDevcapsules()) {
                if ((d.getDevice().getName().equals(this.device.getName())) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                    devc_find.add(d);
                }
            }
        }

        return devc_find;
    }

    public List<Devcapsule> find() {
        if (device != null) {
            DataModel dataModel = DataModel.getInstance();

            List<Trouble> troubles = new ArrayList<Trouble>();
            troubles.addAll(dataModel.getTroubleListForName("current").getTroubles());
            troubles.addAll(dataModel.getTroubleListForName("complete").getTroubles());
            troubles.addAll(dataModel.getTroubleListForName("waiting_close").getTroubles());

            List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

            for (Trouble t : troubles) {
                for (Devcapsule d : t.getDevcapsules()) {
                    if ((d.getDevice().getName().equals(this.device.getName())) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                        devc_find.add(d);
                    }
                }
            }

            return devc_find;
        } else {
            return null;
        }
    }

    public List<Devcapsule> findInData(List<Devcapsule> devcapsules) {
        if (device != null) {
            List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

            for (Devcapsule d : devcapsules) {
                if ((d.getDevice().getName().equals(this.device.getName())) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
                    devc_find.add(d);
                }
            }

            return devc_find;
        } else {
            return null;
        }
    }
}
