package ru.blackart.dsi.infopanel.commands.reports;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.searching.*;

import java.util.List;

public class GetTroubleReport extends AbstractCommand {
    private final DataModel dataModel = DataModel.getInstance();
    @Override
    public String execute() throws Exception {
        String searchParam_str = this.getRequest().getParameter("param");

        Gson gson = new Gson();
        SearchParam searchParam = gson.fromJson(searchParam_str, SearchParam.class);


        List<Devcapsule> devcapsules = null;

        if ((searchParam.getDeviceName() != null) && (!searchParam.getDeviceName().trim().equals(""))) {
            SearchingForName searchingForName = new SearchingForName(searchParam.getDeviceName());
            devcapsules = searchingForName.search(devcapsules);
        }

        if ((searchParam.getStartSearchDate() != null) || (searchParam.getEndSearchDate() != null)) {
            if (searchParam.getStartSearchDate() == null) searchParam.setStartSearchDate((long)0);
            if (searchParam.getEndSearchDate() == null) searchParam.setEndSearchDate(System.currentTimeMillis());

            SearchingForDate searchingForDate = new SearchingForDate(searchParam.getStartSearchDate(), searchParam.getEndSearchDate());
            devcapsules = searchingForDate.search(devcapsules);
        }

        if ((searchParam.getHostStatuses() != null) && (searchParam.getHostStatuses().size() > 0)) {
            SearchingForStatus searchingForStatus = new SearchingForStatus(searchParam.getHostStatuses());
            devcapsules = searchingForStatus.search(devcapsules);
        }

        if (searchParam.getRegions() != null) {
            SearchingForRegion searchingForRegion = new SearchingForRegion(searchParam.getRegions());
            devcapsules = searchingForRegion.search(devcapsules);
        }

        if (devcapsules != null) {
            devcapsules = DataModel.getInstance().sortDevcapsuleByTime(devcapsules);
            synchronized (dataModel) {
                for (Devcapsule d : devcapsules) {
                    Trouble trouble = dataModel.getTroubleForDevcapsule(d);

                }
            }
        }

        this.getResponse().getWriter().print(gson.toJson(devcapsules));

        return null;
    }
}
