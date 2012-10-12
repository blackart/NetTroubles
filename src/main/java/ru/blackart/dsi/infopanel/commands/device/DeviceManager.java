package ru.blackart.dsi.infopanel.commands.device;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.beans.Region;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import java.util.*;

public class DeviceManager {
    private static DeviceManager deviceManager;
    private HashMap device_list;
    private Session session;
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public static synchronized DeviceManager getInstance() {
        if (deviceManager == null) {
            deviceManager = new DeviceManager();
        }

        return deviceManager;
    }

    public HashMap getDevice_list() {
        return device_list;
    }

    private DeviceManager() {
        this.session = SessionFactorySingle.getSessionFactory().openSession();
        Criteria crt_7 = session.createCriteria(Device.class);
        ArrayList<Device> devices = new ArrayList<Device>(crt_7.list());

        this.device_list = new HashMap<String, Device>();
        for (Device dev : devices) {
            this.device_list.put(dev.getName(), dev);
        }
    }

    /*-----------------------------------------get-----------------------------------------------------*/
    public synchronized Device getDevice(Device device) {
        return this.getDevice(device.getName());
    }

    public synchronized Device getDevice(String device_name) {
        return (Device)this.device_list.get(device_name);
    }

    public synchronized Device getDevice(int id) {
        for (Device d : (Collection<Device>)this.device_list.values()) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public synchronized Device getDeviceFromDB(Integer id) {
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

    /*-----------------------------------------update--------------------------------------------------*/

    public synchronized boolean updateDevice(Device dev, String device_name, String group, String desc) {
        if (this.getDevice(dev) == null) {
            return false;
        } else {
            Device device = this.getDevice(dev);

            device.setName(device_name);
            device.setDescription(desc);

            Hostgroup hostgroup = this.getHostGroupFromDB(group);
            if (hostgroup == null) {
                hostgroup = new Hostgroup();
                hostgroup.setNum(Integer.valueOf(group));
                hostgroup.setName(group);
                this.saveHostGroupFromDB(hostgroup);
            }

            device.setHostgroup(hostgroup);

            if (this.updateDeviceFromDB(device)) {
                this.device_list.put(dev.getName(), device);
            } else {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean updateDevice(Device device) {
        if (this.getDevice(device) == null) {
            return false;
        } else {
            if (this.updateDeviceFromDB(device)) {
                this.device_list.put(device.getName(), device);
            } else {
                return false;
            }
        }
        return true;
    }

    private synchronized boolean updateDeviceFromDB(Device device) {
        try {
            session.beginTransaction();
            session.update(device);
            session.getTransaction().commit();
            session.clear();
            return true;
        } catch (HibernateException e) {
            log.error("" + Arrays.toString(e.getStackTrace()));
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
    }

    /*-----------------------------------------add-----------------------------------------------------*/

    public synchronized boolean addNewDevice(String name, String group, String desc) {
        if (this.getDevice(name) != null) {
            return this.updateDevice(this.getDevice(name), name, group, desc);
        } else {
            Hostgroup hostgroup = this.getHostGroupFromDB(group);
            if (hostgroup == null) {
                hostgroup = new Hostgroup();
                hostgroup.setNum(Integer.valueOf(group));
                hostgroup.setName(group);
                this.saveHostGroupFromDB(hostgroup);
            }

            Device device = new Device();
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

            if (this.saveDeviceToDB(device)) {
                this.device_list.put(device.getName(), device);
            } else {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean addNewDevice(Device device) {
        if (this.getDevice(device) != null) {
             return this.updateDevice(device, device.getName(), device.getHostgroup().getName(), device.getDescription());
        } else {
            if (this.saveDeviceToDB(device)) {
                this.device_list.put(device.getName(), device);
            } else {
                return false;
            }
        }
        return true;
    }

    private synchronized boolean saveDeviceToDB(Device device) {
        try {
            session.beginTransaction();
            session.save(device);
            session.getTransaction().commit();
            session.clear();
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
    }

    /*-----------------------------------------delete-----------------------------------------------------*/
    public boolean deleteDevice(String name) {
        Device device = this.getDevice(name);
        return this.deleteDevice(device);
    }

    public boolean deleteDevice(Device device) {
        this.getDevice(device);
        boolean result = this.deleteDeviceFromDB(device);
        if (result) {
            try {
                this.device_list.remove(device.getName());
            } catch (Exception e) {
                log.error("" + Arrays.toString(e.getStackTrace()));
            }
        }
        return result;
    }

    private synchronized boolean deleteDeviceFromDB(Device device) {
        try {
            session.beginTransaction();
            session.delete(device);
            session.getTransaction().commit();
            session.clear();
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

    /*-----------------------------------------public non device methods--------------------------------------------------*/
    public synchronized Hostgroup getHostGroupFromDB(String id) {
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

    public synchronized Hoststatus getStatusFromDB(String id) {
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

    public synchronized Region getRegionFromDB(String id) {
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

    public synchronized void saveHostGroupFromDB(Hostgroup group) {
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
