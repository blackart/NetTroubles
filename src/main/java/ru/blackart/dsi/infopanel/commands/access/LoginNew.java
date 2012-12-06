package ru.blackart.dsi.infopanel.commands.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.access.AccessUserObject;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class LoginNew extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final AccessService accessService = AccessService.getInstance();

    //Статический метод, заполняет параметры в сесси исходными значениями
    public static void start(HttpSession session) throws SQLException {
        session.setAttribute("info", null);
        session.setAttribute("login", false);
        session.setAttribute("access", null);
        session.setAttribute("change_passwd", false);
    }

    @Override
    public String execute() throws Exception {
        String adminURI = "/admin/current/";

        String login = getRequest().getParameter("login");
        String passwd = getRequest().getParameter("passwd");

        synchronized (accessService) {
            User user = accessService.getUser(login);

            if (user == null) {
                if (login.equals("system")) {
                    //считываем настройки из конфигурационного фала
                    String path_to_settings = this.getConfig().getInitParameter("settings");
                    InputStream is_path_to_settings = this.getConfig().getServletContext().getResourceAsStream(path_to_settings);
                    Properties settings = new Properties();
                    try {
                        settings.load(is_path_to_settings);
                    } catch (IOException e) {
                        log.error("Can't load system settings file. \n" + e.getMessage());
                    }

                    if (passwd.equals(settings.getProperty("system_password"))) {
                        user = new User();
                        user.setBlock(false);
                        user.setFio(login);
                        user.setPasswd(passwd);
                        user.setLogin("system");

                        if (accessService.containGroupName("system", -1)) {
                            for (Group g : accessService.getGroups().values()) {
                                if (g.getName().equals("system")) {
                                    user.setGroup_id(g);
                                    break;
                                }
                            }
                        } else {
                            Group group = new Group();
                            group.setName("system");
                            String menuConfig = accessService.getCanonicalMenu().toJson();
                            group.setMenuConfig(menuConfig);
                            accessService.saveGroup(group);

                            user.setGroup_id(group);
                        }

                        accessService.saveUser(user);

                        this.setSession(true);

                        AccessUserObject accessUserObject = new AccessUserObject(user);

                        this.getSession().setAttribute("info", user);
                        this.getSession().setAttribute("login", true);
                        this.getSession().setAttribute("access", accessUserObject);
                        this.getSession().setAttribute("change_passwd", false);

                        this.getResponse().sendRedirect(adminURI);

                        log.info("SYSTEM user is logged !!! Attention!!! Danger!!!");
                        return null;
                    } else {
                        log.info("WRONG PASSWORD FOR SYSTEM USER!!! ATTENTION!!! DETECTED ATTACK OF HACKERS!!! WE ALL ARE GONNA DIE!!!");
                        return null;
                    }
                } else {
                    log.info("User '" + login + "' don't exist");
                    return null;
                }
            } else if (!passwd.equals(user.getPasswd())) {
                if (user.getLogin().equals("system")) {
                    log.info("WRONG PASSWORD FOR SYSTEM USER!!! ATTENTION!!! DETECTED ATTACK OF HACKERS!!! WE ALL ARE GONNA DIE!!!");
                } else {
                    log.info("User '" + user.getLogin() + "' can't login because password is wrong");
                }
                return null;
            } else {
                this.setSession(true);

                boolean isLogin = (Boolean)this.getSession().getAttribute("login");
                User userOld = (User)this.getSession().getAttribute("info");

                if ((isLogin) && (userOld != null) && (userOld.getLogin().equals(user.getLogin()))) {
                    this.getResponse().sendRedirect(adminURI);
                    return null;
                }

                this.getSession().setAttribute("info", user);

                if (user.getBlock()) {
                    this.getSession().setAttribute("login", false);
                    log.info("User '" + user.getLogin() + "' can't login because it's blocked");
                    return null;
                }

                AccessUserObject accessUserObject = new AccessUserObject(user);
                this.getSession().setAttribute("login", true);
                this.getSession().setAttribute("access", accessUserObject);

                if (passwd.equals("")) {
                    this.getSession().setAttribute("change_passwd", true);
                } else {
                    this.getSession().setAttribute("change_passwd", false);
                }

                this.getResponse().sendRedirect(adminURI);

                if (user.getLogin().equals("system") ) {
                    log.info("SYSTEM user is logged !!! Attention!!! Danger!!!");
                } else {
                    log.info("User '" + user.getLogin() + "' is logged");
                }
                return null;
            }
        }
    }
}
