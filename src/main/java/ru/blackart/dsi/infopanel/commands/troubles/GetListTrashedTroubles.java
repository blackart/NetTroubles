package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;

public class GetListTrashedTroubles extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModel) {
            TroubleList trash = dataModel.getList_of_trash_troubles();

            Gson gson = new Gson();
            this.getResponse().getWriter().print(gson.toJson(trash));
        }
        return null;
    }
}