package ru.blackart.dsi.infopanel.model;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.services.DeviceManager;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static DataModel dataModel;
    private DeviceManager deviceManager = DeviceManager.getInstance();
    private TroubleList list_of_current_troubles;
    private TroubleList list_of_complete_troubles;
    private TroubleList list_of_waiting_close_troubles;
    private TroubleList list_of_trash_troubles;
    private TroubleList list_of_need_actual_problem;
    private SessionFactory sessionFactory;
    private List<TroubleList> troubleLists = new ArrayList<TroubleList>();

    public synchronized TroubleList getList_of_current_troubles() {
        return list_of_current_troubles;
    }

    public synchronized TroubleList getList_of_complete_troubles() {
        return list_of_complete_troubles;
    }

    public synchronized TroubleList getList_of_waiting_close_troubles() {
        return list_of_waiting_close_troubles;
    }

    public synchronized TroubleList getList_of_trash_troubles() {
        return list_of_trash_troubles;
    }

    public synchronized TroubleList getList_of_need_actual_problem() {
        return list_of_need_actual_problem;
    }

    public List<TroubleList> getTroubleLists() {
        return troubleLists;
    }

    public TroubleList loadTroubleList(String name, Session session) {
        //загрузка всех текущих траблов в объект
        session.beginTransaction();

        Criteria crt_curr_trouble = session.createCriteria(TroubleList.class);
        crt_curr_trouble.add(Restrictions.eq("name", name));

        TroubleList troubleList = (TroubleList) crt_curr_trouble.list().get(0);

        List<Trouble> troubles = troubleList.getTroubles();

        for (Trouble trouble : troubles) {
            trouble.getComments();

            List<Devcapsule> devcapsules = trouble.getDevcapsules();
            for (Devcapsule devcapsule : devcapsules) {
                Device device = devcapsule.getDevice();
                devcapsule.setDevice(deviceManager.getDevice(device));
            }
            devcapsules.size();

            List<Service> services = trouble.getServices();
            services.size();
        }

        this.troubleLists.add(troubleList);

        session.flush();
        session.close();

        return troubleList;
    }

    public static synchronized DataModel getInstance() {
        if (dataModel == null) {
            dataModel = new DataModel();
            TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();

            dataModel.sessionFactory = SessionFactorySingle.getSessionFactory();

            dataModel.list_of_current_troubles = dataModel.loadTroubleList("current", dataModel.sessionFactory.openSession());
            dataModel.errorCorrectionComplianceTroublesAndList(dataModel.list_of_current_troubles);
            troubleListsManager.setCurrTroubleList(dataModel.list_of_current_troubles);
            troubleListsManager.setTroubleListForCallCenter(dataModel.list_of_current_troubles);


            dataModel.list_of_complete_troubles = dataModel.loadTroubleList("complete", dataModel.sessionFactory.openSession());
            troubleListsManager.setClosedTroubleList(dataModel.list_of_complete_troubles);
            dataModel.list_of_waiting_close_troubles = dataModel.loadTroubleList("waiting_close", dataModel.sessionFactory.openSession());
            dataModel.errorCorrectionComplianceTroublesAndList(dataModel.list_of_waiting_close_troubles);
            troubleListsManager.setWaitingCloseTroubleList(dataModel.list_of_waiting_close_troubles);
            dataModel.list_of_need_actual_problem = dataModel.loadTroubleList("need_actual_problem", dataModel.sessionFactory.openSession());
            dataModel.errorCorrectionComplianceTroublesAndList(dataModel.list_of_need_actual_problem);
            troubleListsManager.setNeedActualProblemTroubleList(dataModel.list_of_need_actual_problem);
            dataModel.list_of_trash_troubles = dataModel.loadTroubleList("trash", dataModel.sessionFactory.openSession());
            troubleListsManager.setTrashTroubleList(dataModel.list_of_trash_troubles);

        }

        return dataModel;
    }

