package ru.blackart.dsi.infopanel.temp.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.beans.Comment;

import java.util.List;

public class CommentDAO extends AbstractDao<Comment> {
    public void saveOrUpdate(Comment persistent) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(persistent);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
    }

    public void delete(int id) {
        Criteria criteria = this.getSession().createCriteria(Comment.class);
        criteria.add(Restrictions.eq("id", id));
        delete((Comment) criteria.list().get(0));
    }

    public void delete(Comment persistent) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.delete(persistent);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            throw e;
        }
    }

    public Comment get(int id) {
        return (Comment)getSession().load(Comment.class, id);
    }

    public List<Comment> getAll() {
        return this.getSession().createCriteria(Comment.class).list();
    }
}
