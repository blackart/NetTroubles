package ru.blackart.dsi.infopanel.temp;

public class test {
    public static void main(String args[]) {
        //тест 1

//        DataObjectModel dataObjectModel = DataObjectModel.getInstance();

//        List<Trouble> troubles = dataObjectModel.getTroubles();

//        Trouble trouble = troubles.get(0);
//        System.out.print(troubles.get(0).getDevcapsules().get(0).getDevice().getHostgroup().getName());

//        List<Devcapsule> devcapsules = dataObjectModel.getDevcapsulesForTrouble(trouble);

//        System.out.print(devcapsules.get(0).getDevice().getHostgroup().getName());

        /*Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria criteria_1 = session.createCriteria(Device.class);
        criteria_1.add(Restrictions.eq("name", "asedar"));
        Device device = (Device)criteria_1.list().get(0);
        Criteria criteria_2 = session.createCriteria(Trouble.class);
        criteria_2.add(Restrictions.eq("device", device));
        List<Trouble> findTroubles = (List<Trouble>)criteria_2.list();

        for (Trouble removeTrouble : findTroubles) {
            session.delete(removeTrouble);
        }

        session.getTransaction().commit();
        session.flush();
        session.close();*/

        //тест 2
        /*Device device = new Device();
        device.setName("sw54");
        device.setDescription("my test");

        Criteria criteria = session.createCriteria(Hostgroup.class);
        criteria.add(Restrictions.eq("num", 10));
        List<Hostgroup> hostgroup_arr = criteria.list();
        Hostgroup hostgroup = hostgroup_arr.get(0);

        device.setHostgroup(hostgroup);

        session.save(device);*/

//        Criteria criteria = session.createCriteria(Device.class);

//        Device dev = (Device)criteria.list().get(0);
//        System.out.println(dev.getName());

//        String[] str_date_mas = "12/02/2003".split("\\/");
//        String[] str_time_mas = "12:04:43".split(":");
//        calendar.set(Integer.valueOf(str_date_mas[2]),Integer.valueOf(str_date_mas[1]),Integer.valueOf(str_date_mas[0]),Integer.valueOf(str_time_mas[0]),Integer.valueOf(str_time_mas[1]),Integer.valueOf(str_time_mas[2]));




        /*Calendar calendar = Calendar.getInstance();
        
        calendar.set(2003,2,1,15,10,54);

        java.sql.Date date = new java.sql.Date(calendar.getTimeInMillis());*/
    }
}
