package ru.blackart.dsi.infopanel.commands.device;

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

import java.util.List;
import java.util.Properties;

public class EditSomeDevices extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private DeviceManager deviceManager = DeviceManager.getInstance();

    @Override
    public String execute() throws Exception {
        log.info("Editing device ...");

        String devices_id = this.getRequest().getParameter("ids");
        String desc = this.getRequest().getParameter("desc");
        String status_id = this.getRequest().getParameter("status_id");
        String group_id = this.getRequest().getParameter("group_id");
        String region_id = this.getRequest().getParameter("region_id");

        String[] device_id_arr = devices_id.split("\\|");
        String[] desc_arr = desc.split("\\|");
        String[] status_id_arr = status_id.split("\\|");
        String[] group_id_arr = group_id.split("\\|");
        String[] region_id_arr = region_id.split("\\|");


        log.info("Device parameters: id - " + devices_id + "; description - " + desc + "; status_id - " + status_id + "; group_id - " + group_id + ";");

        synchronized (deviceManager) {
            for (int i = 0; i < device_id_arr.length; i++) {
                int id;
                try {
                    id = Integer.valueOf(device_id_arr[i]);
                } catch (Exception e) {
                    log.error("Device id don't cast to Number!\n" + e.getStackTrace());
                    continue;
                }
                Device device = deviceManager.getDevice(id);

                if (device == null) {
                    log.error("Could not find device with request id - " + device_id_arr[i]);
                    continue;
                }
                if (status_id_arr[i].equals("")) {
                    log.error("Host status for device " + device.getName() + " [" + device.getId() + "] " + " empty!");
                    continue;
                }
                if (group_id_arr[i].equals("")) {
                    log.error("Host group for device " + device.getName() + " [" + device.getId() + "] " + " empty!");
                    continue;
                }
                if (region_id_arr[i].equals("")) {
                    log.error("Region for device " + device.getName() + " [" + device.getId() + "] " + " empty!");
                    continue;
                }

                Hoststatus hoststatus = deviceManager.getStatusFromDB(status_id_arr[i]);
                Hostgroup hostgroup = deviceManager.getHostGroupFromDB(group_id_arr[i]);
                Region region = deviceManager.getRegionFromDB(region_id_arr[i]);

                if (hoststatus == null) {
                    log.error("Host status for device " + device.getName() + " [" + device.getId() + "] " + " not found");
                    continue;
                } else if (hostgroup == null) {
                    log.error("Host group for device " + device.getName() + " [" + device.getId() + "] " + " not found");
                    continue;
                } else if (region == null) {
                    log.error("Region for device " + device.getName() + " [" + device.getId() + "] " + " not found");
                    continue;
                }

                device.setDescription(desc_arr[i]);
                device.setHoststatus(hoststatus);
                device.setHostgroup(hostgroup);
                device.setRegion(region);

                deviceManager.updateDevice(device);

                log.info("Update info about device - " + device.getName() + " [" + device.getId() + "]");
            }
        }

        return null;
    }
}
