package ru.blackart.dsi.infopanel.commands.reports;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.searching.SearchParam;
import ru.blackart.dsi.infopanel.utils.searching.SearchingForName;
import ru.blackart.dsi.infopanel.utils.searching.SearchingForStatus;

import java.util.List;

public class GetTroubleReport extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String searchParam_str = this.getRequest().getParameter("param");

        Gson gson = new Gson();
        SearchParam searchParam = gson.fromJson(searchParam_str, SearchParam.class);


        List<Devcapsule> devcapsules = null;

        if (!searchParam.getDeviceName().trim().equals("")) {
            SearchingForName searchingForName = new SearchingForName(searchParam.getDeviceName());
            devcapsules = searchingForName.search(devcapsules);
        }

        if ((searchParam.getStartSearchDate() != null) || (searchParam.getEndSearchDate() != null)) {
            if (searchParam.getStartSearchDate() == null) searchParam.setStartSearchDate((long)0);
            if (searchParam.getEndSearchDate() == null) searchParam.setEndSearchDate(System.currentTimeMillis());

//            SearchingForStatus searchingForStatus = new SearchingForStatus(searchParam.getHostStatuses());
//            devcapsules = searchingForStatus.find();
        }

        if ((searchParam.getHostStatuses() != null) && (searchParam.getHostStatuses().size() > 0)) {
            SearchingForStatus searchingForStatus = new SearchingForStatus(searchParam.getHostStatuses());
            devcapsules = searchingForStatus.search(devcapsules);
        }

        if (searchParam.getRegions() != null) {
//            SearchingForStatus searchingForStatus = new SearchingForStatus(searchParam.getHostStatuses());
//            devcapsules = searchingForStatus.find();
        }

        /*
        if (devcapsules != null) {

        }*/

        devcapsules = DataModel.getInstance().sortDevcapsuleByTime(devcapsules);

        return null;
    }
}
