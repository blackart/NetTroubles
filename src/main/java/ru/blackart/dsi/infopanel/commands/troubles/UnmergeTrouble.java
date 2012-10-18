package ru.blackart.dsi.infopanel.commands.troubles;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.*;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.DevcapsuleService;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;
import ru.blackart.dsi.infopanel.utils.crm.CrmComment;
import ru.blackart.dsi.infopanel.utils.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class UnmergeTrouble extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
    TroubleListService troubleListService = TroubleListService.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    CommentService commentService = CommentService.getInstance();

    @Override
    public String execute() throws Exception {
        String id_devc = this.getRequest().getParameter("id_devc");

        if ((id_devc != null) && (!id_devc.trim().equals(""))) {
            synchronized (dataModelConstructor) {
                synchronized (devcapsuleService) {
                    Devcapsule devcapsule = devcapsuleService.getDevcapsule(Integer.valueOf(id_devc));
                    Trouble trouble = dataModelConstructor.getTroubleForDevcapsule(devcapsule);
                    TroubleList troubleList = dataModelConstructor.getTroubleListForTrouble(trouble);
                    String list_name = troubleList.getName();

                    int index = -1;
                    for (Devcapsule d : trouble.getDevcapsules()) {
                        if (d.getId() == devcapsule.getId()) {
                            index = trouble.getDevcapsules().indexOf(d);
                        }
                    }
                    trouble.getDevcapsules().remove(index);

                    Date time_down_min = new Date(Long.valueOf(trouble.getDevcapsules().get(0).getTimedown()));
                    for (Devcapsule devc : trouble.getDevcapsules()) {
                        Date time_down_another = new Date(Long.valueOf(devc.getTimedown()));
                        if (time_down_min.after(time_down_another)) {
                            time_down_min = time_down_another;
                        }
                    }
                    trouble.setDate_in(String.valueOf(time_down_min.getTime()));

                    synchronized (troubleService) {
                        troubleService.update(trouble);
                    }

                    /*--------------------------------------------------------------------------------------------------------*/
                    boolean complete = true;
                    for (Devcapsule d : trouble.getDevcapsules()) {
                        complete = complete && d.getComplete();
                    }

                    synchronized (commentService) {
                        if (complete && (list_name.equals("current") || list_name.equals("need_actual_problem"))) {
                            trouble.setClose(true);
                            trouble.setDate_out(dataModelConstructor.sortDevcapsuleByTime(trouble.getDevcapsules()).get(0).getTimeup());

                            if (trouble.getCrm()) {
                                CrmTrouble crmTrouble = new CrmTrouble(trouble, "2");
                                if (crmTrouble.validation()) {
                                    Boolean send_to_crm = crmTrouble.send();
                                    if (send_to_crm) {
                                        for (Comment comment : trouble.getComments()) {                           //Далее работаем над отправкой комментариев к проблеме в CRM
                                            CrmComment crmComment = new CrmComment(trouble, comment);
                                            if (!comment.getCrm()) {                                               //если коммент ещё не отправлен в CRM
                                                if (crmComment.send()) {                                           //отправляем коммент в CRM, если успешно, сохраняем это состояние в DB
                                                    comment.setCrm(true);
                                                    commentService.update(comment);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (!complete && trouble.getCrm() && list_name.equals("current")) {
                            CrmTrouble crmTrouble = new CrmTrouble(trouble, "1");
                            if (crmTrouble.validation()) {
                                Boolean send_to_crm = crmTrouble.send();
                                if (send_to_crm) {
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
                            }
                        }
                    }

                    synchronized (troubleService) {
                        troubleService.update(trouble);
                    }

                    TroubleList targetTroubleList = dataModelConstructor.getTargetTroubleListForTrouble(trouble);
                    dataModelConstructor.moveTroubleList(trouble, troubleList, targetTroubleList);

                    /*--------------------------------------------------------------------------------------------------------*/

                    Trouble new_trouble = new Trouble();
                    new_trouble.setDevcapsules(new ArrayList<Devcapsule>());
                    new_trouble.getDevcapsules().add(devcapsule);
                    new_trouble.setTitle(devcapsule.getDevice().getName() + ", " + devcapsule.getDevice().getDescription());
                    new_trouble.setDate_in(devcapsule.getTimedown());
                    new_trouble.setAuthor((User) this.getSession().getAttribute("info"));
                    new_trouble.setComments(new ArrayList<Comment>());
                    new_trouble.setActualProblem("");
                    new_trouble.setCrm(false);

                    if (devcapsule.getComplete()) {
                        new_trouble.setDate_out(devcapsule.getTimeup());
                        new_trouble.setTimeout(devcapsule.getTimeup());
                        new_trouble.setClose(true);
                    } else {
                        new_trouble.setClose(false);
                    }

                    troubleList = dataModelConstructor.getTargetTroubleListForTrouble(new_trouble);

                    synchronized (troubleService) {
                        troubleService.save(new_trouble);
                    }
                    troubleList.getTroubles().add(new_trouble);
                    log.info("The trouble " + new_trouble.getTitle() + " added to " + troubleList.getName() + " trouble list.");

                    synchronized (troubleListService) {
                        troubleListService.update(troubleList);
                    }

                    BasicXmlData xml = new BasicXmlData("trouble");
                    BasicXmlData xml_level_1 = new BasicXmlData("trouble_merge");
                    if (list_name.equals("current")) {
                        xml_level_1.addKid(new BasicXmlData("return_", "1"));
                    } else if (list_name.equals("complete")) {
                        xml_level_1.addKid(new BasicXmlData("return_", "2"));
                    }

                    xml.addKid(xml_level_1);

                    OutputStream out = getResponse().getOutputStream();
                    xml.save(out);

                    TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
                    troubleListsManager.sortTroubleList(dataModelConstructor.getTroubleListForName(list_name));
                }
            }
        }
        return null;
    }
}
