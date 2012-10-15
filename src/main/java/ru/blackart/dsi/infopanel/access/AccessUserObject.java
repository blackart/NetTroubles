package ru.blackart.dsi.infopanel.access;

import com.google.gson.Gson;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Tab;
import ru.blackart.dsi.infopanel.beans.Users;

import java.util.ArrayList;
import java.util.List;

public class AccessUserObject {
    private Users user;
    private List<AccessItemMenu> tabs;
    private Menu menu;

    public AccessUserObject(Users user) {
        this.user = user;
        this.tabs = new ArrayList<AccessItemMenu>();

        Gson gson = new Gson();

        Session session = SessionFactorySingle.getSessionFactory().openSession();

        session.beginTransaction();
        Criteria criteria = session.createCriteria(Group.class);

        criteria.add(Restrictions.eq("id", user.getGroup_id().getId()));
        List<Group> groups = criteria.list();

        try {
            Group group = groups.get(0);
            this.menu = gson.fromJson(group.getMenuConfig(), Menu.class);

            List<Tab> tabs = group.getTabs();
            this.tabs = this.generateMenuTabs(tabs);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        session.getTransaction().commit();
        session.flush();
        session.close();
    }

    private static List<AccessItemMenu> sortMenu(List<AccessItemMenu> menu) {
        for (int i=0; i < menu.size(); i++) {
            for (int j=0; j < i; j++) {
                if (menu.get(i).getPosition() < menu.get(j).getPosition()) {
                    AccessItemMenu replaceItem = menu.get(i);
                    menu.remove(i);
                    menu.add(i,menu.get(j));
                    menu.remove(j);
                    menu.add(i,replaceItem);
                }
            }
        }
        return menu;
    }

    public static List<AccessItemMenu> generateMenuTabs(List<Tab> tabs) {
        ArrayList<AccessItemMenu> menu = new ArrayList<AccessItemMenu>();
        for (Tab tab : tabs) {
            if (tab.getType().equals("tab")) {
                List<AccessTab> childrens = new ArrayList<AccessTab>();
                for (Tab tab_c : tabs) {
                    if ((tab_c.getType().equals("item")) && (tab_c.getMenu_group() == tab.getMenu_group())) {
                        childrens.add(new AccessTab(true,tab_c));
                    }
                }
                menu.add(new AccessItemMenu(new AccessTab(true,tab),childrens,tab.getGroup_position()));
            }
        }
        return sortMenu(menu);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<AccessItemMenu> getTabs() {
        return tabs;
    }

    public void setTabs(List<AccessItemMenu> tabs) {
        this.tabs = tabs;
    }


}