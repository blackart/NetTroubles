package ru.blackart.dsi.infopanel.commands.security.users;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Users;

import java.util.ArrayList;

public class EditUser extends AbstractCommand {
    @Override
    public String execute() throws Exception {

        String id = this.getRequest().getParameter("id");
        String login = this.getRequest().getParameter("login");
        String passwd = this.getRequest().getParameter("passwd");
        String name = this.getRequest().getParameter("name");
        String group_id = this.getRequest().getParameter("group");
        String block = this.getRequest().getParameter("block");

        if (id !=null) {

            Session session = SessionFactorySingle.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria crt_user = session.createCriteria(Users.class);
            crt_user.add(Restrictions.eq("id", Integer.valueOf(id)));
            Users user = (Users)crt_user.list().get(0);

            Criteria crt_trouble = session.createCriteria(Group.class);
            crt_trouble.add(Restrictions.eq("id", Integer.valueOf(group_id)));
            Group group = (Group)crt_trouble.list().get(0);

            user.setLogin(login);
            user.setPasswd(passwd);
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
        }

        return null;
    }
}
