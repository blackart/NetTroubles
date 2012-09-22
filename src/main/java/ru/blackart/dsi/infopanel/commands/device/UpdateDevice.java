package ru.blackart.dsi.infopanel.commands.device;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;

import java.util.List;

public class UpdateDevice {
    private Device device;

    public UpdateDevice(Device dev) {
        this.device = dev;
    }

    public Device update(String device_name, String group, String desc) {
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        this.device.setName(device_name);
        this.device.setDescription(desc);

        Criteria crt_group = session.createCriteria(Hostgroup.class);
        crt_group.add(Restrictions.eq("num", Integer.valueOf(group)));
        List<Hostgroup> hostgroup_arr = crt_group.list();

        Hostgroup hostgroup;
        if (hostgroup_arr.size() != 0) {
            hostgroup = hostgroup_arr.get(0);
        } else {
            hostgroup = new Hostgroup();
            hostgroup.setNum(Integer.valueOf(group));
            hostgroup.setName(group);
            session.save(hostgroup);
        }

        this.device.setHostgroup(hostgroup);

        session.beginTransaction();
        session.update(this.device);
        session.getTransaction().commit();
        session.flush();
        session.close();

        return this.device;
    }
}
