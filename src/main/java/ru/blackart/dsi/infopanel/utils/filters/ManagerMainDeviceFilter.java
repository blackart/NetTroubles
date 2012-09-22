package ru.blackart.dsi.infopanel.utils.filters;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.DeviceFilter;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ManagerMainDeviceFilter {
    private static ManagerMainDeviceFilter managerDeviceFilter;
    private SessionFactory sessionFactory;
    private List<DeviceFilter> inputDeviceFilters;

    public static ManagerMainDeviceFilter getInstance() {
        if (managerDeviceFilter == null) {
            managerDeviceFilter = new ManagerMainDeviceFilter();
            managerDeviceFilter.sessionFactory = SessionFactorySingle.getSessionFactory();

            managerDeviceFilter.inputDeviceFilters = new ArrayList<DeviceFilter>();

            Session session = managerDeviceFilter.sessionFactory.openSession();

            Criteria crt_trouble = session.createCriteria(TroubleList.class);
            crt_trouble.add(Restrictions.eq("name", "main"));
            TroubleList troubleList  = (TroubleList) crt_trouble.list().get(0);
            managerDeviceFilter.inputDeviceFilters.addAll(troubleList.getFilters());

            session.flush();
            session.close();

            TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
            troubleListsManager.setMainFilterList(managerDeviceFilter.inputDeviceFilters);
        }

        return managerDeviceFilter;
    }

    public boolean validInputDeviceName(String name) {
        boolean result = false;
        int count_filters = 0;
        for (DeviceFilter df : this.inputDeviceFilters) {
            if ((df.isEnable()) && (df.getType().getName().equals("name"))) {
                if (df.isPolicy()) {
                    result = result || Pattern.matches(df.getValue(), name);
                    count_filters++;
                }
            }
        }

        if (count_filters == 0) result = true;

        for (DeviceFilter df : this.inputDeviceFilters) {
            if ((df.isEnable()) && (df.getType().getName().equals("name"))) {
                if (!df.isPolicy()) {
                    result = result && !Pattern.matches(df.getValue(), name);
                }
            }
        }

        return result;
    }   

    public boolean validInputDeviceGroup(int group) {
        boolean result = false;
        int count_filters = 0;
        for (DeviceFilter df : this.inputDeviceFilters) {
            if ((df.isEnable()) && (df.getType().getName().equals("group"))) {
                if (df.isPolicy()) {
                    result = result || (Integer.valueOf(df.getValue()) == group);
                    count_filters++;
                }
            }
        }

        if (count_filters == 0) result = true;

        for (DeviceFilter df : this.inputDeviceFilters) {
            if ((df.isEnable()) && (df.getType().getName().equals("group"))) {
                if (!df.isPolicy()) {
                    result = result && (Integer.valueOf(df.getValue()) != group);
                }
            }
        }

        return result;
    }

    public boolean filterInputDevice(Device device) {
        return this.validInputDeviceName(device.getName()) && this.validInputDeviceGroup(device.getHostgroup().getNum());
    }

    public void addNewDeviceInputFilter(DeviceFilter deviceFilter) {
        int index = -1;
        for (DeviceFilter df : this.inputDeviceFilters) {
            if (df.getName().equals(deviceFilter.getName())) {
                index = this.inputDeviceFilters.indexOf(df);
            }
        }
        if (index != -1) {
            inputDeviceFilters.remove(index);
        }
        inputDeviceFilters.add(deviceFilter);
    }

    public void editDeviceInputFilter(DeviceFilter deviceFilter) {
        int index = -1;
        for (DeviceFilter df : this.inputDeviceFilters) {
            if (df.getId() == deviceFilter.getId()) {
                index = this.inputDeviceFilters.indexOf(df);
            }
        }
        if (index != -1) {
            inputDeviceFilters.remove(index);
            inputDeviceFilters.add(deviceFilter);
        }        
    }

    public void deleteDeviceInputFilter(DeviceFilter deviceFilter) {
        int index = -1;
        for (DeviceFilter df : this.inputDeviceFilters) {
            if (df.getId() == deviceFilter.getId()) {
                index = this.inputDeviceFilters.indexOf(df);
            }
        }
        if (index >= 0) {
            this.inputDeviceFilters.remove(index);
        }
    }
    
}
