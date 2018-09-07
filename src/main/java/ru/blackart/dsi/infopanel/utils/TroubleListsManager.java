package ru.blackart.dsi.infopanel.utils;

import ru.blackart.dsi.infopanel.beans.*;

import javax.servlet.ServletConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TroubleListsManager {
    private static TroubleListsManager troubleListsManager;
    private ServletConfig config;
    private TroubleList troubleListForCallCenter;

    public static TroubleListsManager getInstance() {
        if (troubleListsManager == null) {
            troubleListsManager = new TroubleListsManager();
            troubleListsManager.troubleListForCallCenter = new TroubleList();
            troubleListsManager.troubleListForCallCenter.setTroubles(new ArrayList<Trouble>());            
        }

        return troubleListsManager;
    }

    public void setServletConfig (ServletConfig config) {
        this.config = config;    
    }

    public TroubleList sortTroubleList(TroubleList troubleList) {
        for(int i = 0; i < troubleList.getTroubles().size(); ++i) {
            for(int j = 0; j < i; ++j) {
                Long dev_time_i = Long.valueOf(((Trouble)troubleList.getTroubles().get(i)).getDate_in());
                Long dev_time_j = Long.valueOf(((Trouble)troubleList.getTroubles().get(j)).getDate_in());
                if (dev_time_i.longValue() < dev_time_j.longValue()) {
                    Trouble trouble_1 = (Trouble)troubleList.getTroubles().get(i);
                    troubleList.getTroubles().set(i, troubleList.getTroubles().get(j));
                    troubleList.getTroubles().set(j, trouble_1);
                }
            }
        }

        return troubleList;
    }

    public void setTroubleListForCallCenter(TroubleList troubleList) {
        this.config.getServletContext().setAttribute("callCenterTroubleList", this.sortTroubleList(troubleList));
    }

    public void setWaitingCloseTroubleList(TroubleList troubleList) {
        this.config.getServletContext().setAttribute("waitingCloseTroubleList", sortTroubleList(troubleList));
    }

    public void setCurrTroubleList(TroubleList troubleList) {
        this.config.getServletContext().setAttribute("currTroubleList", sortTroubleList(troubleList));
    }    

    public void setClosedTroubleList(TroubleList troubleList) {
        this.config.getServletContext().setAttribute("closedTroubleList", troubleList);
    }

    public void setTrashTroubleList(TroubleList troubleList) {
        this.config.getServletContext().setAttribute("trashTroubleList", sortTroubleList(troubleList));
    }

    public void setNeedActualProblemTroubleList(TroubleList troubleList) {
        this.config.getServletContext().setAttribute("needActualProblemTroubleList", sortTroubleList(troubleList));
    }

    public void updateDeviceList(List<Device> devices) {
        this.config.getServletContext().setAttribute("deviceList", devices);
    }

    public void setMainFilterList(List<DeviceFilter> mainFilter) {
        this.config.getServletContext().setAttribute("mainFilter", mainFilter);    
    }

    public ServletConfig getHTTPServletConfig() {
        return this.config;
    }
}
