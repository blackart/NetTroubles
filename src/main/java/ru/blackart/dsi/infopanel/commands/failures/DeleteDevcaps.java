package ru.blackart.dsi.infopanel.commands.failures;

import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;
import ru.blackart.dsi.infopanel.utils.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

public class DeleteDevcaps extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    TroubleListService troubleListService = TroubleListService.getInstance();
    TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();

    @Override
    public String execute() throws Exception {
        int trouble_id = Integer.valueOf(this.getRequest().getParameter("id"));

        synchronized (dataModelConstructor) {
            synchronized (troubleService) {
                Trouble trouble = troubleService.get(trouble_id);
                TroubleList now_troubleList = dataModelConstructor.getTroubleListForTrouble(trouble);

                if ((!trouble.getClose()) && (trouble.getCrm())) {
                    CrmTrouble crmTrouble = new CrmTrouble(trouble, "3");
                    crmTrouble.send();
                }

                dataModelConstructor.moveTroubleList(trouble, now_troubleList, dataModelConstructor.getList_of_trash_troubles());

                synchronized (troubleListsManager) {
                    troubleListsManager.sortTroubleList(dataModelConstructor.getList_of_trash_troubles());
                }
            }
        }
        return null;
    }
}