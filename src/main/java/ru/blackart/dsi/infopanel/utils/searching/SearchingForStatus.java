package ru.blackart.dsi.infopanel.utils.searching;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class SearchingForStatus implements Searching {
    ManagerMainDeviceFilter managerMainDeviceFilter = ManagerMainDeviceFilter.getInstance();
    private Properties hoststatuses = new Properties();
    private String all_status = "";

    public SearchingForStatus(String[] statuses) {
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        for (int i = 0; i < statuses.length; i++) {
            Criteria crt_status = session.createCriteria(Hoststatus.class);
            crt_status.add(Restrictions.eq("id", Integer.valueOf(statuses[i])));
            if (crt_status.list().size() > 0) {
                Hoststatus hoststatus = (Hoststatus) crt_status.list().get(0);
                hoststatuses.put(hoststatus.getName(), hoststatus);
            }
        }

        session.flush();
        session.close();

        int i = 0;
        for (Enumeration en = hoststatuses.keys(); en.hasMoreElements();) {
            i++;
            String status_name = en.nextElement().toString();
            if (i != hoststatuses.size()) {
                all_status += status_name + ", ";
            } else {
                all_status += status_name;
            }
        }
    }

    public Properties getHoststatuses() {
        return hoststatuses;
    }

    public String getAll_status() {
        return all_status;
    }

    public List<Devcapsule> find() {
        DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();

        if (hoststatuses.size() > 0) {
            List<Trouble> troubles = new ArrayList<Trouble>();
            troubles.addAll(dataModelConstructor.getTroubleListForName("current").getTroubles());
            troubles.addAll(dataModelConstructor.getTroubleListForName("complete").getTroubles());
            troubles.addAll(dataModelConstructor.getTroubleListForName("waiting_close").getTroubles());

            List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

            for (Trouble t : troubles) {
                for (Devcapsule d : t.getDevcapsules()) {
                    if ((d.getDevice().getHoststatus() != null) && hoststatuses.containsKey(d.getDevice().getHoststatus().getName()) && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))) {
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
        return null;
    }
}
