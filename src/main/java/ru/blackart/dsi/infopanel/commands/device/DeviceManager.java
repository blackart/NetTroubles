package ru.blackart.dsi.infopanel.commands.device;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DeviceManager {
    private static DeviceManager deviceManager;
    private Properties device_list;
    private Session session;

    public static synchronized DeviceManager getInstance() {
        if (deviceManager == null) {
            deviceManager = new DeviceManager();
        }

        return deviceManager;
    }

    public Properties getDevice_list() {
        return device_list;
    }

    private DeviceManager() {
        this.session = SessionFactorySingle.getSessionFactory().openSession();
        Criteria crt_7 = session.createCriteria(Device.class);
        ArrayList<Device> devices = new ArrayList<Device>(crt_7.list());

        this.device_list = new Properties();
        for (Device dev : devices) {
            this.device_list.put(dev.getName(),dev);
        }
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

            device.setName(device_name);
            device.setDescription(desc);

            Hostgroup hostgroup = this.getHostGroup(group);
            if (hostgroup == null) {
                hostgroup = new Hostgroup();
                hostgroup.setNum(Integer.valueOf(group));
                hostgroup.setName(group);
                this.saveHostGroup(hostgroup);
            }

            device.setHostgroup(hostgroup);
            this.updateDevice(device);
            this.device_list.put(dev.getName(), device);
        }
        return device;
    }

    public synchronized Device addNewDevice(String name, String group, String desc) {
        Device device;
        if (this.getDevice(name) != null) {
            device = this.updateDevice(this.getDevice(name), name, group, desc);
        } else {
            Hostgroup hostgroup = this.getHostGroup(group);
            if (hostgroup == null) {
                hostgroup = new Hostgroup();
                hostgroup.setNum(Integer.valueOf(group));
                hostgroup.setName(group);
                this.saveHostGroup(hostgroup);
            }

            device = new Device();
            device.setName(name);
            device.setHostgroup(hostgroup);
            device.setDescription(desc);

            TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
            Region region;
            synchronized (troubleListsManager) {
                List<Region> regions = (List<Region>)troubleListsManager.getHTTPServletConfig().getServletContext().getAttribute("regions");
                region = CheckRegionDevice.getRegionForDevice(device, regions);
            }

            device.setRegion(region);
            this.saveDevice(device);
            this.device_list.put(device.getName(), device);
        }
        return device;
    }

    public synchronized Device updateExistDevice(Device device) {
        if (this.getDevice(device) == null) {
            return null;
        } else {
            this.updateDevice(device);
            this.device_list.put(device.getName(), device);
        }
        return device;
    }

    public synchronized Device addNewDevice(Device device) {
        if (this.getDevice(device) != null) {
            this.updateDevice(device, device.getName(), device.getHostgroup().getName(), device.getDescription());
        } else {
            this.saveDevice(device);
            this.device_list.put(device.getName(), device);
        }
        return device;
    }

    public synchronized Boolean deleteDevice(Device device) {
        try {
            session.beginTransaction();
            session.delete(device);
            session.getTransaction().commit();
            session.clear();

            this.device_list.remove(device.getName());
            return true;
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            return false;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
    }

    public synchronized void saveDevice(Device device) {
        try {
            session.beginTransaction();
            session.save(device);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
    }

    public synchronized void updateDevice(Device device) {
        try {
            session.beginTransaction();
            session.update(device);
            session.getTransaction().commit();
            session.clear();
            this.device_list.put(device.getName(), device);
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
    }

    public synchronized Device getDevice(Integer id) {
        Device device = null;
        try {
            Criteria crt = session.createCriteria(Device.class);
            crt.add(Restrictions.eq("id", id));
            List<Device> list = (List<Device>)crt.list();
            device = list.size() > 0 ? list.get(0) : null;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
        return device;
    }

    public synchronized Hostgroup getHostGroup(String id) {
        int int_id;
        try {
            int_id = Integer.valueOf(id);
        } catch (Exception e) {
            return null;
        }
        Criteria criteria = session.createCriteria(Hostgroup.class);
        criteria.add(Restrictions.eq("id", int_id));
        List<Hostgroup> list = (List<Hostgroup>)criteria.list();
        return list.size() > 0 ? list.get(0) : null;
    }

    public synchronized Hoststatus getStatus(String id) {
        int int_id;
        try {
            int_id = Integer.valueOf(id);
        } catch (Exception e) {
            return null;
        }
        Criteria criteria = session.createCriteria(Hoststatus.class);
        criteria.add(Restrictions.eq("id", int_id));
        List<Hoststatus> list = (List<Hoststatus>)criteria.list();
        return list.size() > 0 ? list.get(0) : null;
    }

    public synchronized Region getRegion(String id) {
        int int_id;
        try {
            int_id = Integer.valueOf(id);
        } catch (Exception e) {
            return null;
        }
        Criteria criteria = session.createCriteria(Region.class);
        criteria.add(Restrictions.eq("id", int_id));
        List<Region> list = (List<Region>)criteria.list();
        return list.size() > 0 ? list.get(0) : null;
    }

    public synchronized void saveHostGroup(Hostgroup group) {
        Session session = this.session;
        try {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
    }
}
