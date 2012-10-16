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
import ru.blackart.dsi.infopanel.access.menu.MenuItem;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Users;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import javax.servlet.ServletConfig;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class AccessService {
    private static AccessService AccessService;
    private Session session;
    private Gson gson = new Gson();
    private HashMap<Integer, Menu> menuForGroups = new HashMap<Integer, Menu>();
    private HashMap<Integer, HashMap<Integer, MenuItem>> indexingMenuForGroups = new HashMap<Integer, HashMap<Integer, MenuItem>>();
    private HashMap<Integer, Group> groups = new HashMap<Integer, Group>();
    private Menu canonicalMenu;
    private HashMap<Integer, MenuItem> canonicalIndexingMenu = new HashMap<Integer, MenuItem>();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private synchronized Session getSession() {
        return session;
    }

    public HashMap<Integer, Menu> getMenuForGroups() {
        return menuForGroups;
    }

    public HashMap<Integer, Group> getGroups() {
        return groups;
    }

    public Menu getCanonicalMenu() {
        return canonicalMenu;
    }

    public HashMap<Integer, MenuItem> getCanonicalIndexingMenu() {
        return canonicalIndexingMenu;
    }

    private HashMap<Integer, MenuItem> indexingMenu(List<MenuItem> menuItems, HashMap<Integer, MenuItem> targetIndexingMenu) {
        if (targetIndexingMenu == null) return null;
        for (MenuItem menuItem : menuItems) {
            targetIndexingMenu.put(menuItem.getId(), menuItem);
            if (menuItem.getItems() != null) this.indexingMenu(menuItem.getItems(), targetIndexingMenu);
        }
        return targetIndexingMenu;
    }

    private AccessService() {
        this.session = SessionFactorySingle.getSessionFactory().openSession();
        List<Group> groupList = this.getGroupsFromDB() ;
        for (Group group : groupList) {
            this.groups.put(group.getId(), group);
        }
        this.createMenuForGroups(groups.values());
        TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
        ServletConfig config = troubleListsManager.getHTTPServletConfig();

        Properties paths = (Properties) config.getServletContext().getAttribute("pathToDataFile");
        InputStream inputStream = config.getServletContext().getResourceAsStream(paths.getProperty("menuConfig"));
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.canonicalMenu = this.gson.fromJson(inputStreamReader, Menu.class);

        this.indexingMenu(this.getCanonicalMenu().getItems(), this.canonicalIndexingMenu);

        System.out.print("");
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

    public void createMenuForGroups(Collection<Group> groups) {
        for (Group group : groups) {
            Menu menu = this.createMenuForGroup(group);
            this.menuForGroups.put(group.getId(), menu);
            this.indexingMenuForGroups.put(group.getId(), this.indexingMenu(menu.getItems(), new HashMap<Integer, MenuItem>()));
        }
    }

    private Menu sortMenu(Menu menu) {
        for (MenuItem menuGroup : menu.getItems()) {
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

    public synchronized boolean saveGroup(Group group) {
        this.saveGroupToDB(group);
        this.groups.put(group.getId(), group);
        Menu menu = this.createMenuForGroup(group);
        this.menuForGroups.put(group.getId(), menu);
        this.indexingMenuForGroups.put(group.getId(), this.indexingMenu(menu.getItems(), new HashMap<Integer, MenuItem>()));
        return true;
    }

    private synchronized void saveGroupToDB(Group group) {
        Session session = this.getSession();
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
