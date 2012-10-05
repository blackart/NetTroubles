package ru.blackart.dsi.infopanel.commands.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.Users;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;

import javax.servlet.http.HttpSession;


public class Logout extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public String execute() throws Exception {
        this.setSession(true);
        HttpSession session = this.getSession();
        session.setAttribute("login", false);
        session.setAttribute("page", "login");
        try {
            Users user = (Users)session.getAttribute("info");
            log.info("User - " + user.getLogin() + " [" + user.getFio() + "] logged out.");
        } catch (NullPointerException exeption) {
            log.error("Unknown user try execute logged out command");
        }
        return "/login";
    }
}
