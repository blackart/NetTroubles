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

public class EditDevice extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public String execute() throws Exception {
        log.info("Editing device ...");

        String device_id = this.getRequest().getParameter("id");
        String name = this.getRequest().getParameter("name");
        String desc = this.getRequest().getParameter("desc");
        String status_id = this.getRequest().getParameter("status");
        String group_id = this.getRequest().getParameter("group");
        String region_id = this.getRequest().getParameter("region");

        log.info("Input parameters : name - " + name + "; description - " + desc  + "; status_id - " + status_id + "; group_id - " + group_id + "; region_id - " + region_id);

        Hostgroup hostgroup = null;
        Hoststatus hoststatus = null;
        Region region = null;
        List<Device> devices = null;

        boolean ok = true;
        Session session = SessionFactorySingle.getSessionFactory().openSession();
        BasicXmlData xml = new BasicXmlData("device_message");

        if (name.equals("")) {
            ok = ok && false;
            log.error("Host name is empty!");
            xml.addKid(new BasicXmlData("message", "Информация об устройстве не может быть изменена, так как имя устройства не может быть пустым!"));
        } else {
            Criteria crt_device = session.createCriteria(Device.class);
            crt_device.add(Restrictions.eq("id", Integer.valueOf(device_id)));
            devices = (List<Device>)crt_device.list();

            if (devices.size() == 0) {
                ok = ok && false;
                log.error("Could not find device with request id - " + device_id);
                xml.addKid(new BasicXmlData("message", "Информация об устройстве не может быть изменена, так как устройство не найдено в системе!"));
            }
        }

        if (status_id.equals("") || group_id.equals("") || (region_id.equals(""))) {
            ok = ok && false;
            if (status_id.equals("")) log.error("Host status is empty!");
            if (group_id.equals("")) log.error("Host group is empty!");
            if (region_id.equals("")) log.error("Region is empty!");
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
            Device device = devices.get(0);

            device.setName(name);
            device.setDescription(desc);
            device.setHoststatus(hoststatus);
            device.setHostgroup(hostgroup);
            device.setRegion(region);

            session.beginTransaction();
            session.update(device);
            session.getTransaction().commit();
            log.info("Update device - " + device.getName());

/*            List<Device> devices_local = (List<Device>)this.getConfig().getServletContext().getAttribute("deviceList");

            int num = -1;
            int num_replace_elem = -1;
            for (Device d : devices_local) {
                num++;
                if (d.getId() == Integer.valueOf(device_id)) {
                    num_replace_elem = num;
                }
            }
            if (num_replace_elem != -1) {
                devices_local.set(num_replace_elem,device);
            }*/

            DeviceManager deviceManager = DeviceManager.getInstance();
            Properties dev_list = deviceManager.getDevice_list();
            Device find_dev = (Device)dev_list.get(device.getName());
            find_dev.setName(device.getName());
            find_dev.setDescription(device.getDescription());
            find_dev.setHoststatus(device.getHoststatus());
            find_dev.setHostgroup(device.getHostgroup());
            find_dev.setRegion(device.getRegion());

            log.info("Update info about device - " + device.getName());
            xml.addKid(new BasicXmlData("message", "Информация об устройстве успешно изменена!"));

        }

        session.flush();
        session.close();

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);

        return null;
    }


}
