package ru.blackart.dsi.infopanel.commands.filters.mainDeviceFilter;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.DeviceFilter;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;

public class DeleteFilter extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String id = this.getRequest().getParameter("id");

        Session session = SessionFactorySingle.getSessionFactory().openSession();


        Criteria criteria = session.createCriteria(DeviceFilter.class);
        criteria.add(Restrictions.eq("id", Integer.valueOf(id)));
        DeviceFilter deviceFilter = (DeviceFilter)criteria.list().get(0);

        Criteria criteria_trouble_list = session.createCriteria(TroubleList.class);
        criteria_trouble_list.add(Restrictions.eq("name", "main"));
        TroubleList troubleList = (TroubleList)criteria_trouble_list.list().get(0);
        troubleList.getFilters().remove(deviceFilter);

        session.beginTransaction();

        session.save(troubleList);
        session.delete(deviceFilter);

        session.getTransaction().commit();

        session.flush();
        session.close();

        ManagerMainDeviceFilter.getInstance().deleteDeviceInputFilter(deviceFilter);

        return null;
    }
}
