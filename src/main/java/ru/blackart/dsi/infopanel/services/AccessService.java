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
    private Menu canonicalMenu;
    private HashMap<Integer, MenuItem> canonicalIndexingMenu = new HashMap<Integer, MenuItem>();

    private HashMap<Integer, Menu> menuForGroups = new HashMap<Integer, Menu>();
    private HashMap<Integer, HashMap<Integer, MenuItem>> indexingMenuForGroups = new HashMap<Integer, HashMap<Integer, MenuItem>>();
    private HashMap<Integer, Group> groups = new HashMap<Integer, Group>();

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

    public HashMap<Integer, HashMap<Integer, MenuItem>> getIndexingMenuForGroups() {
        return indexingMenuForGroups;
    }

    public HashMap<Integer, MenuItem> getCanonicalIndexingMenu() {
        return canonicalIndexingMenu;
    }

    public synchronized Menu resolveMenu(String json) {
        Menu menu = null;
        try {
            menu = gson.fromJson(json, Menu.class);
        } catch (Exception e) {
            log.error("Can't cast json to Menu.class \n" + e.getMessage());
            return menu;
        }
        return this.resolveMenu(menu);
    }

    public synchronized Menu resolveMenu(Menu menu) {
        for (MenuItem item0 : menu.getItems()) {
            if (canonicalIndexingMenu.containsKey(item0.getId())) {
                MenuItem canonicalMenuItem = canonicalIndexingMenu.get(item0.getId());
                item0.setName(canonicalMenuItem .getName());
                item0.setPosition(canonicalMenuItem.getPosition());
                item0.setUrl(canonicalMenuItem .getUrl());
                if (item0.getItems() != null) {
                    for (MenuItem item1 : item0.getItems()) {
                        if (canonicalIndexingMenu.containsKey(item1.getId())) {
                            canonicalMenuItem = canonicalIndexingMenu.get(item1.getId());
                            item1.setName(canonicalMenuItem .getName());
                            item1.setPosition(canonicalMenuItem.getPosition());
                            item1.setUrl(canonicalMenuItem .getUrl());
                        }
                    }
                }
            }
        }

        return menu;
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

    private synchronized boolean containGroupName(String name, int excludeId) {
        for (Group g : this.getGroups().values()) {
            if ((g.getName().equals(name)) && (excludeId != g.getId())) {
                return true;
            }
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean saveGroup(Group group) {
        if (this.containGroupName(group.getName(),-1)) {
            log.error("Can't save group. Duplicate group name. Group with name - '" + group.getName() + "' already exists");
            return false;
        }
        if (!this.saveGroupToDB(group)) return false;

        this.groups.put(group.getId(), group);
        Menu menu = this.createMenuForGroup(group);
        this.menuForGroups.put(group.getId(), menu);
        this.indexingMenuForGroups.put(group.getId(), this.indexingMenu(menu.getItems(), new HashMap<Integer, MenuItem>()));
        log.info("Group '" + group.getName() + "' [" + group.getId() + "] has been added");
        return true;
    }

    private synchronized boolean saveGroupToDB(Group group) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't save group - '" + group.getName() + "' \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean updateGroup(Group group) throws Exception {
        group = this.getGroup(group.getId());
        if (group == null) return false;
        if (this.containGroupName(group.getName(), group.getId())) return false;
        this.getGroups().put(group.getId(), group);
        this.updateGroupToDB(group);
        return true;
    }

    private synchronized void updateGroupToDB(Group group) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(group);
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

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean deleteGroup(int id) {
        Group group = this.getGroup(id);
        return group != null && this.deleteGroup(group);
    }

    public synchronized boolean deleteGroup(Group group) {
        if (group == null) return false;
        try {
            this.getGroups().remove(group.getId());
        } catch (Exception e) {
            log.error("Can't delete group " + group.getName() + " [" + group.getId() + "] \n" + e.getMessage());
            return false;
        }

        this.menuForGroups.remove(group.getId());
        this.indexingMenuForGroups.remove(group.getId());

        return this.deleteGroupFromDB(group);
    }

    private synchronized boolean deleteGroupFromDB(Group group) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(group);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't delete group from DB '" + group.getName() + "' [" + group.getId() + "] \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------
    public synchronized Group getGroup(int id) {
        Group group = this.groups.get(id);
        return group == null ? this.getGroupFromDB(id) : group;
    }

    private synchronized Group getGroupFromDB(int id) {
        Criteria crt_groups = this.getSession().createCriteria(Group.class);
        crt_groups.add(Restrictions.eq("id", id));
        return crt_groups.list().size() == 1 ? (Group)crt_groups.list().get(0) : null;
    }
    //----------------------------------------------------------------------------------------------------

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
