package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;

public class TroubleService {
    private static TroubleService troubleService;
    private Session session;

    public synchronized Session getSession() {
        return session;
    }

    public static TroubleService getInstance() {
        if (troubleService == null) {
            troubleService = new TroubleService();
            troubleService.session = SessionFactorySingle.getSessionFactory().openSession();
        }

        return troubleService;
    }

    public synchronized void update(Trouble trouble) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(trouble);
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

    public synchronized void save(Trouble trouble) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(trouble);
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

    public synchronized void delete(Trouble trouble) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(trouble);
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

    public Trouble get(int id) {
        Criteria crt_trouble = this.getSession().createCriteria(Trouble.class);
        crt_trouble.add(Restrictions.eq("id", id));
        return (Trouble) crt_trouble.list().get(0);
    }

}
