package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;

public class RecoveryTrouble extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();
    TroubleListService troubleListService = TroubleListService.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModel) {
            int id = Integer.valueOf(this.getRequest().getParameter("id"));

            Trouble trouble = dataModel.getTroubleForId(id);

            TroubleList troubleListTarget = dataModel.getTargetTroubleListForTrouble(trouble);
            TroubleList troubleListSource = dataModel.getTroubleListForTrouble(trouble);

            dataModel.moveTroubleList(trouble, troubleListSource, troubleListTarget);

            if ((!trouble.getClose()) && (trouble.getCrm())) {
                CrmTrouble crmTrouble = new CrmTrouble(trouble, "3");
                crmTrouble.send();
            } else if (trouble.getCrm() && trouble.getClose()) {
                CrmTrouble crmTrouble = new CrmTrouble(trouble, "2");
                crmTrouble.send();
            }

            TroubleListsManager troubleListsManager = TroubleListsManager.getInstance();
            synchronized (troubleListsManager) {
                synchronized (troubleListTarget) {
                    troubleListsManager.sortTroubleList(troubleListTarget);
                }
                synchronized (troubleListSource) {
                    troubleListsManager.sortTroubleList(troubleListSource);
                }
            }
        }
        return null;
    }
}