/*------------------------------------------------------------------------------*/
/*-----------------------------TroubleList--------------------------------------*/
/*------------------------------------------------------------------------------*/

    public TroubleList getTroubleListForTrouble(Trouble trouble) {
        return this.getTroubleListForTroubleId(trouble.getId());
    }

    public TroubleList getTroubleListForTroubleId(int id) {
        TroubleList tr_list = null;

        for (Trouble t : this.getList_of_need_actual_problem().getTroubles()) {
            if (t.getId() == id) {
                return this.getList_of_need_actual_problem();
            }
        }
        for (Trouble t : this.getList_of_current_troubles().getTroubles()) {
            if (t.getId() == id) {
                return this.getList_of_current_troubles();
            }
        }
        for (Trouble t : this.getList_of_waiting_close_troubles().getTroubles()) {
            if (t.getId() == id) {
                return this.getList_of_waiting_close_troubles();
            }
        }
        for (Trouble t : this.getList_of_trash_troubles().getTroubles()) {
            if (t.getId() == id) {
                return this.getList_of_trash_troubles();
            }
        }
        for (Trouble t : this.getList_of_complete_troubles().getTroubles()) {
            if (t.getId() == id) {
                return this.getList_of_complete_troubles();
            }
        }

        return tr_list;
    }

    public TroubleList getTroubleListForName(String troubleListName) {
        TroubleList t_list = null;
        if (troubleListName.equals("current")) {
            t_list = this.getList_of_current_troubles();
        } else if (troubleListName.equals("complete")) {
            t_list = this.getList_of_complete_troubles();
        } else if (troubleListName.equals("waiting_close")) {
            t_list = this.getList_of_waiting_close_troubles();
        } else if (troubleListName.equals("trash")) {
            t_list = this.getList_of_trash_troubles();
        } else if (troubleListName.equals("need_actual_problem")) {
            t_list = this.getList_of_need_actual_problem();
        }

        return t_list;
    }

    public void errorCorrectionComplianceTroublesAndList(TroubleList troubleList) {

        List<Trouble> needMoveTroubles = new ArrayList();
        for (Trouble trouble : troubleList.getTroubles()) {
            try{
            if (!this.checkComplianceTroubleAndList(trouble, troubleList)) {
                needMoveTroubles.add(trouble);
            }
            }catch (Exception ex){
                int a =1;
            }
        }
        for (Trouble trouble : needMoveTroubles) {
            try{
            TroubleList targetTroubleList = this.getTargetTroubleListForTrouble(trouble);
            this.moveTroubleList(trouble, troubleList, targetTroubleList);
            }catch (Exception ex){
                int a =1;
            }
        }

    }

    public Boolean checkComplianceTroubleAndList(Trouble trouble) {
        TroubleList sourceTroubleList = this.getTroubleListForTrouble(trouble);
        return this.checkComplianceTroubleAndList(trouble, sourceTroubleList);
    }

    public boolean checkComplianceTroubleAndList(Trouble trouble, TroubleList sourceTroubleList) {
        TroubleList targetTroubleList = this.getTargetTroubleListForTrouble(trouble);
        if (targetTroubleList == null) {
            return false;
        } else {
            return targetTroubleList.getId() == sourceTroubleList.getId();
        }
    }

    public TroubleList getTargetTroubleListForTrouble(Trouble trouble) {
        boolean complete = true;
        boolean wait = true;
        boolean current = false;
        boolean need_actual = true;

        boolean complete_devc = true;
        boolean actual_problem_empty;

        for (Devcapsule devcap : trouble.getDevcapsules()) {
            complete_devc = complete_devc && devcap.getComplete();
        }

        if (trouble.getActualProblem() == null) {
            actual_problem_empty = true;
        } else {
            if (trouble.getActualProblem().trim().equals("")) {
                actual_problem_empty = true;
            } else {
                actual_problem_empty = false;
            }
        }

        wait = wait && (complete_devc && trouble.getClose() && !trouble.getCrm());
        complete = complete && (complete_devc && trouble.getClose() && trouble.getCrm() && !actual_problem_empty);
        need_actual = need_actual && (complete_devc && trouble.getClose() && trouble.getCrm() && actual_problem_empty);
        current = current || !complete_devc;

        TroubleList troubleList = null;
        if (complete) {
            troubleList = this.getList_of_complete_troubles();
        } else if (wait) {
            troubleList = this.getList_of_waiting_close_troubles();
        } else if (current) {
            troubleList = this.getList_of_current_troubles();
        } else if (need_actual) {
            troubleList = this.getList_of_need_actual_problem();
        }

        return troubleList;
    }

