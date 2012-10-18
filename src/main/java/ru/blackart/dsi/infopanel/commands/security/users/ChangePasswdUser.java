package ru.blackart.dsi.infopanel.commands.security.users;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;

import java.util.ArrayList;

public class ChangePasswdUser extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String id = this.getRequest().getParameter("id");
        String passwd = this.getRequest().getParameter("passwd");

        if (id !=null) {

            Session session = SessionFactorySingle.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria crt_user = session.createCriteria(User.class);
            crt_user.add(Restrictions.eq("id", Integer.valueOf(id)));
            User user = (User)crt_user.list().get(0);

            user.setPasswd(passwd);

            session.update(user);
            session.getTransaction().commit();

            Criteria crt_5 = session.createCriteria(User.class);
            ArrayList<User> users = new ArrayList<User>(crt_5.list());
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
