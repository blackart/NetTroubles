package ru.blackart.dsi.infopanel.commands.device;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;
import ru.blackart.dsi.infopanel.services.DeviceManager;

import java.io.IOException;
import java.io.OutputStream;

public class AddDevice extends AbstractCommand {
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
        log.info("Adding device ...");

        String name = this.getRequest().getParameter("name").trim();
        String desc = this.getRequest().getParameter("desc").trim();
        String status_id = this.getRequest().getParameter("status").replace("_hs_add", "").trim();
        String group_id = this.getRequest().getParameter("group").replace("_hg_add", "").trim();
        String region_id = this.getRequest().getParameter("region").trim();

        log.info("Input parameters : name - " + name + "; description - " + desc + "; status_id - " + status_id + "; group_id - " + group_id + "; region_id - " + region_id);

        OutputStream out = getResponse().getOutputStream();

        synchronized (deviceManager) {
            if (name.equals("")) {
                log.error("Host name value empty!");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство с пустым именем не может быть добавлено!");
                xml.save(out);
                return null;
            }

            Device device = deviceManager.getDevice(name);

            if (device != null) {
                log.error("Host name - " + name + " already exist");

                BasicXmlData xml = this.generateXMLResponse("message", "Устройство c таким именем уже существует!");
                xml.save(out);
                return null;
            }

            if (status_id.equals("")) {
                log.error("Host status value empty!");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, если значение его статуса пусто!");
                xml.save(out);
                return null;
            }

            if (group_id.equals("")) {
                log.error("Host group value empty!");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, если значение его группы пусто!");
                xml.save(out);
                return null;
            }

            if (region_id.equals("")) {
                log.error("Region value empty!");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не может быть добавлено, если значение его региона пусто!");
                xml.save(out);
                return null;
            }

            Hoststatus hoststatus = deviceManager.getStatusFromDB(status_id);
            Hostgroup hostgroup = deviceManager.getHostGroupFromDB(group_id);
            Region region = deviceManager.getRegionFromDB(region_id);

            if (hostgroup == null) {
                log.error("Not found host group with " + group_id + " id");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не добавлено. Группа устройства не найдена.");
                xml.save(out);
                return null;
            }
            if (hoststatus == null) {
                log.error("Not found host status with " + status_id + " id");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не добавлено. Не удалось присвоить статус устройстве. Такого статуса не существует.");
                xml.save(out);
                return null;
            }
            if (region == null) {
                log.error("Not found region with " + region_id + " id");
                BasicXmlData xml = this.generateXMLResponse("message", "Устройство не добавлено. Регион для устройства указан неверно");
                xml.save(out);
                return null;
            }

            log.info("Host group - " + hostgroup.getName() + ", host status - " + hoststatus.getName() + ", region - " + region.getName());

            device = new Device();

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
