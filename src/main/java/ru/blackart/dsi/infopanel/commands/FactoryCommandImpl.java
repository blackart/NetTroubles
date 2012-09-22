package ru.blackart.dsi.infopanel.commands;

import ru.blackart.dsi.infopanel.commands.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;

public interface FactoryCommandImpl {
    public Command createClass(HttpServletRequest Request,
                               HttpServletResponse Response,
                               ServletContext getServletContext,
                               ServletConfig getServletConfig, String cmd)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException;
}