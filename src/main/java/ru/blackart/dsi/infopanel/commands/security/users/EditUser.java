package ru.blackart.dsi.infopanel.commands.security.users;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Users;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;

import java.util.ArrayList;

public class EditUser extends AbstractCommand {
    AccessService accessService = AccessService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {


        String id = this.getRequest().getParameter("id");
        String login = this.getRequest().getParameter("login");
        String passwd = this.getRequest().getParameter("passwd");
        String name = this.getRequest().getParameter("name");
        String group_id = this.getRequest().getParameter("group");
        String block = this.getRequest().getParameter("block");

        int group_int_id;
        try {
            group_int_id = Integer.valueOf(group_id);
        } catch (Exception e) {
            log.error("Can't cast id " + group_id + " to Integer type \n" + e.getMessage());
            return null;
        }

        Group group = accessService.getGroup(group_int_id);

        Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria crt_user = session.createCriteria(Users.class);
        crt_user.add(Restrictions.eq("id", Integer.valueOf(id)));
        Users user = (Users) crt_user.list().get(0);


        user.setLogin(login);
        if (!passwd.trim().equals("")) user.setPasswd(passwd);
        user.setFio(name);
        user.setGroup_id(group);
        user.setBlock(Boolean.valueOf(block));

        session.save(user);
        session.getTransaction().commit();

        Criteria crt_5 = session.createCriteria(Users.class);
        ArrayList<Users> users = new ArrayList<Users>(crt_5.list());
        this.getConfig().getServletContext().setAttribute("users", users);

        session.flush();
        session.close();

        return null;
    }
}
