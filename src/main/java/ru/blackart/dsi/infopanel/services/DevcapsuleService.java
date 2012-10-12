package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Device;

import java.util.List;

public class DevcapsuleService {
    private static DevcapsuleService devcapsuleService;
    private Session session;

    private synchronized Session getSession() {
        return session;
    }

    public static DevcapsuleService getInstance() {
        if (devcapsuleService == null) {
            devcapsuleService = new DevcapsuleService();
            devcapsuleService.session = SessionFactorySingle.getSessionFactory().openSession();
        }

        return devcapsuleService;
    }

    public synchronized void update(Devcapsule devcapsule) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.update(devcapsule);
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

    public synchronized void save(Devcapsule devcapsule) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.save(devcapsule);
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

    public synchronized void delete(Devcapsule devcapsule) {
        Session session = this.getSession();
        try {
            session.beginTransaction();
            session.delete(devcapsule);
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

    public Devcapsule getDevcapsule(Devcapsule devcapsule) {
        return this.getDevcapsule(devcapsule.getId());
    }

    public Devcapsule getDevcapsule(Integer id) {
        Criteria crt_devcapsule = this.getSession().createCriteria(Devcapsule.class);
        crt_devcapsule.add(Restrictions.eq("id", id));
        List list =  crt_devcapsule.list();
        return list.size() > 0 ? (Devcapsule)crt_devcapsule.list().get(0) : null;
    }

    public List<Devcapsule> getDevcWithOpenUpDateForDevice(Device device) {
        Criteria crt_devcapsule = this.getSession().createCriteria(Devcapsule.class);
        crt_devcapsule
                .add(Restrictions.eq("device", device))
                .add(Restrictions.and(Restrictions.isNull("timedown"), Restrictions.isNotNull("timeup")));

        return crt_devcapsule.list();
    }

    public List<Devcapsule> getDevcWithOpenDownDateForDevice(Device device) {
        Criteria crt_devcapsule = this.getSession().createCriteria(Devcapsule.class);
        crt_devcapsule
                .add(Restrictions.eq("device", device))
                .add(Restrictions.and(Restrictions.isNotNull("timedown"), Restrictions.isNull("timeup")));

        return crt_devcapsule.list();
    }

    public List<Devcapsule> sortDevcapsulByTime(List<Devcapsule> dev) {
        for (int i = 0; i < dev.size(); i++) {
            for (int j = 0; j < i; j++) {
                Long dev_time_i;
                Long dev_time_j;
                if (dev.get(i).getTimedown() != null) {
                    dev_time_i = Long.valueOf(dev.get(i).getTimedown());
                } else {
                    dev_time_i = Long.valueOf(dev.get(i).getTimeup());
                }
                if (dev.get(j).getTimedown() != null) {
                    dev_time_j = Long.valueOf(dev.get(j).getTimedown());
                } else {
                    dev_time_j = Long.valueOf(dev.get(j).getTimeup());
                }

                if (dev_time_i > dev_time_j) {  /*> - по убыванию, < - по возрастанию*/
                    Devcapsule devc_1 = dev.get(i);
                    dev.set(i, dev.get(j));
                    dev.set(j, devc_1);
                }
            }
        }

        return dev;
    }
}
