package ru.blackart.dsi.infopanel.commands.security.users;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Users;

import java.util.ArrayList;

public class ChangePasswdUser extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String id = this.getRequest().getParameter("id");
        String passwd = this.getRequest().getParameter("passwd");

        if (id !=null) {

            Session session = SessionFactorySingle.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria crt_user = session.createCriteria(Users.class);
            crt_user.add(Restrictions.eq("id", Integer.valueOf(id)));
            Users user = (Users)crt_user.list().get(0);

            user.setPasswd(passwd);

            session.update(user);
            session.getTransaction().commit();

            Criteria crt_5 = session.createCriteria(Users.class);
            ArrayList<Users> users = new ArrayList<Users>(crt_5.list());
            this.getConfig().getServletContext().setAttribute("users", users);

            session.getTransaction().commit();
            session.flush();
            session.close();

            if ((this.getSession().getAttribute("login") != null) && ((Boolean)this.getSession().getAttribute("login"))) {
                this.getSession().setAttribute("info", user);
                this.getSession().setAttribute("change_passwd", false);
            }

        }
        return null;
    }
}
