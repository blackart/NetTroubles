package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.ServiceService;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;
import ru.blackart.dsi.infopanel.utils.message.CompleteStatusMessage;

import java.util.ArrayList;
import java.util.List;

public class DeleteTrouble extends AbstractCommand {
    private DataModel dataModel = DataModel.getInstance();
    private ServiceService serviceService = ServiceService.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();

    @Override
    public String execute() throws Exception {
        int id = Integer.valueOf(this.getRequest().getParameter("id"));

        synchronized (dataModel) {
            Trouble trouble = dataModel.getTroubleForId(id);

            String timeout_str = this.getRequest().getParameter("timeout");
            String timeout = ((trouble.getTimeout() == null) || (trouble.getTimeout().trim().equals(""))) ? null : trouble.getTimeout();
            if ((timeout_str != null) && (!timeout_str.equals(""))) {
                String[] timeout_arr = timeout_str.split(" ");
                timeout = String.valueOf(DateStr.parse(timeout_arr[0], timeout_arr[1]).getTime());
            }

            String[] services = null;
            String services_str = this.getRequest().getParameter("service").trim().replace(" ", "");
            if ((services_str != null) && (!services_str.equals(""))) {
                services = this.getRequest().getParameter("service").trim().replace(" ", "").split(";");
            }

            String title = this.getRequest().getParameter("title").trim();
            String actual_problem = this.getRequest().getParameter("actual_problem").replace("&nbsp;","").trim();

            trouble.setAuthor((User) this.getSession().getAttribute("info"));
            trouble.setTitle(title);
            trouble.setActualProblem(actual_problem);
            trouble.setTimeout(timeout);

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
                troubleService.update(trouble);
            }

            synchronized (troubleListService) {

                if (trouble.getCrm()) {
                    CompleteStatusMessage completeStatusMessage = new CompleteStatusMessage();
                    /*---------------------------------------CRM ------------------------ */
                    CrmTrouble crmTrouble = new CrmTrouble(trouble, "3");
                    if (crmTrouble.send()) {
                        TroubleList now_troubleList = dataModel.getTroubleListForTrouble(trouble);
                        dataModel.moveTroubleList(trouble, now_troubleList, dataModel.getList_of_trash_troubles());
                        completeStatusMessage.setStatus(true);
                    } else {
                        completeStatusMessage.setStatus(false);
                        completeStatusMessage.setMessage("Информация по проблеме заполнена верно, но при отправке в CRM возникла ошибка. Сообщите об этом разработчику.");
                    }

                    this.getResponse().getWriter().print(completeStatusMessage.toJson());
                    /*-----------------------------------------------------------------*/
                } else {
                    TroubleList now_troubleList = dataModel.getTroubleListForTrouble(trouble);
                    dataModel.moveTroubleList(trouble, now_troubleList, dataModel.getList_of_trash_troubles());
                }
            }

            TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
            troubleListsManager.sortTroubleList(dataModel.getList_of_trash_troubles());
        }
        return null;
    }
}
