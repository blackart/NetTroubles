package ru.blackart.dsi.infopanel.temp;

public class LogEngine {
    /*private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static LogEngine logEngine;
    private SessionFactory sessionFactory;
    private int trueDownInterval;
    private DataModelConstructor dataModelConstructor;
    *//*private TroubleList list_of_current_troubles;
    private TroubleList list_of_closed_troubles;
    private TroubleList list_of_trash_troubles;
    private TroubleList list_of_waiting_close_troubles;*//*
    private boolean learning;
    private HashMap<String,Date> upDevcList;
    private Users systemUser;

    public synchronized HashMap<String, Date> getUpDevcList() {
        return upDevcList;
    }

    public synchronized int getTrueDownInterval() {
        return trueDownInterval;
    }

    public synchronized void setTrueDownInterval(int trueDownInterval) {
        this.trueDownInterval = trueDownInterval;
    }

    public synchronized boolean isLearning() {
        return learning;
    }

    public synchronized void setLearning(boolean learning) {
        this.learning = learning;
    }

    *//*public synchronized TroubleList getList_of_current_troubles() {
        return list_of_current_troubles;
    }

    public synchronized void setList_of_current_troubles(TroubleList list_of_current_troubles) {
        this.list_of_current_troubles = list_of_current_troubles;
    }

    public synchronized TroubleList getList_of_complete_troubles() {
        return list_of_closed_troubles;
    }

    public synchronized TroubleList getList_of_trash_troubles() {
        return list_of_trash_troubles;
    }

    public synchronized TroubleList getList_of_waiting_close_troubles() {
        return list_of_waiting_close_troubles;
    }*//*

    public synchronized void sortAndUpdatetroubleLists() {
        TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
//        troubleListsManager.setTroubleListForCallCenter(this.list_of_current_troubles);
//        troubleListsManager.setTrashTroubleList(this.list_of_trash_troubles);

//        troubleListsManager.sortTroubleList(this.list_of_closed_troubles);
        troubleListsManager.sortTroubleList(dataModelConstructor.getList_of_waiting_close_troubles());
        troubleListsManager.sortTroubleList(dataModelConstructor.getList_of_trash_troubles());
    }

    public synchronized List<Devcapsule> sortDevcapsulByTime(List<Devcapsule> dev) {
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

                if (dev_time_i > dev_time_j) {  *//*> - по убыванию, < - по возрастанию*//*
                    Devcapsule devc_1 = dev.get(i);
                    dev.set(i, dev.get(j));
                    dev.set(j, devc_1);
                }
            }
        }

        return dev;
    }

    public synchronized List<Devcapsule> sortDevcapsulByUpTime(List<Devcapsule> dev) {
        for (int i = 0; i < dev.size(); i++) {
            for (int j = 0; j < i; j++) {
                Long dev_time_i = Long.valueOf(dev.get(i).getTimeup());
                Long dev_time_j = Long.valueOf(dev.get(j).getTimeup());
                        
                if (dev_time_i > dev_time_j) {  *//*> - по убыванию, < - по возрастанию*//*
                    Devcapsule devc_1 = dev.get(i);
                    dev.set(i, dev.get(j));
                    dev.set(j, devc_1);
                }
            }
        }

        return dev;
    }    

    public synchronized Date parse(String str_date, String str_time) {
        String[] str_date_mas = str_date.split("\\/");
        String[] str_time_mas = str_time.split(":");

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(str_time_mas[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(str_time_mas[1]));
        calendar.set(Calendar.SECOND, Integer.valueOf(str_time_mas[2]));

        calendar.set(Calendar.YEAR, Integer.valueOf(str_date_mas[2]));
        calendar.set(Calendar.MONTH, Integer.valueOf(str_date_mas[1]) - 1);
        calendar.set(Calendar.DATE, Integer.valueOf(str_date_mas[0]));

        return calendar.getTime();
    }

    public synchronized static LogEngine getInstance() {         //метод вызывается при создании объекта класса
        if (logEngine == null) {
            logEngine = new LogEngine();
            logEngine.sessionFactory = SessionFactorySingle.getSessionFactory();

            logEngine.dataModelConstructor = DataModelConstructor.getInstance();

            Session session = logEngine.sessionFactory.openSession();
            session.beginTransaction();

            //находим system user
            Criteria crt_user = session.createCriteria(Users.class);
            crt_user.add(Restrictions.eq("login", "system"));

            logEngine.log.info("Find system user ...");

            if (crt_user.list().size() > 0) {
                logEngine.systemUser = (Users) crt_user.list().get(0);
                logEngine.log.info("System user was found");
            } else {
                logEngine.systemUser = null;
                logEngine.log.info("System user was not found");
            }

            session.flush();
            session.close();

            logEngine.upDevcList = new HashMap<String,Date>();
        }
        return logEngine;
    }


    public synchronized void updateTroubleList(int id, String title, String description, String legend, String date_in, String date_out, String timeout, String[] services, String troubleList, Users author) {
        log.info("Update trouble list ...");

        boolean trouble_close = false;
        boolean trouble_crm = false;

        if (troubleList.equals("complete")) {
            trouble_close = true;
            trouble_crm = true;
        } else if (troubleList.equals("waiting_close")) {
            trouble_close = true;
            trouble_crm = false;
        }

        TroubleList tl = dataModelConstructor.getTroubleListForName(troubleList);

        if (tl != null) {
            int index = -1;
            for (Trouble t : tl.getTroubles()) {
                if (t.getId() == id) {
                    index = tl.getTroubles().indexOf(t);
                }
            }

            Trouble local_trouble = tl.getTroubles().get(index);

            //open session hibernate to database
            Session session = SessionFactorySingle.getSessionFactory().openSession();
            session.beginTransaction();          //start transaction

            //select from DB entry with name "current" and type of TroubleList. This entry is list of entry of current troubles.
            Criteria crt_trouble = session.createCriteria(Trouble.class);
            crt_trouble.add(Restrictions.eq("id", id));
            List<Trouble> troubles = crt_trouble.list();
            Trouble trouble = troubles.get(0);

            trouble.setTitle(title);
            trouble.setActualProblem(description);
            trouble.setClose(trouble_close);
            trouble.setCrm(trouble_crm);
            trouble.setAuthor(author);

            local_trouble.setTitle(title);
            local_trouble.setDescription(description);
            local_trouble.setLegend(legend);
            local_trouble.setClose(trouble_close);
            local_trouble.setCrm(trouble_crm);
            local_trouble.setAuthor(author);

            if ((services != null) && (services.length > 0)) {
                List<Service> service_ = new ArrayList<Service>();
                for (int i = 0; i < services.length; i++) {
                    if (!services[i].equals("")) {
                        Criteria crt_serv = session.createCriteria(Service.class);
                        crt_serv.add(Restrictions.eq("id", Integer.valueOf(services[i])));
                        service_.add((Service) crt_serv.list().get(0));
                    }
                }
                trouble.setServices(service_);
                local_trouble.setServices(service_);
            }

            trouble.setDate_out(date_out);
            trouble.setDate_in(date_in);
            trouble.setTimeout(timeout);

            local_trouble.setDate_out(date_out);
            local_trouble.setDate_in(date_in);
            local_trouble.setTimeout(timeout);

            session.getTransaction().commit();
            session.flush();
            session.close();
        }
        log.info("Update trouble list complete");
    }

    public synchronized void addDevcapsuleToTrouble(int id, Devcapsule devcapsule) {
        log.info("Add devcapsuleToTrouble ...");

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();

        Criteria crt_trouble = session.createCriteria(Trouble.class);
        crt_trouble.add(Restrictions.eq("id", id));

        Trouble trouble = (Trouble)crt_trouble.list().get(0);
        log.info("Trouble - " + trouble.getTitle());

        trouble.getDevcapsules().add(devcapsule);
        log.info("Add devcapsule " + devcapsule.getDevice().getName() + " to trouble " + trouble.getTitle());

        TroubleList tl = dataModelConstructor.getTroubleListForTrouble(trouble);
        log.info("Current trouble list for trouble - " + tl.getName());

        for (Trouble tr : tl.getTroubles()) {
            if (tr.getId() == id) {
                List<Trouble> troubles = new ArrayList<Trouble>();
                troubles.add(tr);
                devcapsule.setTroubles(troubles);
                tr.getDevcapsules().add(devcapsule);
                log.info("Add devcapsule to local trouble - " + tr.getTitle());
            }
        }

        session.save(trouble);

        session.getTransaction().commit();
        session.flush();
        session.close();
    }

    public synchronized int addTroubleToTroubleList(Devcapsule devcapsule, String nameTroubleList, Users author) {
        log.info("Adding trouble to trouble list for devcapsule - " + devcapsule.getDevice().getName());
        int id = -1;
        if (devcapsule != null) {
            if ((nameTroubleList.equals("current")) && (devcapsule.getComplete())) {
                nameTroubleList = "waiting_close";
            }

            TroubleList tl = dataModelConstructor.getTroubleListForName(nameTroubleList);
            log.info("Trouble list for " + devcapsule.getDevice().getName() + " - " + tl.getName());

            if (tl != null) {
                Session session = this.sessionFactory.openSession();

                Criteria criteria = session.createCriteria(TroubleList.class);
                criteria.add(Restrictions.eq("name", nameTroubleList));

                TroubleList troubleList;

                if (criteria.list().size() > 0) {
                    troubleList = (TroubleList) criteria.list().get(0);
                } else {
                    troubleList = new TroubleList();
                    troubleList.setName(nameTroubleList);
                    List<Trouble> troubles = new ArrayList<Trouble>();
                    troubleList.setTroubles(troubles);

                    session.beginTransaction();
                    session.save(troubleList);
                    session.getTransaction().commit();
                }


                Trouble trouble = new Trouble();
                log.info("Create new trouble for device " + devcapsule.getDevice().getName());

                ArrayList<Devcapsule> devcapsules = new ArrayList<Devcapsule>();
                devcapsules.add(devcapsule);

                trouble.setDevcapsules(devcapsules);

                trouble.setTitle(devcapsule.getDevice().getName() + ", " + devcapsule.getDevice().getDescription());
                trouble.setDate_in(devcapsule.getTimedown());
                trouble.setAuthor(author);
                trouble.setComments(new ArrayList<Comment>());

                if (nameTroubleList.equals("complete")) trouble.setDate_out(devcapsule.getTimedown());

                trouble.setLegend("");
                trouble.setDescription("");

                if (nameTroubleList.equals("complete")) {
                    trouble.setCrm(true);
                } else {
                    trouble.setCrm(false);
                }

                if (devcapsule.getComplete()) {
                    trouble.setClose(true);
                } else {
                    trouble.setClose(false);
                }

                log.info("trouble status CRM - " + trouble.getCrm() + " , status close - " + trouble.getClose());

                List<Trouble> troubles = new ArrayList<Trouble>();

                troubles.add(trouble);
                devcapsule.setTroubles(troubles);

                session.beginTransaction();
                session.save(trouble);
                session.getTransaction().commit();

                log.info("Save trouble for - " + devcapsule.getDevice().getName());

                troubleList.getTroubles().add(trouble);

                session.beginTransaction();
                session.save(troubleList);
                session.getTransaction().commit();

                log.info("Add trouble " + trouble.getTitle() + " to trouble list " + troubleList.getName());

                List<TroubleList> tl_loc = new ArrayList<TroubleList>();
                tl_loc.add(tl);
                tl.getTroubles().add(trouble);

                *//*if (nameTroubleList.equals("current")) {
                    TroubleListsManager.getInstance().setCurrTroubleList(tl);
                } else if (nameTroubleList.equals("complete")) {
                    TroubleListsManager.getInstance().setClosedTroubleList(tl);
                }*//*

                id = trouble.getId();

                session.flush();
                session.close();
            }
        }

        return id;
    }

    public synchronized void removeDevcapsuleFromTrouble(int id_devc) throws IOException, JMSException, URISyntaxException {
        Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria crt_tr = session.createCriteria(Devcapsule.class);
        crt_tr.add(Restrictions.eq("id", Integer.valueOf(id_devc)));
        Devcapsule devcapsule = (Devcapsule) crt_tr.list().get(0);

        Trouble trouble = devcapsule.getTroubles().get(0);
        TroubleList t_list = dataModelConstructor.getTroubleListForTrouble(trouble);

        if (trouble.getDevcapsules().size() > 1) {
            trouble.getDevcapsules().remove(devcapsule);
        }

        Trouble local_trouble = null;

        if (t_list != null) {
            int id_local_trouble = -1;
            int id_local_devc = -1;

            for (Trouble t : t_list.getTroubles()) {
                if (t.getId() == trouble.getId()) {
                    for (Devcapsule d : t.getDevcapsules()) {
                        if (d.getId() == id_devc) {
                            id_local_trouble = t_list.getTroubles().indexOf(t);
                            id_local_devc = t.getDevcapsules().indexOf(d);
                            local_trouble = t;
                        }
                    }
                }
            }

            t_list.getTroubles().get(id_local_trouble).getDevcapsules().remove(id_local_devc);
        }

        boolean move = true;
        for (Devcapsule d : trouble.getDevcapsules()) {
            move = move && d.getComplete();
        }

        if (move) {
            trouble.setClose(true);
            trouble.setDate_out(this.sortDevcapsulByUpTime(trouble.getDevcapsules()).get(0).getTimeup());
            local_trouble.setClose(true);
            local_trouble.setDate_out(this.sortDevcapsulByUpTime(trouble.getDevcapsules()).get(0).getTimeup());

            session.save(trouble);
            session.getTransaction().commit();

            if (!devcapsule.getComplete()) {
                this.moveTrouble(trouble.getId(), dataModelConstructor.getTroubleListForTrouble(trouble).getName(),"waiting_close");
                if (trouble.getCrm()) {
                    this.sendToCRM(trouble.getId(),"2");
                }
            }
        } else {
            if (trouble.getCrm()) {
                this.sendToCRM(trouble.getId(),"1");
            }
        }

        session.flush();
        session.close();
    }

//    public synchronized void

    public synchronized void deleteTrouble(int id) {
        log.info("delete trouble ...");
        Session session = this.sessionFactory.openSession();


        log.info("Trouble id - " + id);

        Criteria crt_trouble = session.createCriteria(Trouble.class);
        crt_trouble.add(Restrictions.eq("id", id));

        Trouble trouble = (Trouble) crt_trouble.list().get(0);
        TroubleList t_list = dataModelConstructor.getTroubleListForTrouble(trouble);

        log.info("Current trouble list - " + t_list);

        session.beginTransaction();

        if (t_list != null) {
            Trouble find_tr = null;
            log.info("Find trouble in local trouble list ...");
            for (Trouble tr : t_list.getTroubles()) {
                if (tr.getId() == id) {
                    find_tr = tr;
                }
            }
            log.info("Found trouble - " + find_tr);
            if (find_tr != null) t_list.getTroubles().remove(find_tr);
            log.info("Remove trouble from trouble list - " + t_list);


                *//*trouble.getTroublelist().getTroubles().remove(trouble);
                log.info("Remove trouble " + trouble.getTitle() + " from trouble list - " + tl.getName());
                session.up(tl);*//*

            for (Devcapsule devcapsule : trouble.getDevcapsules()) {
                devcapsule.getTroubles().remove(trouble);
                log.info("Remove trouble " + trouble.getTitle() + " from devcapsule - " + devcapsule.getDevice().getName());
                session.save(devcapsule);
            }

            trouble.getComments().clear();
            trouble.getServices().clear();

            session.save(trouble);
        }

        session.delete(trouble);
        log.info("Delete trouble " + trouble.getTitle() + " from DB");

        session.getTransaction().commit();
        session.flush();
        session.close();
    }

    public synchronized void moveTrouble(int id, String oldTroubleListName, String newTroubleListName) {
        log.info("Moving trouble ...");
        if (!oldTroubleListName.equals(newTroubleListName)) {
            Session session = this.sessionFactory.openSession();
            session.beginTransaction();

            //for DB
            Trouble trouble = null;
            TroubleList oldTroubleList = null;
            TroubleList newTroubleList = null;

            Criteria crt_trouble = session.createCriteria(Trouble.class);
            crt_trouble.add(Restrictions.eq("id", id));
            ArrayList<Trouble> troubles = (ArrayList<Trouble>) crt_trouble.list();
            if (troubles.size() > 0) {
                trouble = troubles.get(0);
            }
            log.info("Trouble - " + (troubles.size() > 0 ? trouble.getTitle() : "null"));

            Criteria crt_oldTroubleList = session.createCriteria(TroubleList.class);
            crt_oldTroubleList.add(Restrictions.eq("name", oldTroubleListName));
            ArrayList<TroubleList> oldTroubleLists = (ArrayList<TroubleList>) crt_oldTroubleList.list();
            if (oldTroubleLists.size() > 0) {
                oldTroubleList = oldTroubleLists.get(0);
            }
            log.info("OldTroubleList - " + (oldTroubleLists.size() > 0 ? oldTroubleList.getName() : "null"));

            Criteria crt_newTroubleList = session.createCriteria(TroubleList.class);
            crt_newTroubleList.add(Restrictions.eq("name", newTroubleListName));
            ArrayList<TroubleList> newTroubleLists = (ArrayList<TroubleList>) crt_newTroubleList.list();
            if (newTroubleLists.size() > 0) {
                newTroubleList = newTroubleLists.get(0);
            }
            log.info("NewTroubleList - " + (newTroubleLists.size() > 0 ? newTroubleList.getName() : "null"));

            //for local
            TroubleList oldTroubleListLocal = dataModelConstructor.getTroubleListForName(oldTroubleListName);
            log.info("OldTroubleList local - " + (oldTroubleListLocal == null ? oldTroubleListLocal.getName() : "null"));
            TroubleList newTroubleListLocal = dataModelConstructor.getTroubleListForName(newTroubleListName);
            log.info("NewTroubleList local - " + (newTroubleListLocal == null ? newTroubleListLocal.getName() : "null"));
            Trouble troubleLocal = null;

            if (oldTroubleListLocal != null) {
                for (Trouble t : oldTroubleListLocal.getTroubles()) {
                    if (t.getId() == id) {
                        troubleLocal = t;
                    }
                }
            }
            log.info("Trouble local - " + (troubleLocal == null ? troubleLocal.getTitle() : "null"));

            //for DB
            if ((trouble != null) && (oldTroubleList != null) && (newTroubleList != null)) {
                oldTroubleList.getTroubles().remove(trouble);
                session.save(oldTroubleList);
                log.info("Remove trouble  " + trouble.getId() + "  from " + oldTroubleList.getName() + " trouble list");

                newTroubleList.getTroubles().add(trouble);
                session.save(newTroubleList);
                log.info("Add trouble  " + trouble.getId() + " to " + newTroubleList.getName() + " trouble list");
            }

            session.getTransaction().commit();
            session.flush();
            session.close();

            //for local
            if ((oldTroubleList != null) && (newTroubleListLocal != null) && (troubleLocal != null)) {
                oldTroubleListLocal.getTroubles().remove(oldTroubleListLocal.getTroubles().indexOf(troubleLocal));
                log.info("Remove local trouble  " + trouble.getId() + "  from " + oldTroubleList.getName() + " local trouble list");
                List<TroubleList> troubleLists = new ArrayList<TroubleList>();
                troubleLists.add(newTroubleListLocal);
                newTroubleListLocal.getTroubles().add(troubleLocal);
                log.info("Add local trouble  " + trouble.getId() + " to " + newTroubleList.getName() + " local trouble list");
            }
        }
    }

    public synchronized boolean validTrouble(Trouble trouble) {
        boolean valid = true;
        try {
            if ((trouble.getTitle() == null) || (trouble.getTitle().trim().equals(""))) {
                valid = valid && false;
                log.info("");
            }
            if (trouble.getServices() == null) {
                valid = valid && false;
            }
            if (trouble.getDate_in() == null) {
                valid = valid && false;
            }
            if (trouble.getTimeout() == null) {
                valid = valid && false;
            }
            if ((trouble.getLegend() == null) || (trouble.getLegend().trim().equals(""))) {
                valid = valid && false;
            }
            if ((trouble.getDescription() == null) || (trouble.getDescription().trim().equals(""))) {
                valid = valid && false;
            }
        } catch (LazyInitializationException e) {
            valid = false;
            //and some code
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    public synchronized boolean sendToCRM(int id, String status_crm) throws IOException, JMSException, URISyntaxException {
        Boolean send_to_crm = false;
        log.info("Sending to CRM ...");
        log.info("ID trouble - " + id);
        Session session = this.sessionFactory.openSession();

        Criteria crt_trouble = session.createCriteria(Trouble.class);
        crt_trouble.add(Restrictions.eq("id", id));
        Trouble trouble = (Trouble) crt_trouble.list().get(0);

        log.info("Trouble name - " + trouble.getTitle());

        TroubleList localTroubleList = dataModelConstructor.getTroubleListForTrouble(trouble);
        log.info("Trouble list - " + localTroubleList.getName());
        Trouble localTrouble = null;

        for (Trouble tr : localTroubleList.getTroubles()) {
            if (tr.getId() == id) {
                localTrouble = tr;
            }
        }
        log.info("Local trouble - " + localTrouble.getTitle());

        if (localTrouble != null) {
            if (Integer.valueOf(status_crm) <= 3) {                       //отправка из current или waiting close очереди. 1 - current, 2 - waiting close
                if (validTrouble(trouble)) {
                    String service = "";
                    for (Service s : trouble.getServices()) {
                        service = service + s.getId() + ";";
                    }

                    log.info("Services - " + service);

                    String sw = "";
                    for (Devcapsule d : trouble.getDevcapsules()) {
                        Device device = d.getDevice();
                        sw = sw + device.getName() + (device.getHoststatus() != null ? ", " + device.getHoststatus().getName() : "") + "; ";
                    }

                    log.info("SW - " + sw);

                    SimpleDateFormat format = new SimpleDateFormat();
                    format.applyPattern("dd/MM/yyyy HH:mm:ss");

                    Date timeout = new Date(Long.valueOf(trouble.getTimeout()));
                    Date date_in = new Date(Long.valueOf(trouble.getDate_in()));

                    Date date_out = null;
                    if ((trouble.getDate_out() != null) && (!trouble.getDate_out().equals(""))) {
                        date_out = new Date(Long.valueOf(trouble.getDate_out()));
                    }

                    log.info("timeout - " + timeout + "; date_in - " + date_in + "; date_out - " + date_out);

//                    CrmEngine crmEngine = new CrmEngine();
                    //todo раскомментить при заливке на сервер, отправка в CRM трабла !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                   send_to_crm = crmEngine.sendTrouble(trouble.getId(),trouble.getTitle(),sw,service,trouble.getLegend(),trouble.getDescription(),format.format(timeout),format.format(date_in),date_out == null ? null : format.format(date_out),status_crm,trouble.getAuthor().getFio());
                    send_to_crm = true;

                    log.info("Send to crm trouble - " + trouble.getTitle());

                    trouble.setCrm(true);
                    localTrouble.setCrm(true);
                    log.info("Trouble status CRM - " + localTrouble.getCrm());

                    session.beginTransaction();
                    session.save(trouble);
                    session.getTransaction().commit();
                    session.flush();
                    session.close();

                    log.info("Trouble status CLOSE - " + trouble.getClose());
                    if (trouble.getClose()) {
                        String list_name = dataModelConstructor.getTroubleListForTrouble(trouble).getName();
                        log.info("Current troubleList - " + list_name);
                        if (list_name.equals("waiting_close")) {
                            this.moveTrouble(trouble.getId(), "waiting_close", "complete");
                            log.info("Trouble - " + trouble.getTitle() + " - move to complete trouble list");
                        }
                    }
                }
            } else if (Integer.valueOf(status_crm) == 4) {
                trouble.setCrm(true);
                localTrouble.setCrm(true);
                log.info("Trouble status CRM - " + localTrouble.getCrm());

                session.beginTransaction();
                session.save(trouble);
                session.getTransaction().commit();
                session.flush();
                session.close();

                log.info("Trouble status CLOSE - " + trouble.getClose());
                if (trouble.getClose()) {
                    String list_name = dataModelConstructor.getTroubleListForTrouble(trouble).getName();
                    log.info("Current troubleList - " + list_name);
                    if (list_name.equals("waiting_close")) {
                        this.moveTrouble(trouble.getId(), "waiting_close", "complete");
                        log.info("Trouble - " + trouble.getTitle() + " - move to complete trouble list");
                    }
                }
                send_to_crm = true;
            } else if (Integer.valueOf(status_crm) == 5) {
                trouble.setCrm(true);
                localTrouble.setCrm(true);
                log.info("Trouble status CRM - " + localTrouble.getCrm());

                session.beginTransaction();
                session.save(trouble);
                session.getTransaction().commit();
                session.flush();
                session.close();

                log.info("Trouble status CLOSE - " + trouble.getClose());
                if (trouble.getClose()) {
                    String list_name = dataModelConstructor.getTroubleListForTrouble(trouble).getName();
                    log.info("Current troubleList - " + list_name);

                    this.moveTrouble(trouble.getId(), list_name, "trash");
                    log.info("Trouble - " + trouble.getTitle() + " - move to trash trouble list");
                }
                send_to_crm = true;
            } else if (Integer.valueOf(status_crm) == 6) {                        //
                if (validTrouble(trouble)) {
                    String service = "";
                    for (Service s : trouble.getServices()) {
                        service = service + s.getId() + ";";
                    }

                    log.info("Services - " + service);

                    String sw = "";
                    for (Devcapsule d : trouble.getDevcapsules()) {
                        Device device = d.getDevice();
                        sw = sw + device.getName() + (device.getHoststatus() != null ? ", " + device.getHoststatus().getName() : "") + "; ";
                    }

                    log.info("SW - " + sw);

                    SimpleDateFormat format = new SimpleDateFormat();
                    format.applyPattern("dd/MM/yyyy HH:mm:ss");

                    Date timeout = new Date(Long.valueOf(trouble.getTimeout()));
                    Date date_in = new Date(Long.valueOf(trouble.getDate_in()));

                    Date date_out = null;
                    if ((trouble.getDate_out() != null) && (!trouble.getDate_out().equals(""))) {
                        date_out = new Date(Long.valueOf(trouble.getDate_out()));
                    }

                    log.info("timeout - " + timeout + "; date_in - " + date_in + "; date_out - " + date_out);

//                    CrmEngine crmEngine = new CrmEngine();
                    //todo раскомментить при заливке на сервер, отправка в CRM трабла !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                   send_to_crm = crmEngine.sendTrouble(trouble.getId(),trouble.getTitle(),sw,service,trouble.getLegend(),trouble.getDescription(),format.format(timeout),format.format(date_in),date_out == null ? null : format.format(date_out),status_crm,trouble.getAuthor().getFio());
                    send_to_crm = true;

                    log.info("Send to crm trouble - " + trouble.getTitle());

                    trouble.setCrm(true);
                    localTrouble.setCrm(true);
                    log.info("Trouble status CRM - " + localTrouble.getCrm());

                    session.beginTransaction();
                    session.save(trouble);
                    session.getTransaction().commit();
                    session.flush();
                    session.close();

                    log.info("Trouble status CLOSE - " + trouble.getClose());
                    if (trouble.getClose()) {
                        String list_name = dataModelConstructor.getTroubleListForTrouble(trouble).getName();
                        log.info("Current troubleList - " + list_name);
                        this.moveTrouble(trouble.getId(), list_name, "trash");
                        log.info("Trouble - " + trouble.getTitle() + " - move to trash trouble list");
                    }
                }
            }
        }

        return send_to_crm;
    }


    public synchronized Device getDevice(String device_name) {
        log.info("Check device - " + device_name + " in DB");

        DeviceManager deviceManager = DeviceManager.getInstance();
        Device device = deviceManager.getDevice(device_name);

//        log.info("Result check device - " + device_name + " - " + (result == null ? "null" : "find device in DB, name - " + result.getName()));
        return device;
    }

    private synchronized Device saveDevice(String device, String group, String desc) {
        log.info("Saving  device - " + device + " in DB ...");

        DeviceManager deviceManager = DeviceManager.getInstance();
        Device return_dev = deviceManager.addNewDevice(device, group, desc);

        log.info("Saving device - " + device + " in DB complete");

        return return_dev;
    }

    private synchronized Device updateDevice(Device dev, String device_name, String group, String desc) {
        log.info("Update  device - " + device_name + " in DB ...");
        DeviceManager deviceManager = DeviceManager.getInstance();
        dev = deviceManager.updateDevice(dev, device_name, group, desc);
        log.info("Update device - " + device_name + " in DB complete");
        return dev;
    }

    public synchronized Device addDevice(String device, String group, String desc) {
        Device dev = this.getDevice(device);

        if (dev == null) { //если даннх о узле в DB нет, записываем.
            dev = this.saveDevice(device, group, desc);
            log.info("Save info about new device - " + device);
        } else {   //если данные о узле в DB есть
            if (this.learning) { //и включен режим обучения
                dev = this.updateDevice(dev, device, group, desc); //сохраняем новые данные в DB и возвращаем объект
                log.info("ENABLED MOD LEARN INFO DEVICE. Update info about new device - " + device);
            } else {    //если режим обучения не включен
                log.info("Get info about " + device + " from DB");
            }
        }
        return dev;
    }


    public synchronized List<Devcapsule> getDevcapsulesDownForDevice(Device dev) {
        List<Devcapsule> devcapsules = new ArrayList<Devcapsule>();

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();

        //достаем из БД все капсули с временем падения но без времени поднятия
        Criteria criteria = session.createCriteria(Devcapsule.class);
        criteria
                .add(Restrictions.eq("device", dev))
                .add(Restrictions.and(Restrictions.isNotNull("timedown"), Restrictions.isNull("timeup")));

        devcapsules.addAll(criteria.list());

        session.getTransaction().commit();
        session.flush();
        session.close();

        return devcapsules;
    }


    public synchronized Devcapsule addDownDevcapsul(Device dev, String date, String time) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        Date downDate = this.parse(date, time);
        log.info("Saving devcapsule for " + dev.getName() + " down date - " + format.format(downDate));
        Session session = this.sessionFactory.openSession();


        log.info("Find in DB devcapsule with + " + dev.getName() + " and null timedown and not null timeup");
        Criteria criteria = session.createCriteria(Devcapsule.class);
        criteria
                .add(Restrictions.eq("device", dev))
                .add(Restrictions.and(Restrictions.isNull("timedown"), Restrictions.isNotNull("timeup")));

        List<Devcapsule> devcapsules = criteria.list();
        log.info("Count devcapsule for device " + dev.getName() + " - " + devcapsules.size());
        if (criteria.list().size() > 0) {
            log.info("Sorting list of devcapsules for device " + dev.getName());
            devcapsules = sortDevcapsulByTime(criteria.list());    //Сортируем по убыванию
        }

        Boolean lock = false;
        Devcapsule return_devcapsule = new Devcapsule();

        if (devcapsules.size() > 0) {
            for (Devcapsule devcapsule : devcapsules) {
                if (!lock) {
                    Date upDate = new Date(Long.valueOf(devcapsule.getTimeup()));
                    if (upDate.after(downDate)) {
                        devcapsule.setTimedown(String.valueOf(downDate.getTime()));
                        devcapsule.setComplete(true);

                        session.beginTransaction();
                        session.update(devcapsule);
                        session.getTransaction().commit();

                        lock = true;
                        return_devcapsule = devcapsule;
                        log.info("");
                    }
                }
            }
        }

        if (!lock) {
            criteria = session.createCriteria(Devcapsule.class);
            criteria
                    .add(Restrictions.eq("device", dev))
                    .add(Restrictions.and(Restrictions.isNotNull("timedown"), Restrictions.isNull("timeup")));

            log.info("Find in DB devcapsule with + " + dev.getName() + " and not null timedown and null timeup");

            devcapsules = criteria.list();

            if (criteria.list().size() > 0) {
                devcapsules = sortDevcapsulByTime(criteria.list());    //Сортируем по убыванию
            }
            if (devcapsules.size() > 0) {
                Devcapsule devcapsule = devcapsules.get(0);
                Date downDate_last = new Date(Long.valueOf(devcapsule.getTimedown()));
                if (downDate.before(downDate_last)) {
                    devcapsule.setTimedown(String.valueOf(downDate.getTime()));
                    devcapsule.setComplete(false);

                    session.beginTransaction();
                    session.update(devcapsule);
                    session.getTransaction().commit();

                    log.info("Find devcapsule and update.");

                }
                lock = true;
                return_devcapsule = null;
            }
        }

        if (!lock) {
            Devcapsule devcap = new Devcapsule();
            devcap.setDevice(dev);
            devcap.setComplete(false);
            devcap.setTimedown(String.valueOf(downDate.getTime()));

            session.beginTransaction();
            session.save(devcap);
            session.getTransaction().commit();

            return_devcapsule = devcap;
            log.info("Save devcapsule for device " + dev.getName());
        }


        session.flush();
        session.close();

        return return_devcapsule;
    }

    public synchronized Devcapsule addUpDevcapsul(Devcapsule devcapsule, Date upDate, String status_crm) throws IOException, JMSException, URISyntaxException {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        log.info("Saving devcapsule for device [" + devcapsule.getDevice().getName() + "] down date - " + format.format(upDate));

        Session session = this.sessionFactory.openSession();
        session.beginTransaction();

        log.info("Find devcapsule for device [" + devcapsule.getDevice().getName() + "] in store");
        Criteria crt_devc = session.createCriteria(Devcapsule.class);
        crt_devc.add(Restrictions.eq("id", devcapsule.getId()));
        devcapsule = (Devcapsule) crt_devc.list().get(0);
        log.info("Result found devcapsule - " + devcapsule == null ? "null" : "true");


        Devcapsule return_devcapsule = new Devcapsule();
        if (devcapsule != null) {
            Date downDate = new Date(Long.valueOf(devcapsule.getTimedown())); //берём значение timeDown

            *//*   ищем devcapsule в объекте  *//*
            Devcapsule devcapsule_obj = null;
            Trouble trouble_obj = null;
//            String troubleListName = dataModelConstructor.getTroubleListForTrouble(devcapsule.getTroubles().get(0)).getName();
            TroubleList localTroubleList = dataModelConstructor.getTroubleListForTrouble(devcapsule.getTroubles().get(0));
            log.info("TroubleList of devcapsul - " + localTroubleList.getName());

            log.info("Starting find devcapsule for device [" + devcapsule.getDevice().getName() + "] in local object");
            for (Trouble t : localTroubleList.getTroubles()) {
                for (Devcapsule d : t.getDevcapsules()) {
                    if (d.getId() == devcapsule.getId()) {
                        d.setTimeup(String.valueOf(upDate.getTime()));
                        d.setComplete(true);
                        devcapsule_obj = d;
                        trouble_obj = t;
                        log.info("Found devcapsule for device ["+ d.getDevice().getName() +"] in local object");
                    }
                }
            }

            if (downDate.before(upDate)) {   //если downDate меньше time upDate
                log.info("Time interval for " + devcapsule.getDevice().getName() + " - " + (upDate.getTime() - downDate.getTime()) / (60 * 1000));
                if ((upDate.getTime() - downDate.getTime()) / (60 * 1000) >= this.trueDownInterval) {

                    devcapsule.setTimeup(String.valueOf(upDate.getTime()));
                    devcapsule.setComplete(true);
                    session.update(devcapsule);

                    session.getTransaction().commit();
                    
                    log.info("Devcapsule for " + devcapsule.getDevice().getName() + " saved");

                    Trouble move_trouble = devcapsule.getTroubles().get(0);

                    log.info("Starting process moving trouble " + move_trouble.getTitle() + " which contain devcapsule for [" + devcapsule.getDevice().getName() + "]");
                    boolean move = true;
                    for (Devcapsule d_c : move_trouble.getDevcapsules()) {
                        move = d_c.getComplete() && move;
                    }

                    log.info(move ? "Trouble should be moved" : "Trouble shouldn't be moved");
                    if (move) {
                        session.beginTransaction();
                        trouble_obj.setClose(true);
                        trouble_obj.setDate_out(String.valueOf(upDate.getTime()));
                        move_trouble.setClose(true);
                        move_trouble.setDate_out(String.valueOf(upDate.getTime()));
                        session.save(move_trouble);
                        session.getTransaction().commit();
                        log.info("To problem the status is appropriated is closed");
                        this.moveTrouble(move_trouble.getId(), localTroubleList.getName(), "waiting_close");
                        this.sendToCRM(move_trouble.getId(), status_crm);
                    }
                    return_devcapsule = devcapsule;
                } else {
                    List<Trouble> remove_troubles = devcapsule.getTroubles();

                    Criteria crt_trouble_list = session.createCriteria(TroubleList.class);
                    crt_trouble_list.add(Restrictions.eq("name", "current"));
                    TroubleList troubleList = (TroubleList) crt_trouble_list.list().get(0);

                    for (Trouble tr : remove_troubles) {
                        tr.getDevcapsules().remove(devcapsule);
                        if (tr.getDevcapsules().size() > 0) {
                            session.save(tr);
                        } else {
                            List<Trouble> troubles = troubleList.getTroubles();
                            troubles.remove(tr);
                            session.save(troubleList);
                            session.delete(tr);
                        }
                    }

                    session.delete(devcapsule);

                    if ((devcapsule_obj != null) && (trouble_obj != null)) {
                        log.info("Count of devc - " + trouble_obj.getDevcapsules().size());
                        if (trouble_obj.getDevcapsules().size() == 1) {
                            dataModelConstructor.getList_of_current_troubles().getTroubles().remove(trouble_obj);
                        } else if (trouble_obj.getDevcapsules().size() > 1) {
                            dataModelConstructor.getList_of_current_troubles().getTroubles().get(dataModelConstructor.getList_of_current_troubles().getTroubles().indexOf(trouble_obj)).getDevcapsules().remove(devcapsule_obj);
                        }
                    }
                    session.getTransaction().commit();
                }
            }
        }
        session.flush();
        session.close();

        return return_devcapsule;
    }


    public synchronized void deviceDown(String poolling, String device, String date, String time, String group, String desc) {
        log.info("income request of device - " + device + " DOWN");
        if (poolling.equals("down")) {
            Device dev = this.addDevice(device, group, desc);
            Devcapsule devcapsule = addDownDevcapsul(dev, date, time);

            if (devcapsule != null) {
                this.addTroubleToTroubleList(devcapsule, "current", this.systemUser);
            }

            if (this.getUpDevcList().containsKey(device)) {
                SimpleDateFormat format_date = new SimpleDateFormat();
                format_date.applyPattern("dd/MM/yyyy");
                SimpleDateFormat format_time = new SimpleDateFormat();
                format_time.applyPattern("HH:mm:ss");

                Date upDate = this.getUpDevcList().get(device);
                Date downDate = this.parse(date,time);

                log.info("+++++++++++++++++ find in up devc list - " + device);

                if (upDate.after(downDate)) {
                    try {
                        this.deviceUp("up", device, format_date.format(upDate),format_time.format(upDate));
                        this.getUpDevcList().remove(device);
                        log.info("--------------- size up devc list - " + this.getUpDevcList().size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.info("--------------- size up devc list - " + this.getUpDevcList().size());
            this.sortAndUpdatetroubleLists();
        }
    }

    public synchronized void deviceUp(String poolling, String device, String date, String time) throws IOException, JMSException, URISyntaxException {
        log.info("income request of device - " + device + " UP");
        if (poolling.equals("up")) {
            Device dev = this.getDevice(device);
            Date upDate = this.parse(date, time);
            List<Devcapsule> devcapsules = this.sortDevcapsulByTime(this.getDevcapsulesDownForDevice(dev));

            if (devcapsules.size() > 0) {
                for (Devcapsule devcapsule : devcapsules) {
                    addUpDevcapsul(devcapsule, upDate, "2");
                }
            } else if (devcapsules.size() == 0) {
                if (this.getUpDevcList().containsKey(device)) {
                    if (this.getUpDevcList().get(device).before(upDate)) {
                        this.getUpDevcList().put(device,upDate);
                        log.info("--------------- replace in up devc list - " + device);
                    }
                } else {
                    this.getUpDevcList().put(device,upDate);
                    log.info("--------------- put to up devc list - " + device);
                }
            }
            this.sortAndUpdatetroubleLists();
            log.info("--------------- size up devc list - " + this.getUpDevcList().size());
        }
    }*/
}