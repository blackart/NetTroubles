package ru.blackart.dsi.infopanel.commands.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.CompleteStatusMessage;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class CheckUser extends AbstractCommand {
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

        String login = getRequest().getParameter("login");
        String passwd = getRequest().getParameter("passwd");

        synchronized (accessService) {
            User user = accessService.getUser(login);

            CompleteStatusMessage completeStatusMessage = new CompleteStatusMessage();

            if (user == null) {
                if (login.equals("system")) {
                    //считываем настройки из конфигурационного файла
                    String path_to_settings = this.getConfig().getInitParameter("settings");
                    InputStream is_path_to_settings = this.getConfig().getServletContext().getResourceAsStream(path_to_settings);
                    Properties settings = new Properties();
                    try {
                        settings.load(is_path_to_settings);
                    } catch (IOException e) {
                        log.error("Can't load system settings file. \n" + e.getMessage());
                    }

                    if (passwd.equals(settings.getProperty("system_password"))) {

                        answer = "Welcome, Overlord!";

                        completeStatusMessage.setMessage(answer);
                        completeStatusMessage.setStatus(true);
                        this.getResponse().getWriter().print(completeStatusMessage.toJson());

                        return null;
                    } else {
                        answer = "WRONG PASSWORD FOR SYSTEM USER!!! ATTENTION!!! DETECTED ATTACK OF HACKERS!!! WE ALL ARE GONNA DIE!!!";
                        completeStatusMessage.setMessage(answer);
                        completeStatusMessage.setStatus(false);
                        this.getResponse().getWriter().print(completeStatusMessage.toJson());

                        return null;
                    }
                } else {
                    answer = "Account don't exist";
                    completeStatusMessage.setMessage(answer);
                    completeStatusMessage.setStatus(false);
                    this.getResponse().getWriter().print(completeStatusMessage.toJson());

                    return null;
                }
            } else if (!passwd.equals(user.getPasswd())) {
                if (user.getLogin().equals("system")) {
                    answer = "WRONG PASSWORD FOR SYSTEM USER!!! ATTENTION!!! DETECTED ATTACK OF HACKERS!!! WE ALL ARE GONNA DIE!!!";
                } else {
                    answer = "Wrong password";
                }
                completeStatusMessage.setMessage(answer);
                completeStatusMessage.setStatus(false);
                this.getResponse().getWriter().print(completeStatusMessage.toJson());

                return null;
            } else {
                if (user.getBlock()) {

                    answer = "Account blocked";
                    completeStatusMessage.setMessage(answer);
                    completeStatusMessage.setStatus(true);
                    this.getResponse().getWriter().print(completeStatusMessage.toJson());

                    return null;
                }

                if (user.getLogin().equals("system") ) {
                    answer = "Welcome, Overlord!";
                } else {
                    answer = "Waiting for login ...";
                }
                completeStatusMessage.setMessage(answer);
                completeStatusMessage.setStatus(true);
                this.getResponse().getWriter().print(completeStatusMessage.toJson());

                return null;
            }
        }
    }
}
