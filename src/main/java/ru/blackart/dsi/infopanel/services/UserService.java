package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.Users;

public class UserService {
    private static UserService UserService;
    private Session session;

    public synchronized Session getSession() {
        return session;
    }

    public static UserService getInstance() {
        if (UserService == null) {
            UserService = new UserService();
            UserService.session = SessionFactorySingle.getSessionFactory().openSession();
        }

        return UserService;
    }

    public Users get(int id) {
        Criteria crt_trouble = this.getSession().createCriteria(Users.class);
        crt_trouble.add(Restrictions.eq("id", id));
        return (Users)crt_trouble.list().get(0);
    }

    public Users get(String login) {
        Criteria crt_trouble = this.getSession().createCriteria(Users.class);
        crt_trouble.add(Restrictions.eq("login", login));
        return (Users)crt_trouble.list().get(0);
    }
}
