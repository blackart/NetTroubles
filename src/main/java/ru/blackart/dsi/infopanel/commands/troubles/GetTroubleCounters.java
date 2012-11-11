package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.message.TroublesCounter;

public class GetTroubleCounters extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModel) {
            int current_troubles_count = dataModel.getList_of_current_troubles().getTroubles().size();
            int waiting_close_troubles_count = dataModel.getList_of_waiting_close_troubles().getTroubles().size();
            int close_troubles_count = dataModel.getList_of_complete_troubles().getTroubles().size();
            int trash_troubles_count = dataModel.getList_of_trash_troubles().getTroubles().size();
            int need_actual_problem_troubles_count = dataModel.getList_of_need_actual_problem().getTroubles().size();

            TroublesCounter troublesCounter = new TroublesCounter(
                    current_troubles_count,
                    waiting_close_troubles_count,
                    close_troubles_count,
                    trash_troubles_count,
                    need_actual_problem_troubles_count
            );

            this.getResponse().getWriter().print(troublesCounter.toJson());
        }

        return null;
    }
}
