package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.crm.CrmComment;
import ru.blackart.dsi.infopanel.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.ServiceService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.utils.message.CompleteStatusMessage;
import ru.blackart.dsi.infopanel.view.TroubleView;

import java.util.ArrayList;
import java.util.List;

public class SendToCRMNew extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    ServiceService serviceService = ServiceService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {
        this.getRequest().setCharacterEncoding("UTF-8");
        String troubleJSON = this.getRequest().getParameter("trouble");

        Gson gson = new Gson();
        TroubleView troubleView = gson.fromJson(troubleJSON, TroubleView.class);

        CompleteStatusMessage completeStatusMessage = new CompleteStatusMessage();

        synchronized (dataModel) {
            Trouble trouble = dataModel.getTroubleForId(troubleView.getId());
            TroubleList tList = dataModel.getTroubleListForTrouble(trouble);        //выясняем в какой очереде (листе) находится проблема

            log.info("The trouble [" + troubleView.getId() + "] belongs to the " + tList.getName() + " trouble list");

            String timeout;
            if (!troubleView.getTimeout().equals("")) {
                String[] timeout_arr = troubleView.getTimeout().split(" ");
                timeout = String.valueOf(DateStr.parse(timeout_arr[0], timeout_arr[1]).getTime());
            } else {
                timeout = null;
            }

            trouble.setTitle(troubleView.getTitle());
            trouble.setActualProblem(troubleView.getActualProblem());
            trouble.setTimeout(timeout);
            trouble.setAuthor((User) this.getSession().getAttribute("info"));
            trouble.setCrm(false);

            synchronized (serviceService) {
                List<Service> services_ = new ArrayList<Service>();
                int[] services = troubleView.getServices();

                for (int i=0; i < services.length; i++) {
                    Service service = serviceService.getService(services[i]);
                    services_.add(service);
                }
                trouble.setServices(services_);
            }

            synchronized (troubleService) {
                troubleService.update(trouble);
            }

            String status_crm;
            if (tList.getName().equals("current")) {
                status_crm = "1";                                                              //если очередь current - значит проблема ещё не разрешена, статус проблемы в CRM будет - 1 (активная проблема)
            } else {
                status_crm = "2";                                                              //в противном случае это будет очередь - waiting_close (проблема разрешена, но не отправлена в CRM), статус проблемы в CRM будет - 2 (проблема закрыта)
            }

            CrmTrouble crmTrouble = new CrmTrouble(trouble, status_crm);                       //создаём объект, который отправит в CRM проблему

            if (crmTrouble.validation()) {                                                     //вызываем метод validation() для проверки все ли поля проблемы корректно заполнены
                log.info("Preparing the trouble [" + troubleView.getId() + "] to sending to CRM");
                Boolean send_to_crm = crmTrouble.send();                                       //отправляем проблему в CRM, если всё прошло успешно, метод возвратит true
                log.info("The trouble [" + troubleView.getId() + "] posted to CRM");
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

                    TroubleList targetTroubleList = dataModel.getTargetTroubleListForTrouble(trouble);
                    log.info("Target list for the trouble [" + troubleView.getId() + "] is " + targetTroubleList.getName() + " trouble list");

                    if (tList.getId() != targetTroubleList.getId()) {
                        //перемещаем и сохраняем состояние обоих очередей в DB.
                        dataModel.moveTroubleList(trouble, tList, targetTroubleList);
                    }

                } else {
                    completeStatusMessage.setMessage("Информация по проблеме заполнена верно, но при отправке в CRM возникла ошибка. Сообщите об этом разработчику.");
                }
                completeStatusMessage.setStatus(send_to_crm);
            } else {
                completeStatusMessage.setStatus(false);
                completeStatusMessage.setMessage("Информация по проблеме заполнена не полностью. Проблема не отправлена в CRM.");
            }
        }

        this.getResponse().getWriter().write(completeStatusMessage.toJson());

        return null;
    }
}
