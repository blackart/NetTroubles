package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.utils.message.CurrentTroubleListGroup;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

public class GetListsTroubles extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModelConstructor) {
            TroubleList current = dataModelConstructor.getList_of_current_troubles();
            TroubleList wait = dataModelConstructor.getList_of_waiting_close_troubles();
            TroubleList need = dataModelConstructor.getList_of_need_actual_problem();

            CurrentTroubleListGroup currentTroubleListGroup = new CurrentTroubleListGroup(current, wait, need);
            this.getResponse().getWriter().print(currentTroubleListGroup.toJson());
        }
        return null;
    }
}
