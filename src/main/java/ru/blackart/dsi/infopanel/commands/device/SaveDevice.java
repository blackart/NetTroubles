package ru.blackart.dsi.infopanel.commands.device;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import java.util.List;

public class SaveDevice {
    public Device save(String device, String group, String desc) {
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        Device dev = new Device();

        Criteria criteria = session.createCriteria(Hostgroup.class);
        criteria.add(Restrictions.eq("num", Integer.valueOf(group)));
        List<Hostgroup> hostgroup_arr = criteria.list();

        /*if ((status != null) && (!status.equals(""))) {
            Criteria crt_status = session.createCriteria(Hoststatus.class);
            crt_status.add(Restrictions.eq("id", Integer.valueOf(status)));
            List<Hoststatus> hoststatus_arr = crt_status.list();

            Hoststatus hoststatus;

            if (hoststatus_arr.size() != 0) {
                hoststatus = hoststatus_arr.get(0);
                dev.setHoststatus(hoststatus);
            }
        }*/

        Hostgroup hostgroup;
        if (hostgroup_arr.size() != 0) {
            hostgroup = hostgroup_arr.get(0);
        } else {
            hostgroup = new Hostgroup();
            hostgroup.setNum(Integer.valueOf(group));
            hostgroup.setName(group);
            session.save(hostgroup);
        }

        dev.setName(device);
        dev.setDescription(desc);
        dev.setHostgroup(hostgroup);

        dev.setRegion(CheckRegionDevice.getRegionForDevice(dev,(List<Region>)TroubleListsManager.getInstance().getHTTPServletConfig().getServletContext().getAttribute("regions")));

        session.beginTransaction();
        session.save(dev);
        session.getTransaction().commit();
        session.flush();
        session.close();

        return dev;
    }
}
