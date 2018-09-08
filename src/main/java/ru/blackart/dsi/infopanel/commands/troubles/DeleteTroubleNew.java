package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
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
import ru.blackart.dsi.infopanel.view.TroubleView;

import java.util.ArrayList;
import java.util.List;

public class DeleteTroubleNew  extends AbstractCommand {
    private DataModel dataModel = DataModel.getInstance();
    private ServiceService serviceService = ServiceService.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();

    @Override
    public String execute() throws Exception {
        String troubleJSON = this.getRequest().getParameter("trouble");
        Gson gson = new Gson();
        TroubleView troubleView = gson.fromJson(troubleJSON, TroubleView.class);

        int id = troubleView.getId();

        synchronized (dataModel) {
            Trouble trouble = dataModel.getTroubleForId(id);

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
