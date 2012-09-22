package ru.blackart.dsi.infopanel.temp.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.TroubleList;

import java.util.List;

public class TroubleListDAO extends AbstractDao<TroubleList> {
    public void saveOrUpdate(TroubleList persistent) {
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
        Criteria criteria = this.getSession().createCriteria(TroubleList.class);
        criteria.add(Restrictions.eq("id", id));
        delete((TroubleList) criteria.list().get(0));
    }

    public void delete(TroubleList persistent) {
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

    public TroubleList get(TroubleList troubleList) {
        return get(troubleList.getId());
    }

    public TroubleList get(int id) {
        Session session = getSession();
        session.beginTransaction();
        TroubleList troubleList = (TroubleList)session.load(TroubleList.class, id);
        session.getTransaction().commit();
        return troubleList;
    }

    public List<TroubleList> getAll() {
        return this.getSession().createCriteria(TroubleList.class).list();
    }
}
