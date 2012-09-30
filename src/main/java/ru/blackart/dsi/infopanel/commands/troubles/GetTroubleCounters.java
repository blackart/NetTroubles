package ru.blackart.dsi.infopanel.commands.troubles;

import com.myjavatools.xml.BasicXmlData;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.io.OutputStream;

public class GetTroubleCounters extends AbstractCommand {
    DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModelConstructor) {
            int current_troubles_count = dataModelConstructor.getList_of_current_troubles().getTroubles().size();
            int waiting_close_troubles_count = dataModelConstructor.getList_of_waiting_close_troubles().getTroubles().size();
            int close_troubles_count = dataModelConstructor.getList_of_complete_troubles().getTroubles().size();
            int trash_troubles_count = dataModelConstructor.getList_of_trash_troubles().getTroubles().size();
            int need_actual_problem_troubles_count = dataModelConstructor.getList_of_need_actual_problem().getTroubles().size();

            BasicXmlData xml = new BasicXmlData("logEntry");

            xml.addKid(new BasicXmlData("current", String.valueOf(current_troubles_count)));
            xml.addKid(new BasicXmlData("waiting_close", String.valueOf(waiting_close_troubles_count)));
            xml.addKid(new BasicXmlData("close", String.valueOf(close_troubles_count)));
            xml.addKid(new BasicXmlData("trash", String.valueOf(trash_troubles_count)));
            xml.addKid(new BasicXmlData("need_actual_problem", String.valueOf(need_actual_problem_troubles_count)));

            OutputStream out = this.getResponse().getOutputStream();
            xml.save(out);
        }

        return null;
    }
}
