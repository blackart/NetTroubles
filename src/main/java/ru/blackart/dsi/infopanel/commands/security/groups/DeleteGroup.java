package ru.blackart.dsi.infopanel.commands.security.groups;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.access.AccessMenuForGroup;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Tab;

import java.util.ArrayList;

public class DeleteGroup extends AbstractCommand {
    @Override
    public String execute() throws Exception {

        String group_id = this.getRequest().getParameter("id");

        Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria crt_groups = session.createCriteria(Group.class);
        crt_groups.add(Restrictions.eq("id", Integer.valueOf(group_id)));
        Group group = (Group)crt_groups.list().get(0);

        session.delete(group);

        session.getTransaction().commit();
        session.flush();
        session.close();

        session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria crt_4 = session.createCriteria(Tab.class);
        ArrayList<Tab> all_tabs = new ArrayList<Tab>(crt_4.list());
        getConfig().getServletContext().setAttribute("tabs", all_tabs);

        Criteria crt_3 = session.createCriteria(Group.class);
        ArrayList<Group> groups = new ArrayList<Group>(crt_3.list());
        ArrayList<AccessMenuForGroup> tabs_of_groups = new ArrayList<AccessMenuForGroup>();
        for (Group g : groups) {
            g.setTabs(new ArrayList<Tab>(g.getTabs()));
            tabs_of_groups.add(new AccessMenuForGroup(g,all_tabs));
        }
        getConfig().getServletContext().setAttribute("tabs_of_groups", tabs_of_groups);

        getConfig().getServletContext().setAttribute("groups", groups);

        session.getTransaction().commit();
        session.flush();
        session.close();

        return null;
    }
}