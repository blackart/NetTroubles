package test;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.TroubleList;

import java.io.IOException;
import java.util.List;

public class testDB {
    public static void main(String[] args) throws IOException {

        SessionFactory sessionFactory = SessionFactorySingle.getSessionFactory();
        Session session = sessionFactory.openSession();

        Criteria crt_trouble = session.createCriteria(TroubleList.class);
//        crt_trouble.add(Restrictions.eq("name", "main"));
        List<TroubleList> troubleList = crt_trouble.list();

        for (TroubleList t : troubleList) {
            System.out.println(t.getName());
        }

        session.flush();
        session.close();


    }
}
