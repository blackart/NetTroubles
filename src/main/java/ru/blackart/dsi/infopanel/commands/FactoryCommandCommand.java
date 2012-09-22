package ru.blackart.dsi.infopanel.commands;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class FactoryCommandCommand implements FactoryCommandImpl {
    private static FactoryCommandCommand factory;
    private Properties mappings;
    private String cmd;

    private void setMap(ServletConfig config) {
        InputStream s;
        Properties path_to_data_file = (Properties)config.getServletContext().getAttribute("pathToDataFile");

        String cmd = path_to_data_file.getProperty("cmd");
        s = config.getServletContext().getResourceAsStream(cmd);
        mappings = new Properties();
        try {
            mappings.load(s);
        } catch (IOException e) {
//            log.error("Can't load cmd properties, exception: " + e);
        }
    }


    public static FactoryCommandCommand getInstance(ServletConfig config) {
        if (factory == null) {
            factory = new FactoryCommandCommand();
            factory.setMap(config);
        }

        return factory;
    }

    public Command createClass(HttpServletRequest Request, HttpServletResponse Response, ServletContext getServletContext, ServletConfig getServletConfig, String cmd) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String actionClass;
        if (mappings.containsKey(cmd)) {
            actionClass = (String) mappings.get(cmd);
        } else {
            actionClass = "";
        }

        //Если в сессии пользователя записан IP такой же с которого он в данный момент обращается к контоллеру и
        //команда которую он пытается выполнить защищённая (защищённые команды могут выполнять торлько залогиневшиеся пользователи),
        //а пользователь не залогинился, то экземпляр класса-коанды не создаётся
        /*if ((!Request.getRemoteAddr().equals(Request.getSession().getAttribute("IP"))) && ((Request.getSession(true).getAttribute("login_user") == null) || (!(Boolean) Request.getSession(true).getAttribute("login")))) {
            log.error("Can't create new instance class!");
            return null;
        } else {*/
            AbstractCommand CommandClass;

            CommandClass = (AbstractCommand)Class.forName(actionClass).newInstance();
            CommandClass.setRequest(Request);
            CommandClass.setResponse(Response);
            CommandClass.setServletContext(getServletContext);
            CommandClass.setConfig(getServletConfig);
            CommandClass.setSession(true);

            return CommandClass;
        /*}

        return null;*/
    }
}
