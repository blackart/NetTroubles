package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
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

    public synchronized void update(Users user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
    }

    public synchronized void save(Users user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
    }

    public synchronized void delete(Users user) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.flush();
            session.close();
            this.session = SessionFactorySingle.getSessionFactory().openSession();
        }
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
