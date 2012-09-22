package ru.blackart.dsi.infopanel.commands;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public interface Command {
    public HttpSession getSession();
    public void setSession(boolean set);

    public ServletConfig getConfig();
    public void setConfig(ServletConfig config);

    public ServletContext getServletContext();
    public void setServletContext(ServletContext servletContext);

    public HttpServletRequest getRequest();
    public void setRequest(HttpServletRequest Request);

    public HttpServletResponse getResponse();
    public void setResponse(HttpServletResponse Response);

    public String execute() throws Exception;
}
