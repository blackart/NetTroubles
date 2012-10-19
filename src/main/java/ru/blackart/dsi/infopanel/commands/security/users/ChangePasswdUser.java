package ru.blackart.dsi.infopanel.commands.security.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;

public class ChangePasswdUser extends AbstractCommand {
    private final AccessService accessService = AccessService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {
        String id = this.getRequest().getParameter("id");
        String passwd = this.getRequest().getParameter("passwd");

        int user_id;
        try {
            user_id = Integer.valueOf(id);
        } catch (Exception e) {
            log.error("Can't cast id " + id + " to Integer type \n" + e.getMessage());
            return null;
        }

        User user = accessService.getUser(user_id);
        user.setPasswd(passwd);
        accessService.updateUser(user);

        if ((this.getSession().getAttribute("login") != null) && ((Boolean) this.getSession().getAttribute("login"))) {
//            this.getSession().setAttribute("info", user);
            this.getSession().setAttribute("change_passwd", false);
        }

        return null;
    }
}
