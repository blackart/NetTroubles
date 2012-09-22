package ru.blackart.dsi.infopanel.temp.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.Trouble;

import java.util.List;

public class TroubleDAO extends AbstractDao<Trouble> {

    public void saveOrUpdate(Trouble persistent) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(persistent);
            session.getTransaction().commit();
//            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
    }

    public void delete(int id) {
        Criteria criteria = this.getSession().createCriteria(Trouble.class);
        criteria.add(Restrictions.eq("id", id));
        delete((Trouble) criteria.list().get(0));
    }

    public void delete(Trouble persistent) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.delete(persistent);
            session.getTransaction().commit();
//            session.clear();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
    }

    public Trouble get(Trouble persistent) {
        return this.get(persistent.getId());
    }

    public Trouble get(int id) {
        Criteria criteria = this.getSession().createCriteria(Trouble.class);
        criteria.add(Restrictions.eq("id", id));
        return (Trouble) criteria.list().get(0);
    }

    public List<Trouble> getAll() {
        return this.getSession().createCriteria(Trouble.class).list();
    }
}
