package ru.blackart.dsi.infopanel.commands.device;

import org.hibernate.Criteria;
import org.hibernate.Session;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Region;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class CheckRegionDevice extends AbstractCommand {

    @Override
    public String execute() throws Exception {
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        Criteria crt_device = session.createCriteria(Device.class);

        ArrayList<Region> regions = (ArrayList<Region>) this.getConfig().getServletContext().getAttribute("regions");

        Properties regions_all = new Properties();
        for (Region r : regions) {
            regions_all.setProperty(r.getName(),".*(" + r.getPrefix() + ")" );
        }

        for (Device d : (List<Device>)crt_device.list()) {
            Boolean irk = true;
            for (Enumeration en = regions_all.keys(); en.hasMoreElements();) {
                String region_ = en.nextElement().toString();
                if (Pattern.matches(regions_all.getProperty(region_), d.getName()) && (!region_.equals("Иркутск"))) {
                    Region region_dest = null;
                    for (Region r : regions) {
                        if (r.getName().equals(region_)) region_dest = r;
                    }
                    if (region_dest != null) {
                        d.setRegion(region_dest);
                        session.beginTransaction();
                        session.save(d);
                        session.getTransaction().commit();
                    }
                    irk = false;
                }
            }
            if (irk) {
                Region region_dest = null;
                for (Region r : regions) {
                    if (r.getName().equals("Иркутск")) region_dest = r;
                }
                if (region_dest != null) {
                    d.setRegion(region_dest);
                    session.beginTransaction();
                    session.save(d);
                    session.getTransaction().commit();
                }
            }
        }

        session.flush();
        session.close();

        return null;
    }

    public static synchronized Region getRegionForDevice(Device device, List<Region> regions) {
        Region return_region = null;

        Properties regions_all = new Properties();
        for (Region r : regions) {
            regions_all.setProperty(r.getName(), ".*(" + r.getPrefix() + ")");
        }

        Boolean irk = true;
        for (Enumeration en = regions_all.keys(); en.hasMoreElements();) {
            String region_ = en.nextElement().toString();
            if (Pattern.matches(regions_all.getProperty(region_), device.getName()) && (!region_.equals("Иркутск"))) {
                Region region_dest = null;
                for (Region r : regions) {
                    if (r.getName().equals(region_)) region_dest = r;
                }
                if (region_dest != null) {
                    return_region = region_dest;
                }
                irk = false;
            }
        }
        if (irk) {
            Region region_dest = null;
            for (Region r : regions) {
                if (r.getName().equals("Иркутск")) region_dest = r;
            }
            if (region_dest != null) {
                return_region = region_dest;
            }
        }

        return return_region;
    }
}
