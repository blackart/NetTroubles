package ru.blackart.dsi.infopanel.controllers;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.commands.Command;
import ru.blackart.dsi.infopanel.commands.FactoryCommandCommand;
import ru.blackart.dsi.infopanel.commands.access.Login;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.services.DeviceManager;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class HTTPServletController extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private FactoryCommandCommand factory;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        //создаем экземпляр синглтона, устанавливаем в него ссылку на конфиг HTTPSC, чтобы записывать в сервлет
        //контекст инфу из SNMPc контроллера.
        TroubleListsManager.getInstance().setServletConfig(config);

        String path = config.getInitParameter("pathToDataFile");
        InputStream inputStream = config.getServletContext().getResourceAsStream(path);
        Properties paths_to_data_files = new Properties();
        try {
            paths_to_data_files.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.getServletContext().setAttribute("pathToDataFile", paths_to_data_files);

        // подгружаем настройки логера
        inputStream = config.getServletContext().getResourceAsStream(paths_to_data_files.getProperty("pathToLog4jConfig"));
        Properties log4jConfig = new Properties();
        try {
            log4jConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(log4jConfig);

        //инициализируем Фабрику Комманд
        this.factory = FactoryCommandCommand.getInstance(this.getServletConfig());

        //забираем из базы занчения
        Session session = SessionFactorySingle.getSessionFactory().openSession();
        //Services
        Criteria crt_0 = session.createCriteria(Service.class);
        ArrayList<Service> services = new ArrayList<Service>(crt_0.list());
        config.getServletContext().setAttribute("services", services);

        Criteria crt_2 = session.createCriteria(TypeDeviceFilter.class);
        ArrayList<TypeDeviceFilter> typeDeviceFilters = new ArrayList<TypeDeviceFilter>(crt_2.list());
        config.getServletContext().setAttribute("typeDeviceFilters", typeDeviceFilters);

        //Hostgroups
        Criteria crt_6 = session.createCriteria(Hostgroup.class);
        ArrayList<Hostgroup> hostgroups = new ArrayList<Hostgroup>(crt_6.list());
        config.getServletContext().setAttribute("hostgroups", hostgroups);

        //Hoststatus
        Criteria crt_host_status = session.createCriteria(Hoststatus.class);
        ArrayList<Hoststatus> hoststatuses = new ArrayList<Hoststatus>(crt_host_status.list());
        config.getServletContext().setAttribute("hoststatuses", hoststatuses);

        //Region
        Criteria crt_region = session.createCriteria(Region.class);
        ArrayList<Region> regions = new ArrayList<Region>(crt_region.list());
        config.getServletContext().setAttribute("regions", regions);

        session.flush();
        session.close();

        //Devices
        DeviceManager.getInstance();
        AccessService.getInstance();
        DataModel.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        //Проверка начальных параметров сессии пользователя
        if (request.getSession(true).getAttribute("login") == null) {
            try {
                Login.start(request.getSession(true));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String redirect = null;
        if (request.getParameterMap().containsKey("cmd")) {
            try {
                Command cmd = factory.createClass(request,response,getServletContext(),getServletConfig(),request.getParameter("cmd"));
                redirect = cmd.execute();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            redirect = "index.jsp";
        }

        if (redirect != null) {
            response.sendRedirect(redirect);
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        this.doPost(httpServletRequest,httpServletResponse);
    }
}
