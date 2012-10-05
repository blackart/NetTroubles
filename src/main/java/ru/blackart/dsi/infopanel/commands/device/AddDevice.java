package ru.blackart.dsi.infopanel.commands.device;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class AddDevice extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private DeviceManager deviceManager = DeviceManager.getInstance();

    /**
     * Генерация XML данных.
     * @param prop параметр
     * @param value свойство
     * @throws java.io.IOException
     * @return BasicXmlData данный в формате xml
     */
    private BasicXmlData generateXMLResponse(String prop, String value) throws IOException {
        BasicXmlData xml = new BasicXmlData("device_message");
        xml.addKid(new BasicXmlData(prop, value));
        return xml;
    }

    @Override
    public String execute() throws Exception {
        log.info("Adding device ...");

        String name = this.getRequest().getParameter("name").trim();
        String desc = this.getRequest().getParameter("desc").trim();
        String status_id = this.getRequest().getParameter("status").replace("_hs_add", "").trim();
        String group_id = this.getRequest().getParameter("group").replace("_hg_add","").trim();
        String region_id = this.getRequest().getParameter("region").trim();

        log.info("Input parameters : name - " + name + "; description - " + desc  + "; status_id - " + status_id + "; group_id - " + group_id + "; region_id - " + region_id);

        OutputStream out = getResponse().getOutputStream();

        synchronized (deviceManager) {
            Hostgroup hostgroup;
            Hoststatus hoststatus;
            Region region;

            if (name.equals("")) {
                log.error("Host name is empty!");

                BasicXmlData xml = this.generateXMLResponse("message", "Устройство с пустым именем не может быть добавлено!");
                xml.save(out);
                return null;
            } else {
                Device device = deviceManager.getDevice(name);
                if (device != null) {
                    log.error("Host name - " + name + " already exist");

                    BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, так как в системе уже существует устройство с таким именем!");
                    xml.save(out);
                    return null;
                }
            }

            if (status_id.equals("") || group_id.equals("") || region_id.equals("")) {
                if (status_id.equals("")) log.error("Host status is empty!");
                if (group_id.equals("")) log.error("Host group is empty!");
                if (region_id.equals("")) log.error("Region host is empty!");

                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, если значение статуса, группы или региона пусто!");
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

            Device device = new Device();

            device.setName(name);
            device.setDescription(desc);
            device.setHoststatus(hoststatus);
            device.setHostgroup(hostgroup);
            device.setRegion(region);

            deviceManager.addNewDevice(device);

            log.info("Save device - " + device.getName());

            BasicXmlData xml = this.generateXMLResponse("message", "Устройство успешно добавлено!");
            xml.save(out);
        }
        return null;
    }
}
