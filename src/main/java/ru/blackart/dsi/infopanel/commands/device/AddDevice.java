package ru.blackart.dsi.infopanel.commands.device;

import com.myjavatools.xml.BasicXmlData;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;

import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

public class AddDevice extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public String execute() throws Exception {
        log.info("Adding device ...");

        String name = this.getRequest().getParameter("name");
        String desc = this.getRequest().getParameter("desc");
        String status_id = this.getRequest().getParameter("status").replace("_hs_add", "");
        String group_id = this.getRequest().getParameter("group").replace("_hg_add","");
        String region_id = this.getRequest().getParameter("region");
        log.info("Input parameters : name - " + name + "; description - " + desc  + "; status_id - " + status_id + "; group_id - " + group_id + "; region_id - " + region_id);

        Hostgroup hostgroup = null;
        Hoststatus hoststatus = null;
        Region region = null;

        boolean ok = true;

        Session session = SessionFactorySingle.getSessionFactory().openSession();
        BasicXmlData xml = new BasicXmlData("device_message");

        if (name.equals("")) {
            ok = ok && false;
            log.error("Host name is empty!");
            xml.addKid(new BasicXmlData("message", "Устройство с пустым именем не может быть добавлено!"));
        } else {
            Criteria crt_device = session.createCriteria(Device.class);
            crt_device.add(Restrictions.eq("name", name));
            List<Device> device = (List<Device>)crt_device.list();

            if (device.size() > 0) {
                ok = ok && false;
                log.error("Host with request name - " + name + " already exist");
                xml.addKid(new BasicXmlData("message", "Устройство не может быть бобавлено, так как в системе уже существует устройство с таким именем!"));
            }
        }

        if (status_id.equals("") || group_id.equals("") || region_id.equals("")) {
            ok = ok && false;
            if (status_id.equals("")) log.error("Host status is empty!");
            if (group_id.equals("")) log.error("Host group is empty!");
            if (region_id.equals("")) log.error("Region host is empty!");

            xml.addKid(new BasicXmlData("message", "Устройство не может быть добавлено, если значение его статуса, группы или региона пусто!"));
        } else {
            Criteria crt_hoststatus = session.createCriteria(Hoststatus.class);
            crt_hoststatus.add(Restrictions.eq("id", Integer.valueOf(status_id)));
            List<Hoststatus> hoststatus_list = (List<Hoststatus>)crt_hoststatus.list();

            Criteria crt_hostgroup = session.createCriteria(Hostgroup.class);
            crt_hostgroup.add(Restrictions.eq("id", Integer.valueOf(group_id)));
            List<Hostgroup> hostgroup_list = (List<Hostgroup>)crt_hostgroup.list();

            Criteria crt_region = session.createCriteria(Region.class);
            crt_region.add(Restrictions.eq("id", Integer.valueOf(region_id)));
            List<Region> region_list = (List<Region>)crt_region.list();

            if ((hostgroup_list.size() == 1) && (hoststatus_list.size() == 1) && (region_list.size() == 1)) {
                log.info("Host group - " + hostgroup_list.get(0).getName() + ", host status - " + hoststatus_list.get(0).getName() + ", region - " + region_list.get(0).getName());
                hostgroup = hostgroup_list.get(0);
                hoststatus = hoststatus_list.get(0);
                region = region_list.get(0);
            } else {
                ok = ok && false;
                if (hostgroup_list.size() == 0) log.error("Not found host group with " + group_id + " id");
                if (hoststatus_list.size() == 0) log.error("Not found host status with " + status_id + " id");
                if (region_list.size() == 0) log.error("Not found region with " + region_id + " id");
                xml.addKid(new BasicXmlData("message", "Устройство не может быть добавлено, если статус, группа или регион с такими значениями не найдены в системе!"));
            }
        }

        if (ok) {
            Device device = new Device();

            device.setName(name);
            device.setDescription(desc);
            device.setHoststatus(hoststatus);
            device.setHostgroup(hostgroup);
            device.setRegion(region);

            session.beginTransaction();
            session.save(device);
            session.getTransaction().commit();
            log.info("Save device - " + device.getName());

            DeviceManager deviceManager = DeviceManager.getInstance();
            Properties dev_list = deviceManager.getDevice_list();
            dev_list.put(device.getName(), device);

            log.info("Update info about all device");
            xml.addKid(new BasicXmlData("message", "Устройство успешно добавлено!"));
        }

        session.flush();
        session.close();

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);

        return null;
    }
}
