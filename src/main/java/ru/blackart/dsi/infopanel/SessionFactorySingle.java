package ru.blackart.dsi.infopanel;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactorySingle {
    private static final SessionFactory sessionFactory;

    static {
        try {
            Logger log = LoggerFactory.getLogger("TestLogger");
            AnnotationConfiguration aconf = new AnnotationConfiguration();

            sessionFactory = aconf.configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
