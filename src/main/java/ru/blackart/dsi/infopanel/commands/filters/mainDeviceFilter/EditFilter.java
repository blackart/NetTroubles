package ru.blackart.dsi.infopanel.commands.filters.mainDeviceFilter;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.DeviceFilter;
import ru.blackart.dsi.infopanel.beans.TypeDeviceFilter;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;

import java.util.List;

public class EditFilter extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String id = this.getRequest().getParameter("id");
        String name = this.getRequest().getParameter("name");
        String value = this.getRequest().getParameter("value");
        String type = this.getRequest().getParameter("type");
        String policy = this.getRequest().getParameter("policy");
        String enable = this.getRequest().getParameter("enable");

        Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(TypeDeviceFilter.class);
        criteria.add(Restrictions.eq("id", Integer.valueOf(type)));
        List<TypeDeviceFilter> typeDeviceFilters_array = criteria.list();
        TypeDeviceFilter typeDeviceFilter = typeDeviceFilters_array.get(0);

        criteria = session.createCriteria(DeviceFilter.class);
        criteria.add(Restrictions.eq("id", Integer.valueOf(id)));
        List<DeviceFilter> deviceFilters = (List<DeviceFilter>)criteria.list();

        if (deviceFilters.size() >0) {
            DeviceFilter deviceFilter = deviceFilters.get(0);
            deviceFilter.setName(name);
            deviceFilter.setValue(value);
            deviceFilter.setType(typeDeviceFilter);
            deviceFilter.setPolicy(Boolean.valueOf(policy));
            deviceFilter.setEnable(Boolean.valueOf(enable));

            session.save(deviceFilter);
            
            ManagerMainDeviceFilter.getInstance().editDeviceInputFilter(deviceFilter);
        }

        session.getTransaction().commit();
        session.flush();
        session.close();

        return null;
    }
}
