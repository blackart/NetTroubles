package ru.blackart.dsi.infopanel;

import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.SessionFactory;
import ru.blackart.dsi.infopanel.beans.*;

public class SessionFactorySingle {
    private static final SessionFactory sessionFactory;

    //Для перехода к hibernate-core и JPA

/*	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}*/

/*    static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new AnnotationConfiguration().configure("/hibernate.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}*/

    static {
        try {
            Logger log = LoggerFactory.getLogger("TestLogger");
            AnnotationConfiguration aconf = new AnnotationConfiguration();

            /*aconf.addAnnotatedClass(Region.class);
            aconf.addAnnotatedClass(Tab.class);
            aconf.addAnnotatedClass(MenuGroup.class);
            aconf.addAnnotatedClass(UserSettings.class);
            aconf.addAnnotatedClass(User.class);
            aconf.addAnnotatedClass(TypeDeviceFilter.class);
            aconf.addAnnotatedClass(DeviceFilter.class);
            aconf.addAnnotatedClass(Device.class);
            aconf.addAnnotatedClass(Devcapsule.class);
            aconf.addAnnotatedClass(Hostgroup.class);
            aconf.addAnnotatedClass(Hoststatus.class);
            aconf.addAnnotatedClass(Service.class);
            aconf.addAnnotatedClass(Comment.class);
            aconf.addAnnotatedClass(TroubleList.class);
            aconf.addAnnotatedClass(Trouble.class);*/

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
