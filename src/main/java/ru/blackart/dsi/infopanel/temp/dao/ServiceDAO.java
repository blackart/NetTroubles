package ru.blackart.dsi.infopanel.temp.dao;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.Service;

import java.util.List;

public class ServiceDAO extends AbstractDao<Service> {

    public void saveOrUpdate(Service persistent) {
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
        Criteria criteria = this.getSession().createCriteria(Service.class);
        criteria.add(Restrictions.eq("id", id));
        delete((Service) criteria.list().get(0));
    }

    public void delete(Service persistent) {
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

    public Service get(int id) {
        return (Service) getSession().load(Service.class, id);
    }

    public List<Service> getAll() {
        return this.getSession().createCriteria(Service.class).list();
    }
}
