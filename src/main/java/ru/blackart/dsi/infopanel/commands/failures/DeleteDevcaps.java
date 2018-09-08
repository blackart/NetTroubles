package ru.blackart.dsi.infopanel.commands.failures;

import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

public class DeleteDevcaps extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    TroubleListService troubleListService = TroubleListService.getInstance();
    TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();

    @Override
    public String execute() throws Exception {
        int trouble_id = Integer.valueOf(this.getRequest().getParameter("id"));

        synchronized (dataModel) {
            synchronized (troubleService) {
                Trouble trouble = troubleService.get(trouble_id);
                TroubleList now_troubleList = dataModel.getTroubleListForTrouble(trouble);

                if ((!trouble.getClose()) && (trouble.getCrm())) {
                    CrmTrouble crmTrouble = new CrmTrouble(trouble, "3");
                    crmTrouble.send();
                }

                dataModel.moveTroubleList(trouble, now_troubleList, dataModel.getList_of_trash_troubles());

                synchronized (troubleListsManager) {
                    troubleListsManager.sortTroubleList(dataModel.getList_of_trash_troubles());
                }
            }
        }
        return null;
    }
}