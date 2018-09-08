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
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.beans.UserSettings;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import javax.servlet.ServletConfig;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AccessService {
    private static AccessService AccessService;
    private Session session;
    private Gson gson = new Gson();
    private Menu canonicalMenu;
    private HashMap<Integer, MenuItem> canonicalIndexingMenu = new HashMap<Integer, MenuItem>();
    private ArrayList<String> noLoginCmd;

    private HashMap<Integer, Menu> menuForGroups = new HashMap<Integer, Menu>();
    private HashMap<Integer, HashMap<Integer, MenuItem>> indexingMenuForGroups = new HashMap<Integer, HashMap<Integer, MenuItem>>();
    private HashMap<Integer, Group> groups = new HashMap<Integer, Group>();

    private HashMap<Integer, User> users = new HashMap<Integer, User>();
    private HashMap<String, User> usersForLogin = new HashMap<String, User>();

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private synchronized Session getSession() {
        return session;
    }

    public HashMap<Integer, Menu> getMenuForGroups() {
        return this.menuForGroups;
    }

    public HashMap<Integer, Group> getGroups() {
        return this.groups;
    }

    public HashMap<Integer, User> getUsers() {
        return this.users;
    }

    public Menu getCanonicalMenu() {
        return this.canonicalMenu;
    }

    public HashMap<Integer, HashMap<Integer, MenuItem>> getIndexingMenuForGroups() {
        return this.indexingMenuForGroups;
    }

    public HashMap<Integer, MenuItem> getCanonicalIndexingMenu() {
        return this.canonicalIndexingMenu;
    }

    public ArrayList<String> getNoLoginCmd() {
        return noLoginCmd;
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

        InputStream menuConfigStream = config.getServletContext().getResourceAsStream(paths.getProperty("menuConfig"));
        InputStreamReader menuConfigStreamReader = new InputStreamReader(menuConfigStream);
        this.canonicalMenu = this.gson.fromJson(menuConfigStreamReader, Menu.class);

        InputStream noLoginCmdStream = config.getServletContext().getResourceAsStream(paths.getProperty("no-login-cmd"));
        InputStreamReader noLoginCmdStreamReader = new InputStreamReader(noLoginCmdStream);
        this.noLoginCmd = (ArrayList<String>)this.gson.fromJson(noLoginCmdStreamReader, ArrayList.class);

        List<User> userList = this.getUsersFromDB();
        for (User u : userList) {
            this.users.put(u.getId(), u);
            this.usersForLogin.put(u.getLogin(), u);
        }

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

    public synchronized boolean containGroupName(String name, int excludeId) {
        for (Group g : this.getGroups().values()) {
            if ((g.getName().equals(name)) && (excludeId != g.getId())) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean containUserLogin(String login, int excludeId) {
        for (User g : this.users.values()) {
            if ((g.getLogin().equals(login)) && (excludeId != g.getId())) {
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
        if (this.containGroupName(group.getName(), group.getId())) {
            log.error("Can't save group. Duplicate group name. Group with name - '" + group.getName() + "' already exists");
            Group groupFromDB = this.getGroupFromDB(group.getId());
            this.groups.put(group.getId(), groupFromDB);
            log.info("Revert name for group - '" + groupFromDB.getName() + "'[" + groupFromDB.getId() + "]");
            return false;
        }
        if (!this.updateGroupToDB(group)) {
            Group groupFromDB = this.getGroupFromDB(group.getId());
            this.groups.put(group.getId(), groupFromDB);
            return false;
        }

        this.groups.put(group.getId(), group);
        Menu menu = this.createMenuForGroup(group);
        this.menuForGroups.put(group.getId(), menu);
        this.indexingMenuForGroups.put(group.getId(), this.indexingMenu(menu.getItems(), new HashMap<Integer, MenuItem>()));

        log.info("Group '" + group.getName() + "' [" + group.getId() + "] has been changed");
        return true;
    }

    private synchronized boolean updateGroupToDB(Group group) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(group);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't update group - '" + group.getName() + "' \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean deleteGroup(int id) {
        Group group = this.getGroup(id);
        return group != null && this.deleteGroup(group);
    }

    public synchronized boolean deleteGroup(Group group) {
        if (group == null) return false;
        if (!this.deleteGroupFromDB(group)) return false;
        try {
            this.getGroups().remove(group.getId());
        } catch (Exception e) {
            log.error("Can't delete group " + group.getName() + " [" + group.getId() + "] \n" + e.getMessage());
            return false;
        }

        this.menuForGroups.remove(group.getId());
        this.indexingMenuForGroups.remove(group.getId());
        log.info("Group '" + group.getName() + "' [" + group.getId() + "] has been deleted");
        return true;
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

    private List<Group> getGroupsFromDB() {
        Criteria crt_trouble = this.getSession().createCriteria(Group.class);
        return (List<Group>)crt_trouble.list();
    }

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

    public synchronized boolean updateUser(User user) {
        user = this.getUser(user.getId());
        if (user == null) return false;
        if (this.containUserLogin(user.getLogin(), user.getId())) {
            log.error("Can't save user. Duplicate user name. User with login name - '" + user.getLogin() + "' already exists");
            User userFromDB = this.getUserFromDB(user.getId());
            this.users.put(user.getId(), userFromDB);
            log.info("Revert name for user - '" + userFromDB.getLogin() + "'[" + userFromDB.getId() + "]");
            return false;
        }
        if (!this.updateUserToDB(user)) {
            User userFromDB = this.getUserFromDB(user.getId());
            this.users.put(user.getId(), userFromDB);
            return false;
        }

        this.users.put(user.getId(), user);
        log.info("User '" + user.getLogin() + "' [" + user.getId() + "] has been changed");
        return true;
    }

    private synchronized boolean updateUserToDB(User user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't update user to DB '" + user.getLogin() + "' [" + user.getId() + "] \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean saveUser(User user) {
        if (this.containUserLogin(user.getLogin(), -1)) {
            log.error("Can't save user. Duplicate user login. User with login name - '" + user.getLogin() + "' already exists");
            return false;
        }
        if (!this.saveUserToDB(user)) return false;

        this.users.put(user.getId(), user);
        log.info("User with login name '" + user.getLogin() + "' [" + user.getId() + "] has been added");
        return true;
    }

    private synchronized boolean saveUserToDB(User user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't save user to DB '" + user.getLogin() + "' [" + user.getId() + "] \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean deleteUser(int id) {
        User user = this.getUser(id);
        return this.deleteUser(user);
    }

    public synchronized boolean deleteUser(User user) {
        if (user == null) return false;
        if (!this.deleteUserFromDB(user)) return false;
        try {
            this.users.remove(user.getId());
        } catch (Exception e) {
            log.error("Can't delete user " + user.getLogin() + " [" + user.getId() + "] \n" + e.getMessage());
            return false;
        }

        log.info("User '" + user.getLogin() + "' [" + user.getId() + "] has been deleted");
        return true;
    }

    private synchronized boolean deleteUserFromDB(User user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't delete user from DB '" + user.getLogin() + "' [" + user.getId() + "] \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized User getUser(String name) {
        User user = this.usersForLogin.get(name);
        return user == null ? this.getUserFromDB(name) : user;
    }

    public synchronized User getUser(int id) {
        User user = this.users.get(id);
        return user == null ? this.getUserFromDB(id) : user;
    }

    private synchronized User getUserFromDB(int id) {
        Criteria crt_user = this.getSession().createCriteria(User.class);
        crt_user.add(Restrictions.eq("id", id));
        return crt_user.list().size() == 1 ? (User)crt_user.list().get(0) : null;
    }

    private synchronized User getUserFromDB(String login) {
        Criteria crt_user = this.getSession().createCriteria(User.class);
        crt_user.add(Restrictions.eq("login", login));
        return crt_user.list().size() == 1 ? (User)crt_user.list().get(0) : null;
    }

    private List<User> getUsersFromDB() {
        Criteria crt_trouble = this.getSession().createCriteria(User.class);
        return (List<User>)crt_trouble.list();
    }

    //----------------------------------------------------------------------------------------------------

    public synchronized boolean saveUserSettingsToDB(UserSettings userSettings) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(userSettings);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            log.error("Can't save userSettings to DB [" + userSettings.getId() + "] \n" + e.getMessage());
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
            return false;
        }
        return true;
    }


}
