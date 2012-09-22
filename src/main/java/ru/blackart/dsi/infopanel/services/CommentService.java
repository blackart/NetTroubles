package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Comment;

public class CommentService {
    private static CommentService commentService;
    private Session session;

    private synchronized Session getSession() {
        return session;
    }

    public static CommentService getInstance() {
        if (commentService == null) {
            commentService = new CommentService();
            commentService.session = SessionFactorySingle.getSessionFactory().openSession();
        }

        return commentService;
    }

    public Comment getComment(Comment comment) {
        return this.getComment(comment.getId());
    }

    public Comment getComment(Integer id) {
        Criteria crt_comment = this.getSession().createCriteria(Comment.class);
        crt_comment.add(Restrictions.eq("id", id));
        return (Comment) crt_comment.list().get(0);
    }

    public void save(Comment comment) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(comment);
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

    public void update(Comment comment) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(comment);
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

    public synchronized void delete(Comment comment) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(comment);
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
}
