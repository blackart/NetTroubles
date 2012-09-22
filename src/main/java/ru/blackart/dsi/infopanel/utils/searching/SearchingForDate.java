package ru.blackart.dsi.infopanel.utils.searching;

import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.utils.filters.ManagerMainDeviceFilter;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchingForDate implements Searching {
    private Date left_date = new Date();
    private Date right_date = new Date();
    ManagerMainDeviceFilter managerMainDeviceFilter = ManagerMainDeviceFilter.getInstance();

    public SearchingForDate(int mode, String minDate, String maxDate) {
        switch (mode) {
            case 1: {
                this.left_date = DateStr.parse(minDate.trim(), "00:00:00");
                this.right_date = DateStr.parse(minDate.trim(), "23:59:59");
                break;
            }
            case 2: {
                String[] date_p = minDate.trim().split("\\/");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateStr.parse(minDate.trim(), "00:00:00"));

                this.left_date = DateStr.parse("01/" + date_p[1] + "/" + date_p[2], "00:00:00");
                this.right_date = DateStr.parse(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + date_p[1] + "/" + date_p[2], "23:59:59");
                break;
            }
            case 3: {
                String[] date_p = minDate.trim().split("\\/");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateStr.parse("01/" + "12/" + date_p[2], "00:00:00"));

                this.left_date = DateStr.parse("01/" + "01/" + date_p[2], "00:00:00");
                this.right_date = DateStr.parse(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + "12/" + date_p[2], "23:59:59");
                break;
            }
            case 4: {
                this.left_date = DateStr.parse(minDate.trim(), "00:00:00");
                this.right_date = DateStr.parse(maxDate.trim(), "23:59:59");
                break;
            }
        }
    }

    public Date getLeft_date() {
        return left_date;
    }

    public Date getRight_date() {
        return right_date;
    }

    public List<Devcapsule> find() {
        DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
        List<Trouble> troubles = new ArrayList<Trouble>();
        troubles.addAll(dataModelConstructor.getTroubleListForName("current").getTroubles());
        troubles.addAll(dataModelConstructor.getTroubleListForName("complete").getTroubles());
        troubles.addAll(dataModelConstructor.getTroubleListForName("waiting_close").getTroubles());

        List<Devcapsule> devc_find = new ArrayList<Devcapsule>();

        for (Trouble t : troubles) {
            for (Devcapsule d : t.getDevcapsules()) {
                Date date_down = new Date(Long.valueOf(d.getTimedown() != null ? d.getTimedown() : "0"));
                if ((date_down.compareTo(this.left_date) >= 0) && (date_down.compareTo(this.right_date) <= 0) && this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))  {
                    devc_find.add(d);
                }
            }
        }
        return devc_find;
    }

    public List<Devcapsule> findInData(List<Devcapsule> devcapsules) {
        List<Devcapsule> devc_find = new ArrayList<Devcapsule>();
        for (Devcapsule d : devcapsules) {
                Date date_down = new Date(Long.valueOf(d.getTimedown() != null ? d.getTimedown() : "0"));
                if ((date_down.compareTo(this.left_date) >= 0) && (date_down.compareTo(this.right_date) <= 0) && this.managerMainDeviceFilter.filterInputDevice(d.getDevice()))  {
                    devc_find.add(d);
                }
        }

        return devc_find;
    }
}
