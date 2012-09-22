package ru.blackart.dsi.infopanel.commands;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;

public abstract class AbstractCommand implements Command{

    private HttpServletRequest Request;
    private HttpServletResponse Response;
    private ServletContext servletContext;
    private ServletConfig config;
    private HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(boolean set) {
        this.session = this.getRequest().getSession(set);
    }

    public ServletConfig getConfig() {
        return config;
    }

    public void setConfig(ServletConfig config) {
        this.config = config;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public HttpServletRequest getRequest() {
        return Request;
    }

    public void setRequest(HttpServletRequest Request) {
        this.Request = Request;
    }

    public HttpServletResponse getResponse() {
        return Response;
    }

    public void setResponse(HttpServletResponse Response) {
        this.Response = Response;
    }

    abstract public String execute() throws Exception;
}
