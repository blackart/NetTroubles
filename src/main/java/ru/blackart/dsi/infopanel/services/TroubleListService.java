package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;

import java.util.List;

public class TroubleListService {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static TroubleListService troubleListService;
    private Session session;

    private synchronized Session getSession() {
        return session;
    }

    public static synchronized TroubleListService getInstance() {
        if (troubleListService == null) {
            troubleListService = new TroubleListService();
            troubleListService.session = SessionFactorySingle.getSessionFactory().openSession();
        }

        return troubleListService;
    }

    public synchronized void update(TroubleList troubleList) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(troubleList);
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

    public synchronized void save(TroubleList troubleList) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(troubleList);
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

    public synchronized void delete(TroubleList troubleList) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(troubleList);
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

    public synchronized TroubleList get(int id) {
        Criteria crt = this.getSession().createCriteria(TroubleList.class);
        crt.add(Restrictions.eq("id", id));
        return (TroubleList) crt.list().get(0);
    }

    public synchronized List<TroubleList> getAll() {
        return (List<TroubleList>) this.getSession().createCriteria(TroubleList.class).list();
    }
}
