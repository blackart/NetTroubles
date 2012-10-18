package ru.blackart.dsi.infopanel.commands.security.users;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.*;

import java.util.List;

public class AddUser extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String login = this.getRequest().getParameter("login");
        String passwd = this.getRequest().getParameter("passwd");
        String name = this.getRequest().getParameter("name");
        String group_id = this.getRequest().getParameter("group");
        String block = this.getRequest().getParameter("block");

        Session session = SessionFactorySingle.getSessionFactory().openSession();

        Criteria crt_trouble = session.createCriteria(Group.class);
        crt_trouble.add(Restrictions.eq("id", Integer.valueOf(group_id)));
        Group group = (Group)crt_trouble.list().get(0);

        User user = new User();
        user.setLogin(login);
        user.setPasswd(passwd);
        user.setFio(name);
        user.setGroup_id(group);
        user.setBlock(Boolean.valueOf(block));

        UserSettings userSettings = new UserSettings();
        userSettings.setOpenControlPanel(false);
        userSettings.setCurrentTroublesPageReload(true);
        userSettings.setTimeoutReload("1200000");
        user.setSettings_id(userSettings);

        session.beginTransaction();
        session.save(userSettings);
        session.getTransaction().commit();

        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();

        session.flush();
        session.close();

        List<User> users = (List<User>) this.getConfig().getServletContext().getAttribute("users");
        users.add(user);

        return null;
    }
}
