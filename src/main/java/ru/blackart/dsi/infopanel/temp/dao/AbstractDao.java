package ru.blackart.dsi.infopanel.temp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Persistent;

public abstract class AbstractDao<T extends Persistent> implements Dao<T> {
    private SessionFactory sessionFactory = SessionFactorySingle.getSessionFactory();

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
