package ru.blackart.dsi.infopanel.commands.filters.mainDeviceFilter;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.DeviceFilter;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.beans.TypeDeviceFilter;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;

import java.util.List;

public class AddFilter extends AbstractCommand {
    @Override
    public String execute() throws Exception {
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
        criteria.add(Restrictions.eq("name", name));
        List<DeviceFilter> deviceFilters = (List<DeviceFilter>)criteria.list();

        DeviceFilter deviceFilter;
        if (deviceFilters.size() > 0) {
            deviceFilter = deviceFilters.get(0);
            deviceFilter.setName(name);
            deviceFilter.setValue(value);
            deviceFilter.setType(typeDeviceFilter);
            deviceFilter.setPolicy(Boolean.valueOf(policy));
            deviceFilter.setEnable(Boolean.valueOf(enable));
        } else {
            deviceFilter = new DeviceFilter(name,value,typeDeviceFilter,Boolean.valueOf(policy));
        }

        session.save(deviceFilter);
        session.getTransaction().commit();

        criteria = session.createCriteria(TroubleList.class);
        criteria.add(Restrictions.eq("name", "main"));
        TroubleList troubleList = (TroubleList)criteria.list().get(0);
        troubleList.getFilters().add(deviceFilter);

        session.beginTransaction();

        session.save(troubleList);
        session.getTransaction().commit();

        session.flush();
        session.close();

        ManagerMainDeviceFilter.getInstance().addNewDeviceInputFilter(deviceFilter);

        return null;
    }
}
