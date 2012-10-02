package ru.blackart.dsi.infopanel.controllers;

import net.sf.cglib.core.Block;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Hostgroup;
import ru.blackart.dsi.infopanel.tasksSystem.TaskQueueController;
import ru.blackart.dsi.infopanel.temp.thread.ManagerQueueRequests;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;
import ru.blackart.dsi.infopanel.utils.*;
import ru.blackart.dsi.infopanel.utils.snmpc.handlers.DownTrapsHandler;
import ru.blackart.dsi.infopanel.utils.snmpc.handlers.UpTrapsHandler;
import ru.blackart.dsi.infopanel.utils.snmpc.services.ManagerUpDevcListCleaningThread;
import ru.blackart.dsi.infopanel.utils.snmpc.transport.RequestDataObject;
import ru.blackart.dsi.infopanel.utils.snmpc.storage.Storage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.*;

public class SNMPcRequestController extends HttpServlet {
    private Properties settingsMonitoringPanel;
    private GenerateMonitoringPanel generateMonitoringPanel;
    private Storage storage;
    private ThreadPoolExecutor threadPoolExecutor = TaskQueueController.getInstance().getThreadPoolExecutor();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //инициализация сервлета
        super.init(config);

        //пути к файлу с коммандами, настройкам логирования, и остальным конфигурационным файлам
        String path = config.getInitParameter("pathToDataFile");
        InputStream is = config.getServletContext().getResourceAsStream(path);
        Properties path_to_data_file = new Properties();
        try {
            path_to_data_file.load(is);
        } catch (IOException e) {

        }
        //сохраняем в атрибуты сервлета
        config.getServletContext().setAttribute("pathToDataFile", path_to_data_file);

        //считываем настройки из конфигурационного файла
        //системный пароль, параметр обучения программы и интервал падения узла
        String path_to_settings = config.getInitParameter("settings");
        InputStream is_path_to_settings = config.getServletContext().getResourceAsStream(path_to_settings);
        Properties settings = new Properties();
        try {
            settings.load(is_path_to_settings);
        } catch (IOException e) {

        }

        //пути к панели мониторинга
        String settingsMonitoringPanel = path_to_data_file.getProperty("generateMonitoringPanel");
        InputStream s = config.getServletContext().getResourceAsStream(settingsMonitoringPanel);
        this.settingsMonitoringPanel = new Properties();
        try {
            this.settingsMonitoringPanel.load(s);
        } catch (IOException e) {

        }

        ManagerMainDeviceFilter.getInstance();

        storage = new Storage(Boolean.valueOf((String) settings.get("learning")), Integer.valueOf((String) settings.get("trueDownInterval")));

        ManagerQueueRequests.getInstance().start();
        ManagerUpDevcListCleaningThread managerUpDevcListCleaningThread = ManagerUpDevcListCleaningThread.getInstance();
        managerUpDevcListCleaningThread.setStorage(this.storage);
        managerUpDevcListCleaningThread.startCleaningThread();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        ManagerMainDeviceFilter managerDeviceFilter = ManagerMainDeviceFilter.getInstance();
        RequestDataObject requestDataObject = new RequestDataObject(request.getParameter("poolling"), request.getParameter("device"), request.getParameter("date"), request.getParameter("time"), request.getParameter("group"), request.getParameter("desc"));

        Device inputDevice = new Device();

        Hostgroup inputDeviceHostgroup = new Hostgroup();
        inputDeviceHostgroup.setNum(Integer.valueOf(requestDataObject.getGroup()));

        inputDevice.setName(requestDataObject.getDevice());
        inputDevice.setHostgroup(inputDeviceHostgroup);

        if (managerDeviceFilter.filterInputDevice(inputDevice)) {
            if (requestDataObject.getPoolling().equals("down")) {
                Thread exDownThread = new DownTrapsHandler(requestDataObject, this.storage, this.threadPoolExecutor);
                this.threadPoolExecutor.execute(exDownThread);
            } else if (requestDataObject.getPoolling().equals("up")) {
                Thread exUpThread = new UpTrapsHandler(requestDataObject, this.storage, this.threadPoolExecutor);
                this.threadPoolExecutor.execute(exUpThread);
            }
        }

        System.out.println("All task - " + threadPoolExecutor.getTaskCount() + " completed task - " + threadPoolExecutor.getCompletedTaskCount());
    }
}
