package ru.blackart.dsi.infopanel.commands.troubles;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.ServiceService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.utils.crm.CrmComment;
import ru.blackart.dsi.infopanel.utils.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SendToCRM extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    ServiceService serviceService = ServiceService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {
        this.getRequest().setCharacterEncoding("UTF-8");
        BasicXmlData xml = new BasicXmlData("crm_message");

        /*-------------------------------------------------------------------------------------------*/
        String[] services = null;
        String services_str = this.getRequest().getParameter("service").trim().replace(" ", "");
        if ((services_str != null) && (!services_str.equals(""))) {
            services = services_str.split(";");
        }

        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        String title = this.getRequest().getParameter("title").trim();
        String actual_problem = this.getRequest().getParameter("actual_problem").replace("&nbsp;", "").trim();
        String timeout_str = this.getRequest().getParameter("timeout");

        log.info("Start sending trouble " + title + " [" + id + "] to CRM");
        /*-------------------------------------------------------------------------------------------*/

        synchronized (dataModelConstructor) {
            Trouble trouble = dataModelConstructor.getTroubleForId(id);
            TroubleList tList = dataModelConstructor.getTroubleListForTrouble(trouble);        //выясняем в какой очереде (листе) находится проблема
            log.info("The trouble [" + id + "] belongs to the " + tList.getName() + " trouble list");

            if (tList.getName().equals("current")) {
                String timeout = null;
                if ((timeout_str != null) && (!timeout_str.trim().equals(""))) {
                    String[] timeout_arr = timeout_str.split(" ");
                    timeout = String.valueOf(DateStr.parse(timeout_arr[0], timeout_arr[1]).getTime());
                }
                trouble.setTimeout(timeout);
            }

            trouble.setTitle(title);
            trouble.setActualProblem(actual_problem);
            trouble.setAuthor((User) this.getSession().getAttribute("info"));

            if ((services != null) && (services.length > 0)) {
                synchronized (serviceService) {
                    List<Service> service_ = new ArrayList<Service>();
                    for (String service1 : services) {
                        if (!service1.equals("")) {
                            Service service = serviceService.getService(Integer.valueOf(service1));
                            service_.add(service);
                        }
                    }
                    trouble.setServices(service_);
                }
            }
            synchronized (troubleService) {
                troubleService.update(trouble);                                                    //сохраняем в любом случае инфу о проблеме.
            }

            String status_crm;
            if (tList.getName().equals("current")) {
                status_crm = "1";                                                              //если очередь current - значит проблема ещё не разрешена, статус проблемы в CRM будет - 1 (активная проблема)
            } else {
                status_crm = "2";                                                              //в противном случае это будет очередь - waiting_close (проблема разрешена, но не отправлена в CRM), статус проблемы в CRM будет - 2 (проблема закрыта)
            }

            CrmTrouble crmTrouble = new CrmTrouble(trouble, status_crm);                       //создаём объект, который отправит в CRM проблему

            if (crmTrouble.validation()) {                                                     //вызываем метод validation() для проверки все ли поля проблемы корректно заполнены
                log.info("Preparing the trouble [" + id + "] to sending to CRM");
                Boolean send_to_crm = crmTrouble.send();                                       //отправляем проблему в CRM, если всё прошло успешно, метод возвратит true
                log.info("The trouble [" + id + "] posted to CRM");
                if (send_to_crm) {
                    trouble.setCrm(true);                                                      //отмечаем, что проблема отправлена в CRM

                    CommentService commentService = CommentService.getInstance();              //Далее работаем над отправкой комментариев к проблеме в CRM

                    synchronized (commentService) {
                        for (Comment comment : trouble.getComments()) {
                            CrmComment crmComment = new CrmComment(trouble, comment);
                            if (!comment.getCrm()) {                                               //если коммент ещё не отправлен в CRM
                                if (crmComment.send()) {                                           //отправляем коммент в CRM, если успешно, сохраняем это состояние в DB
                                    comment.setCrm(true);
                                    commentService.update(comment);
                                }
                            }
                        }
                    }

                    synchronized (troubleService) {
                        troubleService.update(trouble);                                            //сохраняем в DB инфу о проблеме
                    }

                    TroubleList targetTroubleList = dataModelConstructor.getTargetTroubleListForTrouble(trouble);
                    log.info("Target list for the trouble [" + id + "] is " + targetTroubleList.getName() + " trouble list");

                    if (tList.getId() != targetTroubleList.getId()) {
                        //перемещаем и сохраняем состояние обоих очередей в DB.
                        dataModelConstructor.moveTroubleList(trouble, tList, targetTroubleList);
                    }

                } else {
                    xml.addKid(new BasicXmlData("message", "Информация по проблеме заполнена верно, но при отправке в CRM возникла ошибка. Сообщите об этом разработчику."));
                }
                xml.addKid(new BasicXmlData("status", send_to_crm.toString()));
            } else {
                xml.addKid(new BasicXmlData("status", "false"));
                xml.addKid(new BasicXmlData("message", "Информация по проблеме заполнена не полностью. Проблема не отправлена в CRM."));
            }

            OutputStream out = getResponse().getOutputStream();
            xml.save(out);
        }

        return null;
    }
}