/*------------------------------------------------------------------------------*/
/*----------------------------Move Trouble List---------------------------------*/
/*------------------------------------------------------------------------------*/

    public void moveTroubleList(Trouble trouble, String sourceTroubleList, String targetTroubleList) {
        this.moveTroubleList(trouble, this.getTroubleListForName(sourceTroubleList), this.getTroubleListForName(targetTroubleList));
    }

    public void moveTroubleList(Trouble trouble, TroubleList sourceTroubleList, TroubleList targetTroubleList) {
        TroubleListService troubleListService = TroubleListService.getInstance();

        if (sourceTroubleList.getId() != targetTroubleList.getId()) {
            int index = -1;
            for (Trouble t : sourceTroubleList.getTroubles()) {
                if (t.getId() == trouble.getId()) {
                    index = sourceTroubleList.getTroubles().indexOf(t);
                }
            }
            sourceTroubleList.getTroubles().remove(index);
            targetTroubleList.getTroubles().add(trouble);
            synchronized (troubleListService) {
                troubleListService.update(sourceTroubleList);
                troubleListService.update(targetTroubleList);
            }
            log.info("The trouble " + trouble.getTitle() + " [" + trouble.getId() + "] moved from " + sourceTroubleList.getName() + " to " + targetTroubleList.getName() + " trouble list");
        }
    }

/*------------------------------------------------------------------------------*/
/*--------------------------------Trouble---------------------------------------*/
/*------------------------------------------------------------------------------*/

    public Trouble getTroubleForDevcapsuleInCustomTroubleList(Devcapsule devcapsule, String troubleList) {
        return this.getTroubleForDevcapsuleInCustomTroubleList(devcapsule, this.getTroubleListForName(troubleList));
    }

    public synchronized Trouble getTroubleForDevcapsuleInCustomTroubleList(Devcapsule devcapsule, TroubleList troubleList) {
        Trouble trouble = null;

        for (Trouble t : troubleList.getTroubles()) {
            for (Devcapsule d : t.getDevcapsules()) {
                if (d.getId() == devcapsule.getId()) {
                    trouble = t;
                }
            }
        }

        return trouble;
    }

    public Trouble getTroubleForDevcapsule(Devcapsule devcapsule) {
        Trouble trouble = null;
        for (TroubleList troubleList : this.getTroubleLists()) {
            if (trouble == null) trouble = this.getTroubleForDevcapsuleInCustomTroubleList(devcapsule, troubleList);
        }
        return trouble;
    }

    public Trouble getTroubleForId(int id) {
        for (Trouble t : this.getList_of_need_actual_problem().getTroubles()) {
            if (t.getId() == id) {
                return t;
            }
        }
        for (Trouble t : this.getList_of_current_troubles().getTroubles()) {
            if (t.getId() == id) {
                return t;
            }
        }
        for (Trouble t : this.getList_of_complete_troubles().getTroubles()) {
            if (t.getId() == id) {
                return t;
            }
        }
        for (Trouble t : this.getList_of_waiting_close_troubles().getTroubles()) {
            if (t.getId() == id) {
                return t;
            }
        }
        for (Trouble t : this.getList_of_trash_troubles().getTroubles()) {
            if (t.getId() == id) {
                return t;
            }
        }

        return null;
    }

/*------------------------------------------------------------------------------*/
/*--------------------------------Devcapsule------------------------------------*/
/*------------------------------------------------------------------------------*/

    public synchronized List<Devcapsule> getDevcWithOpenUpDateForDevice(Device device) {
        List<Devcapsule> return_arr = new ArrayList<Devcapsule>();

        for (Trouble trouble : this.getList_of_current_troubles().getTroubles()) {
            for (Devcapsule devcapsule : trouble.getDevcapsules()) {
                boolean ok = false;

                if (devcapsule.getDevice().getId() == device.getId()) {
                    if (devcapsule.getTimedown() != null) {
                        if (!devcapsule.getTimedown().equals("")) {
                            ok = true;
                        }
                    }

                    if ((devcapsule.getTimeup() == null) || (devcapsule.getTimeup().equals(""))) {
                        ok = ok && true;
                    }
                }

                if (ok) {
                    return_arr.add(devcapsule);
                }
            }
        }
        return return_arr;
    }

    public synchronized List<Devcapsule> sortDevcapsuleByTime(List<Devcapsule> dev) {
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

    public synchronized List<Devcapsule> sortDevcapsuleByUpTime(List<Devcapsule> dev) {
        for (int i = 0; i < dev.size(); i++) {
            for (int j = 0; j < i; j++) {
                Long dev_time_i = Long.valueOf(dev.get(i).getTimeup());
                Long dev_time_j = Long.valueOf(dev.get(j).getTimeup());

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
