package ru.blackart.dsi.infopanel.commands.reports;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.searching.SearchParam;
import ru.blackart.dsi.infopanel.utils.searching.SearchingForStatus;

import java.util.List;

public class GetTroubleReport extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String searchParam_str = this.getRequest().getParameter("param");

        Gson gson = new Gson();
        SearchParam searchParam = gson.fromJson(searchParam_str, SearchParam.class);

        if (searchParam.getStartSearchDate() == null) searchParam.setStartSearchDate(Long.valueOf(0));
        if (searchParam.getEndSearchDate() == null) searchParam.setEndSearchDate(System.currentTimeMillis());

        SearchingForStatus searchingForStatus = new SearchingForStatus(searchParam.getHostStatuses());
        List<Devcapsule> devcapsules = searchingForStatus.find();
        devcapsules = DataModel.getInstance().sortDevcapsuleByTime(devcapsules);

        if (devcapsules != null) {

        }

        return null;
    }
}
