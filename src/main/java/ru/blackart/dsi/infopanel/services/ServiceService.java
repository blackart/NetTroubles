package ru.blackart.dsi.infopanel.services;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Service;

public class ServiceService {
    private static ServiceService serviceService;
    private Session session;

    private synchronized Session getSession() {
        return session;
    }

    public static ServiceService getInstance() {
        if (serviceService == null) {
            serviceService = new ServiceService();
            serviceService.session = SessionFactorySingle.getSessionFactory().openSession();
        }

        return serviceService;
    }

    public Service getService(Service service) {
        return this.getService(service.getId());
    }

    public Service getService(Integer id) {
        Criteria crt_service = this.getSession().createCriteria(Service.class);
        crt_service.add(Restrictions.eq("id", id));
        return (Service)crt_service.list().get(0);
    }
}
