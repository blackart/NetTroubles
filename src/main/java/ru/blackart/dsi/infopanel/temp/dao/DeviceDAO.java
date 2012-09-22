package ru.blackart.dsi.infopanel.temp.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.Device;

import java.util.List;

public class DeviceDAO extends AbstractDao<Device> {
    public void saveOrUpdate(Device persistent) {
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
        Criteria criteria = this.getSession().createCriteria(Device.class);
        criteria.add(Restrictions.eq("id", id));
        delete((Device) criteria.list().get(0));
    }

    public void delete(Device persistent) {
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

    public Device get(int id) {
        return (Device) getSession().load(Device.class, id);
    }

    public List<Device> getAll() {
        return this.getSession().createCriteria(Device.class).list();
    }
}
