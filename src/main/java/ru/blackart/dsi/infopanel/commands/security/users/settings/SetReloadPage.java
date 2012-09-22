package ru.blackart.dsi.infopanel.commands.security.users.settings;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.UserSettings;
import ru.blackart.dsi.infopanel.beans.Users;

public class SetReloadPage extends AbstractCommand {

    @Override
    public String execute() throws Exception {
        String value = this.getRequest().getParameter("value");
        Users user = (Users)this.getSession().getAttribute("info");

        Session session = SessionFactorySingle.getSessionFactory().openSession();

        Criteria crt_user_settings = session.createCriteria(UserSettings.class);
        crt_user_settings.add(Restrictions.eq("id", user.getSettings_id().getId()));
        UserSettings userSettings = (UserSettings)crt_user_settings.list().get(0);

        userSettings.setCurrentTroublesPageReload(Boolean.valueOf(value));

        user.setSettings_id(userSettings);

        session.beginTransaction();
        session.save(userSettings);
        session.getTransaction().commit();

        session.flush();
        session.close();

        return null;
    }
}
