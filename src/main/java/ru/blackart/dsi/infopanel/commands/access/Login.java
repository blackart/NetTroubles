package ru.blackart.dsi.infopanel.commands.access;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.access.AccessMenuForGroup;
import ru.blackart.dsi.infopanel.access.AccessUserObject;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Tab;
import ru.blackart.dsi.infopanel.beans.UserSettings;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Login extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    //Статический метод, заполняет параметры в сесси исходными значениями
    public static void start(HttpSession session) throws SQLException {
        session.setAttribute("info", null);
        session.setAttribute("login", false);
        session.setAttribute("page", null);
        session.setAttribute("access", null);
        session.setAttribute("change_passwd", false);
    }

    @Override
    public String execute() throws Exception {
        String answer = "";
        Session session = SessionFactorySingle.getSessionFactory().openSession();

        session.beginTransaction();
        Criteria criteria = session.createCriteria(User.class);

        String login = getRequest().getParameter("login");
        String passwd = getRequest().getParameter("passwd");

        this.log.info("User - " + login + " trying to login");

        criteria.add(Restrictions.eq("login", login)).add(Restrictions.eq("passwd", passwd));
        List<User> usersList = criteria.list();

        session.getTransaction().commit();
        session.flush();
        session.close();

        this.setSession(true);
        this.log.info("User - " + login + " start HTTP session");

        //считываем настройки из конфигурационного фала
        String path_to_settings = this.getConfig().getInitParameter("settings");
        InputStream is_path_to_settings = this.getConfig().getServletContext().getResourceAsStream(path_to_settings);
        Properties settings = new Properties();
        try {
            settings.load(is_path_to_settings);
        } catch (IOException e) {

        }

        if ((login.equals("system")) && (passwd.equals(settings.getProperty("system_password")))) {
            this.log.info("User - " + login + " is GOD!!!");

            User user = null;
            if (usersList.size() == 1) {
                user = usersList.get(0);
            } else if (usersList.size() == 0) {
                user = new User();
                user.setBlock(false);
                user.setFio(login);
                user.setPasswd(passwd);
                user.setLogin("system");

                UserSettings userSettings = new UserSettings();
                userSettings.setOpenControlPanel(false);
                userSettings.setCurrentTroublesPageReload(true);
                userSettings.setTimeoutReload("1200000");
                user.setSettings_id(userSettings);

                session = SessionFactorySingle.getSessionFactory().openSession();

                Group group = null;
                Criteria crt_group = session.createCriteria(Group.class);
                if (crt_group.list().size() > 0) {
                    group = (Group)crt_group.list().get(0);
                } else {
                    group = new Group();
                    group.setName("system");
                }

                //todo брать не из базы а из properties файла, резервная система в отсутствие записей в DB

                Criteria crt_4 = session.createCriteria(Tab.class);
                ArrayList<Tab> tabs = new ArrayList<Tab>(crt_4.list());

                session.beginTransaction();
                group.setTabs(tabs);
                user.setGroup_id(group);
                session.save(group);
                session.getTransaction().commit();

                session.beginTransaction();
                session.save(userSettings);
                session.getTransaction().commit();

                session.beginTransaction();
                session.save(user);
                session.getTransaction().commit();

                //обновление объектов в памяти (ServletContext)

                Criteria crt_5 = session.createCriteria(User.class);
                ArrayList<User> users = new ArrayList<User>(crt_5.list());
                this.getConfig().getServletContext().setAttribute("users", users);

                Criteria crt_6 = session.createCriteria(Tab.class);
                ArrayList<Tab> all_tabs = new ArrayList<Tab>(crt_6.list());
                getConfig().getServletContext().setAttribute("tabs", all_tabs);

                Criteria crt_7 = session.createCriteria(Group.class);
                ArrayList<Group> groups = new ArrayList<Group>(crt_7.list());
                ArrayList<AccessMenuForGroup> tabs_of_groups = new ArrayList<AccessMenuForGroup>();
                for (Group g : groups) {
                    g.setTabs(new ArrayList<Tab>(g.getTabs()));
                    tabs_of_groups.add(new AccessMenuForGroup(g,all_tabs));
                }
                getConfig().getServletContext().setAttribute("tabs_of_groups", tabs_of_groups);

                getConfig().getServletContext().setAttribute("groups", groups);

                session.flush();
                session.close();
            }

            if (user != null) {
                this.getSession().setAttribute("info", user);

                this.getSession().setAttribute("login", true);  //если true - пользователь прошёл аутентификацию, false - нет
                AccessUserObject accessUserObject = new AccessUserObject(user);

                String path = "admin";

                this.getSession().setAttribute("access", accessUserObject);
                this.getSession().setAttribute("page", path);
                this.getSession().setAttribute("change_passwd", false);

                answer = "Вход выполнен. Ожидайте...|/admin";
            }
        } else if (usersList.size() == 1) {
            User user = usersList.get(0);

            this.log.info("User - " + user.getLogin() + " FIO - " + user.getFio());

            this.getSession().setAttribute("info", user);
            String path = "index";

            if (!user.getBlock()) {
                this.getSession().setAttribute("login", true);  //если true - пользователь прошёл аутентификацию, false - нет
                this.log.info("User - " + user.getLogin() + " login - " + this.getSession().getAttribute("login"));

                AccessUserObject accessUserObject = new AccessUserObject(user);

                path = "admin";

                this.getSession().setAttribute("access", accessUserObject);
                this.getSession().setAttribute("page", path);
                this.log.info("User - " + user.getLogin() + " page - " + path);

                if (passwd.equals("12345")) {
                    this.getSession().setAttribute("change_passwd", true);
                } else {
                    this.getSession().setAttribute("change_passwd", false);
                }
                this.log.info("User - " + user.getLogin() + " change_passwd - " + this.getSession().getAttribute("change_passwd"));

                answer = "Вход выполнен. Ожидайте...|/admin";
                this.log.info("User - " + user.getLogin() + " answer - " + answer);
            } else {
                this.getSession().setAttribute("login", false);  //если true - пользователь прошёл аутентификацию, false - нет
                this.getSession().setAttribute("page", path);

                answer = "Пользователь заблокирован|/login";
                this.log.info("User - " + user.getLogin() + " login - false, page - " + path + " answer - " + answer);
            }
        } else {
            if (usersList.size() == 0) {
                answer = "Неверное имя пользователя или пароль|/login";
            } else {
                answer = "Несколько пользователей с одинаковый логином|/login";
            }
        }
        this.getResponse().getWriter().print(answer);
        return null;
    }
}
