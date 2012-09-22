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
import java.util.List;

public class EditGroup extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String id = this.getRequest().getParameter("id");
        String group_name = this.getRequest().getParameter("name");
        String tabs = this.getRequest().getParameter("tabs");

        String[] tabs_arr = tabs.split(";");

        List<Tab> menu = new ArrayList<Tab>();
        for (int i=0; i<tabs_arr.length; i++) {
            String[] tabs_arr_spl = tabs_arr[i].split("_");

            Session session = SessionFactorySingle.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria crt_trouble = session.createCriteria(Tab.class);
            crt_trouble.add(Restrictions.eq("id", Integer.valueOf(tabs_arr_spl[0])));
            menu.add((Tab) crt_trouble.list().get(0));

            session.getTransaction().commit();
            session.flush();
            session.close();
        }

        Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria crt_group = session.createCriteria(Group.class);
        crt_group.add(Restrictions.eq("id", Integer.valueOf(id)));
        Group group = (Group)crt_group.list().get(0);

        group.setName(group_name);
        group.setTabs(menu);

        session.save(group);

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