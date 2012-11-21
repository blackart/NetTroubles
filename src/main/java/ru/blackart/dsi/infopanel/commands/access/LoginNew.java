package ru.blackart.dsi.infopanel.commands.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.access.AccessUserObject;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.beans.UserSettings;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.CompleteStatusMessage;

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
        session.setAttribute("page", null);
        session.setAttribute("access", null);
        session.setAttribute("change_passwd", false);
    }

    @Override
    public String execute() throws Exception {
        String answer = "";
//        String adminURI = "/net/admin/admin.html";
        String adminURI = "/admin";

        String login = getRequest().getParameter("login");
        String passwd = getRequest().getParameter("passwd");

        synchronized (accessService) {
            User user = accessService.getUser(login);

            CompleteStatusMessage completeStatusMessage = new CompleteStatusMessage();

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
                        UserSettings userSettings = new UserSettings();
                        userSettings.setOpenControlPanel(false);
                        userSettings.setCurrentTroublesPageReload(true);
                        userSettings.setTimeoutReload("1200000");

                        accessService.saveUserSettingsToDB(userSettings);

                        user = new User();
                        user.setBlock(false);
                        user.setFio(login);
                        user.setPasswd(passwd);
                        user.setLogin("system");
                        user.setSettings_id(userSettings);

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
                        this.getSession().setAttribute("page", "admin");
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
                this.getSession().setAttribute("info", user);

                if (user.getBlock()) {
                    this.getSession().setAttribute("login", false);
                    this.getSession().setAttribute("page", "index");
                    log.info("User '" + user.getLogin() + "' can't login because it's blocked");
                    return null;
                }

                AccessUserObject accessUserObject = new AccessUserObject(user);
                this.getSession().setAttribute("login", true);
                this.getSession().setAttribute("access", accessUserObject);
                this.getSession().setAttribute("page", "admin");

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
