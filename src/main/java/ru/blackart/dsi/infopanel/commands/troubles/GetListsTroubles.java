package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.message.CurrentTroubleListGroup;

public class GetListsTroubles extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModel) {
            TroubleList current = dataModel.getList_of_current_troubles();
            TroubleList wait = dataModel.getList_of_waiting_close_troubles();
            TroubleList need = dataModel.getList_of_need_actual_problem();

            CurrentTroubleListGroup currentTroubleListGroup = new CurrentTroubleListGroup(current, wait, need);
            this.getResponse().getWriter().print(currentTroubleListGroup.toJson());
        }
        return null;
    }
}
