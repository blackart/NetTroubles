package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.utils.TroubleListsManager;
import ru.blackart.dsi.infopanel.utils.crm.CrmTrouble;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

public class RecoveryTrouble extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
    TroubleListService troubleListService = TroubleListService.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModelConstructor) {
            int id = Integer.valueOf(this.getRequest().getParameter("id"));

            Trouble trouble = dataModelConstructor.getTroubleForId(id);

            TroubleList troubleListTarget = dataModelConstructor.getTargetTroubleListForTrouble(trouble);
            TroubleList troubleListSource = dataModelConstructor.getTroubleListForTrouble(trouble);

            dataModelConstructor.moveTroubleList(trouble, troubleListSource, troubleListTarget);

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
