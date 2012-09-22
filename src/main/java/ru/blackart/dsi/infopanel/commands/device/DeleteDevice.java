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

import java.io.OutputStream;
import java.util.List;

public class DeleteDevice extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {
        log.info("Deleting device ...");
        String device_id = this.getRequest().getParameter("id");
        log.info("Device id - " + device_id);
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        Criteria crt_device = session.createCriteria(Device.class);
        crt_device.add(Restrictions.eq("id", Integer.valueOf(device_id)));
        List<Device> devices = (List<Device>) crt_device.list();

        BasicXmlData xml = new BasicXmlData("device_message");

        if (devices.size() == 1) {
            session.beginTransaction();

            try {
                log.info("Device - " + devices.get(0).getName());

                session.delete(devices.get(0));
                session.getTransaction().commit();

                log.info("Device " + devices.get(0).getName() + " was deleted from DB");

                List<Device> devices_local = (List<Device>) this.getConfig().getServletContext().getAttribute("deviceList");

                Device dev_local = null;
                for (Device d : devices_local) {
                    if (d.getId() == Integer.valueOf(device_id)) {
                        dev_local = d;
                    }
                }
                if (dev_local != null) devices_local.remove(dev_local);

                log.info("Device " + dev_local.getName() + " was deleted from local storage");

                xml.addKid(new BasicXmlData("message", "Устройство успешно удалено!"));

            } catch (org.hibernate.exception.ConstraintViolationException e) {
                log.error("Could not delete device " + devices.get(0).getName());
                session.getTransaction().rollback();

                xml.addKid(new BasicXmlData("message", "Устройство не может быть удалено, так как оно связано с другими объектами приложения!"));
            }

            session.flush();
            session.close();
        } else if (devices.size() == 0) {
            log.error("Could not find device with id - " + device_id);
            xml.addKid(new BasicXmlData("message", "Устройство не может быть удалено, так как не было найдено в системе, обратитесь к разработчику!"));
        } else {
            log.error("Error. Some device have request id - " + device_id);
            xml.addKid(new BasicXmlData("message", "Устройство не может быть удалено, так как несколько устройств в системме имеют запрашиваемый id, обратитесь к разработчику!"));
        }

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);

        return null;
    }
}
