package ru.blackart.dsi.infopanel.utils.searching;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchingForStatus implements Searching {
    ManagerMainDeviceFilter managerMainDeviceFilter = ManagerMainDeviceFilter.getInstance();
    private HashMap<Integer, Hoststatus> hostStatuses = new HashMap<Integer, Hoststatus>();
    private String all_status = "";
    private final DataModel dataModel = DataModel.getInstance();

    public SearchingForStatus(ArrayList<Hoststatus> statuses) {
        for (int i = 0; i < statuses.size(); i++) {
            Hoststatus hoststatus = statuses.get(i);
            hostStatuses.put(hoststatus.getId(), hoststatus);
        }
    }

    public SearchingForStatus(String[] statuses) {
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        for (int i = 0; i < statuses.length; i++) {
            Criteria crt_status = session.createCriteria(Hoststatus.class);
            crt_status.add(Restrictions.eq("id", Integer.valueOf(statuses[i])));
            if (crt_status.list().size() > 0) {
                Hoststatus hoststatus = (Hoststatus) crt_status.list().get(0);
                hostStatuses.put(hoststatus.getId(), hoststatus);
                all_status += hoststatus.getName() + ", ";
            }
        }

        session.flush();
        session.close();
    }

    public HashMap<Integer, Hoststatus> getHoststatuses() {
        return hostStatuses;
    }

    public String getAll_status() {
        return all_status;
    }

    public List<Devcapsule> find() {
        synchronized (dataModel) {

            if (hostStatuses.size() > 0) {
                List<Trouble> troubles = new ArrayList<Trouble>();
                troubles.addAll(dataModel.getTroubleListForName("current").getTroubles());
                troubles.addAll(dataModel.getTroubleListForName("complete").getTroubles());
                troubles.addAll(dataModel.getTroubleListForName("waiting_close").getTroubles());

                List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

                for (Trouble t : troubles) {
                    for (Devcapsule d : t.getDevcapsules()) {
                        if ((d.getDevice().getHoststatus() != null)
                                && hostStatuses.containsKey(d.getDevice().getHoststatus().getId())
                                && (this.managerMainDeviceFilter.filterInputDevice(d.getDevice())))
                        {
                            devc_find.add(d);
                        }
                    }
                }

                return devc_find;
            } else {
                return null;
            }

        }
    }

    public List<Devcapsule> findInData(List<Devcapsule> devcapsules) {
        return null;
    }
}
