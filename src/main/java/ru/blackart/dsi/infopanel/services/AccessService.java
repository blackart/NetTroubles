package ru.blackart.dsi.infopanel.services;

import com.google.gson.Gson;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.access.menu.MenuGroup;
import ru.blackart.dsi.infopanel.access.menu.MenuItem;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Users;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import javax.servlet.ServletConfig;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class AccessService {
    private static AccessService AccessService;
    private Session session;
    private HashMap<Group, Menu> menuConfigsForGroups = new HashMap<Group, Menu>();
    private List<Group> groups = new ArrayList<Group>();
    private Gson gson = new Gson();
    private Menu canonicalMenu;
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private synchronized Session getSession() {
        return session;
    }

    public HashMap<Group, Menu> getMenuConfigsForGroups() {
        return menuConfigsForGroups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Menu getCanonicalMenu() {
        return canonicalMenu;
    }

    private AccessService() {
        this.session = SessionFactorySingle.getSessionFactory().openSession();
        this.groups.addAll(this.getGroupsFromDB());
        this.createMenuForGroups(groups);

        TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
        ServletConfig config = troubleListsManager.getHTTPServletConfig();

        Properties paths = (Properties) config.getServletContext().getAttribute("pathToDataFile");
        InputStream inputStream = config.getServletContext().getResourceAsStream(paths.getProperty("menuConfig"));
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.canonicalMenu = this.gson.fromJson(inputStreamReader, Menu.class);
    }

    public static AccessService getInstance() {
        if (AccessService == null) {
            AccessService = new AccessService();
        }

        return AccessService;
    }

    public Menu createMenuForGroup(Group group) {
        String config = group.getMenuConfig();
        Menu menu = this.gson.fromJson(config, Menu.class);
        return this.sortMenu(menu);
    }

    public void createMenuForGroups(List<Group> groups) {
        for (Group group : groups) {
            this.menuConfigsForGroups.put(group, this.createMenuForGroup(group));
        }
    }

    private Menu sortMenu(Menu menu) {
        for (MenuGroup menuGroup : menu.getGroups()) {
            List<MenuItem> menuItems = menuGroup.getItems();
            if (menuItems != null) {
                for (int i=0; i < menuItems.size(); i++) {
                    for (int j=0; j < i; j++) {
                        if (menuItems.get(i).getPosition() < menuItems.get(j).getPosition()) {
                            MenuItem replaceItem = menuItems.get(i);
                            menuItems.remove(i);
                            menuItems.add(i,menuItems.get(j));
                            menuItems.remove(j);
                            menuItems.add(i,replaceItem);
                        }
                    }
                }
            }
        }
        return menu;
    }

    private List<Group> getGroupsFromDB() {
        Criteria crt_trouble = this.getSession().createCriteria(Group.class);
        return (List<Group>)crt_trouble.list();
    }

    public synchronized void updateUser(Users user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(user);
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

    public synchronized void saveUser(Users user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(user);
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

    public synchronized void deleteUser(Users user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(user);
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

    public Users getUser(int id) {
        Criteria crt_trouble = this.getSession().createCriteria(Users.class);
        crt_trouble.add(Restrictions.eq("id", id));
        return (Users)crt_trouble.list().get(0);
    }

    public Users getUser(String login) {
        Criteria crt_trouble = this.getSession().createCriteria(Users.class);
        crt_trouble.add(Restrictions.eq("login", login));
        return (Users)crt_trouble.list().get(0);
    }
}
