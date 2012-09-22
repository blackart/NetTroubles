package ru.blackart.dsi.infopanel.commands.device;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;

import javax.servlet.ServletConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DeviceManager {
    private static DeviceManager deviceManager;
    private Properties device_list;
//    private ServletConfig config;

    /*public void setConfig(ServletConfig config) {
        this.config = config;
    }*/

    public static DeviceManager getInstance() {
        if (deviceManager == null) {
            deviceManager = new DeviceManager();
        }

        return deviceManager;
    }

    public Properties getDevice_list() {
        return device_list;
    }

    private DeviceManager(/*ServletConfig config*/) {
        Session session = SessionFactorySingle.getSessionFactory().openSession();
        Criteria crt_7 = session.createCriteria(Device.class);
        ArrayList<Device> devices = new ArrayList<Device>(crt_7.list());

        this.device_list = new Properties();
        for (Device dev : devices) {
            this.device_list.put(dev.getName(),dev);
        }

//        config.getServletContext().setAttribute("dev_list", this.device_list);
    }

    public synchronized Device getDevice(Device device) {
        return this.getDevice(device.getName());
    }

    public synchronized Device getDevice(String device_name) {
        return (Device)this.device_list.get(device_name);
    }

    public synchronized Device updateDevice(Device dev, String device_name, String group, String desc) {
        Device device = null;
        if (this.getDevice(dev) == null) {
            device = this.addNewDevice(device_name, group, desc);
        } else {
            device = this.getDevice(dev);
            UpdateDevice updateDevice = new UpdateDevice(device);
            device = updateDevice.update(device_name, group, desc);
            this.device_list.put(dev.getName(), device);
        }
        return device;
    }

    public synchronized Device addNewDevice(String dev, String group, String desc) {
        Device device = null;
        if (this.getDevice(dev) != null) {
            device = this.updateDevice(this.getDevice(dev), dev,group, desc);
        } else {
            SaveDevice saveDevice = new SaveDevice();
            device = saveDevice.save(dev, group, desc);
            this.device_list.put(device.getName(),device);
        }
        return device;
    }

    public synchronized void deleteDevice() {

    }
}
