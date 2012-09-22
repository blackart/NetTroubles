package ru.blackart.dsi.infopanel.commands.access;

import ru.blackart.dsi.infopanel.commands.AbstractCommand;


public class Logout extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        this.setSession(true);
        this.getSession().setAttribute("login", false);
        this.getSession().setAttribute("page", "login");

        return "/login";
    }
}
