package ru.blackart.dsi.infopanel.temp.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.Devcapsule;

import java.util.List;

public class DevcapsuleDAO extends AbstractDao<Devcapsule> {

    public void saveOrUpdate(Devcapsule persistent) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(persistent);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
    }

    public void delete(int id) {
        Criteria criteria = this.getSession().createCriteria(Devcapsule.class);
        criteria.add(Restrictions.eq("id", id));
        delete((Devcapsule) criteria.list().get(0));
    }

    public void delete(Devcapsule persistent) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.delete(persistent);
            session.getTransaction().commit();
            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
    }

    public Devcapsule get(int id) {
        return (Devcapsule) getSession().load(Devcapsule.class, id);
    }

    public List<Devcapsule> getAll() {
        return this.getSession().createCriteria(Devcapsule.class).list();
    }


}
