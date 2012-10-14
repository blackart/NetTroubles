package ru.blackart.dsi.infopanel.commands.device;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.services.DeviceManager;

import java.io.OutputStream;

public class DeleteDevice extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private DeviceManager deviceManager = DeviceManager.getInstance();

    @Override
    public String execute() throws Exception {
        log.info("Deleting device ...");
        String device_id = this.getRequest().getParameter("id");
        log.info("Device id - " + device_id);

        synchronized (deviceManager) {
            BasicXmlData xml = new BasicXmlData("device_message");

            int id = -1;
            try {
                id = Integer.valueOf(device_id);
            } catch (Exception e) {
                e.printStackTrace();
                xml.addKid(new BasicXmlData("message", "ID устройства не верный!"));

                OutputStream out = getResponse().getOutputStream();
                xml.save(out);
                return null;
            }

            Device device = deviceManager.getDevice(id);

            if (device != null) {
                log.info("Device - " + device.getName());
                Boolean result = deviceManager.deleteDevice(device);

                if (result) {
                    log.info("Device " + device.getName() + " was deleted from DB");
                    xml.addKid(new BasicXmlData("message", "Устройство успешно удалено!"));
                } else {
                    log.error("Could not delete device " + device.getName());
                    xml.addKid(new BasicXmlData("message", "Устройство не может быть удалено, так как оно связано с другими объектами приложения!"));
                }
            } else {
                log.error("Could not find device with id - " + device_id);
                xml.addKid(new BasicXmlData("message", "Устройство не может быть удалено, так как не было найдено в системе!"));
            }

            OutputStream out = getResponse().getOutputStream();
            xml.save(out);
        }
        return null;
    }
}
