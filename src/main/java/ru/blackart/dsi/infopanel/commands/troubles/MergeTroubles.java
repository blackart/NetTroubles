package ru.blackart.dsi.infopanel.commands.troubles;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.*;
import ru.blackart.dsi.infopanel.utils.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class MergeTroubles extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();

    /**
     * Отправка XML на сторону клиента в ответ на запрос.
     * @param prop параметр
     * @param value свойство
     * @throws IOException
     */
    private void sendXMLResponse(String prop, String value) throws IOException {
        //----------------------------Send XML to Client side------------------------------
        BasicXmlData xml = new BasicXmlData("trouble");
        BasicXmlData xml_level_1 = new BasicXmlData("trouble_merge");

        xml_level_1.addKid(new BasicXmlData(prop, value));
        xml.addKid(xml_level_1);

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);
        //----------------------------Send XML to Client side------------------------------
    }

    /**
     * Отправка XML на сторону клиента в ответ на запрос.
     * @param props набор свойств и значений
     * @throws IOException
     */
    private void sendXMLResponse(Properties  props) throws IOException {
        BasicXmlData xml = new BasicXmlData("trouble");
        BasicXmlData xml_level_1 = new BasicXmlData("trouble_merge");

        Enumeration enumeration = props.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            xml_level_1.addKid(new BasicXmlData(key, (String)props.get(key)));
        }
        xml.addKid(xml_level_1);

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);
    }

    /**
     * Поиск проблем в очередях
     * @param ids массив id проблем
     * @return массив объектов проблем
     */
    private Properties checkTroubles(String[] ids) {
        Properties troubles_troubleList = new Properties();

        for (String anIds_arr : ids) {
            boolean find = false;

            for (Trouble t : dataModelConstructor.getList_of_need_actual_problem().getTroubles()) {
                if (t.getId() == Integer.valueOf(anIds_arr)) {
                    troubles_troubleList.put(t, dataModelConstructor.getList_of_need_actual_problem());
                    find = true;
                }
                if (find) break;
            }
            if (!find) {
                for (Trouble t : dataModelConstructor.getList_of_current_troubles().getTroubles()) {
                    if (t.getId() == Integer.valueOf(anIds_arr)) {
                        troubles_troubleList.put(t, dataModelConstructor.getList_of_current_troubles());
                        find = true;
                    }
                    if (find) break;
                }
            }
            if (!find) {
                for (Trouble t : dataModelConstructor.getList_of_waiting_close_troubles().getTroubles()) {
                    if (t.getId() == Integer.valueOf(anIds_arr)) {
                        troubles_troubleList.put(t, dataModelConstructor.getList_of_waiting_close_troubles());
                        find = true;
                    }
                    if (find) break;
                }
            }
            if (!find) {
                for (Trouble t : dataModelConstructor.getList_of_complete_troubles().getTroubles()) {
                    if (t.getId() == Integer.valueOf(anIds_arr)) {
                        troubles_troubleList.put(t, dataModelConstructor.getList_of_complete_troubles());
                        find = true;
                    }
                    if (find) break;
                }
            }
        }

        return troubles_troubleList;
    }

    /**
     * Объединение нескольких проблем в одну
     * @return null
     * @throws Exception
     */
    public String execute() throws Exception {
        String ids = this.getRequest().getParameter("id");
        String title = this.getRequest().getParameter("title");
        String actual_problem = this.getRequest().getParameter("actual_problem").replace("&nbsp;","").trim();
        String service = this.getRequest().getParameter("service");

        String[] ids_arr = ids.trim().split(";");
        String[] ids_service = service.split(";");

        boolean complete = false;
        boolean wait = false;
        boolean current = false;
        boolean need_actual = false;

        List<Devcapsule> devc = new ArrayList<Devcapsule>();
        List<Comment> comments = new ArrayList<Comment>();

        synchronized (dataModelConstructor) {

            Properties troubles_troubleList = this.checkTroubles(ids_arr);

            if (troubles_troubleList.size() == 0) {
                Properties prop = new Properties();
                prop.put("return_", "1");
                prop.put("message", "Указанных проблем не существует.");
                this.sendXMLResponse(prop);
                return null;
            }

            Boolean send_to_crm = true;

            Enumeration enu = troubles_troubleList.keys();
            while (enu.hasMoreElements()) {
                Trouble trouble = (Trouble) enu.nextElement();
                boolean complete_devc = true;

                for (Devcapsule devcap : trouble.getDevcapsules()) {
                    complete_devc = complete_devc && devcap.getComplete();
                }

                TroubleList troubleList_for_trouble = (TroubleList)troubles_troubleList.get(trouble);

                current = current || troubleList_for_trouble.getName().equals("current");
                wait = wait || troubleList_for_trouble.getName().equals("waiting_close");
                need_actual = need_actual || troubleList_for_trouble.getName().equals("need_actual_problem");
                complete = complete || troubleList_for_trouble.getName().equals("complete");

                if ((trouble.getCrm() && (!complete_devc)) || troubleList_for_trouble.getName().equals("need_actual_problem")) {
                    CrmTrouble crmTrouble = new CrmTrouble(trouble, "3");                  //3 - сделать проблему неактивной в CRM.
                    send_to_crm = send_to_crm && crmTrouble.send();
                }
            }

            if (send_to_crm) {
                enu = troubles_troubleList.keys();

                //перебираем все "склеиваемые" проблемы и удаляем их из DB
                while (enu.hasMoreElements()) {
                    Trouble trouble = (Trouble) enu.nextElement();
                    TroubleList tr_list = (TroubleList) troubles_troubleList.get(trouble);

                    tr_list.getTroubles().remove(trouble);

                    devc.addAll(trouble.getDevcapsules());
                    comments.addAll(trouble.getComments());

                    synchronized (troubleService) {
                        trouble = troubleService.get(trouble.getId());

                        trouble.setComments(new ArrayList<Comment>());
                        trouble.setDevcapsules(new ArrayList<Devcapsule>());
                        trouble.setServices(new ArrayList<Service>());

                        troubleService.update(trouble);

                        ArrayList<Trouble> tr = new ArrayList<Trouble>(tr_list.getTroubles());
                        tr_list.setTroubles(tr);
                        synchronized (troubleListService) {
                            troubleListService.update(tr_list);
                        }

                        troubleService.delete(trouble);
                    }
                }

                TroubleList troubleList = null;
                if (current) {
                    troubleList = dataModelConstructor.getList_of_current_troubles();
                    current = true;
                    wait = false;
                    need_actual = false;
                    complete = false;
                } else if (wait) {
                    troubleList = dataModelConstructor.getList_of_waiting_close_troubles();
                    current = false;
                    wait = true;
                    need_actual = false;
                    complete = false;
                } else if (need_actual) {
                    troubleList = dataModelConstructor.getList_of_need_actual_problem();
                    current = false;
                    wait = false;
                    need_actual = true;
                    complete = false;
                } else if (complete) {
                    troubleList = dataModelConstructor.getList_of_complete_troubles();
                    current = false;
                    wait = false;
                    need_actual = false;
                    complete = true;
                }

                Date date_in_min = new Date();
                Date date_out_max = new Date();

                for (Devcapsule d : devc) {
                    Date date_in_another = new Date(Long.valueOf(d.getTimedown()));
                    if (date_in_min.after(date_in_another)) {
                        date_in_min = date_in_another;
                    }
                    if (!current) {
                        Date date_out_another = new Date(Long.valueOf(d.getTimeup()));
                        if (date_out_max.before(date_out_another)) {
                            date_out_max = date_out_another;
                        }
                    }
                }

                List<Devcapsule> devc_for_db = new ArrayList<Devcapsule>();
                DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();

                synchronized (devcapsuleService) {
                    for (Devcapsule d : devc) {
                        devc_for_db.add(devcapsuleService.getDevcapsule(d));
                    }
                }

                List<Comment> comments_for_db = new ArrayList<Comment>();
                CommentService commentService = CommentService.getInstance();

                synchronized (commentService) {
                    for (Comment c : comments) {
                        comments_for_db.add(commentService.getComment(c));
                    }
                }

                List<Service> service_for_db = new ArrayList<Service>();
                ServiceService serviceService = ServiceService.getInstance();

                synchronized (serviceService) {
                    if (ids_service.length > 0) {
                        for (String anIds_service : ids_service) {
                            try {
                                if (!anIds_service.equals("")) service_for_db.add(serviceService.getService(Integer.valueOf(anIds_service)));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                //----------------------------New Trouble------------------------------------------
                Trouble new_trouble = new Trouble();

                new_trouble.setAuthor((User) this.getSession().getAttribute("info"));

                new_trouble.setDevcapsules(devc_for_db);
                new_trouble.setServices(service_for_db);
                new_trouble.setComments(comments_for_db);

                new_trouble.setClose(complete || wait || need_actual);
                new_trouble.setCrm(complete || need_actual);

                new_trouble.setDate_in(String.valueOf(date_in_min.getTime()));
                new_trouble.setDate_out(!current ? String.valueOf(date_out_max.getTime()) : null);
                new_trouble.setTimeout(!current ? String.valueOf(date_out_max.getTime()) : null);

                new_trouble.setActualProblem(actual_problem);
                new_trouble.setTitle(title);

                synchronized (troubleService) {
                    troubleService.save(new_trouble);
                }

                synchronized (troubleList) {
                    troubleList.getTroubles().add(new_trouble);
                }

                synchronized (troubleListService) {
                    troubleListService.update(troubleList);
                }

                //----------------------------New Trouble------------------------------------------

                if (current || wait) {
                    this.sendXMLResponse("return_", "1");
                } else if (complete) {
                    this.sendXMLResponse("return_", "2");
                }

            }
        }
        return null;
    }
}
