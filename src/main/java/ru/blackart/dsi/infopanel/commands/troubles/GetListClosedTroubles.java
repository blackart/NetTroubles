package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.DateStr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetListClosedTroubles extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();

    public String execute() throws Exception {
        this.getResponse().setCharacterEncoding("utf-8");
        this.getResponse().setContentType("text/xml");

        synchronized (dataModel) {
            TroubleList currTroubleList = dataModel.getList_of_complete_troubles();
            List<Trouble> currTroubles = currTroubleList.getTroubles();

            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd/MM/yyyy HH:mm:ss");

            int criteria_find = Integer.valueOf(this.getRequest().getParameter("category"));
            String date_find = this.getRequest().getParameter("date");

            Date left_date = new Date();
            Date right_date = new Date();

            switch (criteria_find) {
                case 1: {
                    left_date = DateStr.parse(date_find.trim(), "00:00:00");
                    right_date = DateStr.parse(date_find.trim(), "23:59:59");
                    break;
                }
                case 2: {
                    String[] date_p = date_find.trim().split("\\/");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateStr.parse(date_find.trim(), "00:00:00"));

                    left_date = DateStr.parse("01/" + date_p[1] + "/" + date_p[2], "00:00:00");
                    right_date = DateStr.parse(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + date_p[1] + "/" + date_p[2], "23:59:59");
                    break;
                }
                case 3: {
                    String[] date_p = date_find.trim().split("\\/");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateStr.parse("01/" + "12/" + date_p[2], "00:00:00"));

                    left_date = DateStr.parse("01/" + "01/" + date_p[2], "00:00:00");
                    right_date = DateStr.parse(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + "12/" + date_p[2], "23:59:59");
                    break;
                }
            }
            ArrayList<Trouble> troubles = new ArrayList<Trouble>();

            try {

                for (Trouble t : currTroubles) {
                    Date date_in = new Date(Long.valueOf(t.getDate_in() != null ? t.getDate_in() : "0"));
                    if ((date_in.compareTo(left_date) >= 0) && (date_in.compareTo(right_date) <= 0)) {
                        troubles.add(t);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            this.getResponse().getWriter().print(gson.toJson(troubles));
        }
        return null;
    }
}
