package ru.blackart.dsi.infopanel.commands.access;

import ru.blackart.dsi.infopanel.access.AccessUserObject;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.view.UserView;

import javax.servlet.http.HttpSession;

public class GetUser extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        HttpSession session = this.getSession();

        Boolean login = (Boolean)session.getAttribute("login");

        if (login) {
            AccessUserObject accessUserObject = (AccessUserObject)session.getAttribute("access");
            User user = (User)session.getAttribute("info");
            Boolean changePassword = (Boolean)session.getAttribute("change_passwd");

            UserView view = new UserView();
            view.setMenu(accessUserObject.getMenu());
            view.setFio(user.getFio());
            view.setBlock(user.getBlock());
            view.setLogin(user.getLogin());
            view.setId(user.getId());
            view.setChangePassword(changePassword);

            this.getResponse().getWriter().print(view.toJSON());
        }

        return null;
    }
}
