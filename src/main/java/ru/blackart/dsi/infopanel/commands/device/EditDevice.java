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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class EditDevice extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private DeviceManager deviceManager = DeviceManager.getInstance();

    /**
     * Генерация XML данных.
     *
     * @param prop  параметр
     * @param value свойство
     * @return BasicXmlData данный в формате xml
     * @throws java.io.IOException
     */
    private BasicXmlData generateXMLResponse(String prop, String value) throws IOException {
        BasicXmlData xml = new BasicXmlData("device_message");
        xml.addKid(new BasicXmlData(prop, value));
        return xml;
    }

    @Override
    public String execute() throws Exception {
        log.info("Editing device ...");

        String device_id = this.getRequest().getParameter("id");
        String name = this.getRequest().getParameter("name");
        String desc = this.getRequest().getParameter("desc");
        String status_id = this.getRequest().getParameter("status");
        String group_id = this.getRequest().getParameter("group");
        String region_id = this.getRequest().getParameter("region");

        OutputStream out = getResponse().getOutputStream();

        log.info("Input parameters : name - " + name + "; description - " + desc + "; status_id - " + status_id + "; group_id - " + group_id + "; region_id - " + region_id);

        Device device = null;
        Hostgroup hostgroup = null;
        Hoststatus hoststatus = null;
        Region region = null;

        synchronized (deviceManager) {
            if (name.equals("")) {
                log.error("Host name is empty!");
                BasicXmlData xml = this.generateXMLResponse("message", "Информация об устройстве не может быть изменена, так как имя устройства не может быть пустым!");
                xml.save(out);
                return null;
            } else {
                int id;
                try {
                    id = Integer.valueOf(device_id);
                } catch (Exception e) {
                    log.error("Device id don't cast to Number!\n" + e.getStackTrace());
                    return null;
                }

                device = deviceManager.getDevice(id);

                if (device == null) {
                    log.error("Could not find device with request id - " + device_id);
                    BasicXmlData xml = this.generateXMLResponse("message", "Информация об устройстве не может быть изменена, так как устройство не найдено в системе!");
                    xml.save(out);
                    return null;
                }
            }

            if (status_id.equals("") || group_id.equals("") || (region_id.equals(""))) {
                if (status_id.equals("")) log.error("Host status is empty!");
                if (group_id.equals("")) log.error("Host group is empty!");
                if (region_id.equals("")) log.error("Region is empty!");

                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, если значение его статуса, группы или региона пусто!");
                xml.save(out);
                return null;
            } else {
                hoststatus = deviceManager.getStatus(status_id);
                hostgroup = deviceManager.getHostGroup(group_id);
                region = deviceManager.getRegion(region_id);

                if ((hoststatus == null) || (hostgroup == null) || (region == null)) {
                    if (hostgroup == null) log.error("Not found host group with " + group_id + " id");
                    if (hoststatus == null) log.error("Not found host status with " + status_id + " id");
                    if (region == null) log.error("Not found region with " + region_id + " id");

                    BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, если статус, группа или регион с таким значением не существует в системе!");
                    xml.save(out);
                    return null;
                } else {
                    log.info("Host group - " + hostgroup.getName() + ", host status - " + hoststatus.getName() + ", region - " + region.getName());
                }
            }

            device.setName(name);
            device.setDescription(desc);
            device.setHoststatus(hoststatus);
            device.setHostgroup(hostgroup);
            device.setRegion(region);

            deviceManager.updateExistDevice(device);

            log.info("Update device - " + device.getName());

            BasicXmlData xml = this.generateXMLResponse("message", "Информация об устройстве успешно изменена!");
            xml.save(out);
        }
        return null;
    }
}
