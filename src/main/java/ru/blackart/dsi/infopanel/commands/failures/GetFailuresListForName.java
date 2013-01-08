package ru.blackart.dsi.infopanel.commands.failures;

import com.myjavatools.xml.BasicXmlData;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.utils.searching.SearchingForDate;
import ru.blackart.dsi.infopanel.utils.searching.SearchingForName;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GetFailuresListForName extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();

    @Override
    public String execute() throws Exception {
        this.getResponse().setContentType("text/xml");

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        BasicXmlData xml = new BasicXmlData("failures");
        BasicXmlData xml_level_1;

        int criteria_find = Integer.valueOf(this.getRequest().getParameter("find_entry"));
        String device_name = this.getRequest().getParameter("name");

        String date_find_min = this.getRequest().getParameter("dateMin");
        String dateUse = this.getRequest().getParameter("dateUse");
        String date_find_max = this.getRequest().getParameter("dateMax");

        SearchingForName searchingForName = new SearchingForName(device_name);

        int count_devc = 0;
        long time_down_summ = 0;
        long time_down_for_every_devc;

        Date right_date = null;
        Date left_date = null;

        synchronized (dataModel) {
            if (searchingForName.getDevice() != null) {
                List<Devcapsule> devc_find = searchingForName.find();
                if (devc_find.size() > 0) {
                    if (dateUse.equals("0")) {
                        right_date = new Date(Long.valueOf(devc_find.get(0).getTimedown() != null ? devc_find.get(0).getTimedown() : "0"));
                        left_date = new Date(Long.valueOf(devc_find.get(devc_find.size() - 1).getTimedown() != null ? devc_find.get(devc_find.size() - 1).getTimedown() : "0"));
                    } else {
                        SearchingForDate searchingForDate = new SearchingForDate(criteria_find, date_find_min, date_find_max);
                        devc_find = searchingForDate.findInData(devc_find);

                        right_date = searchingForDate.getRight_date();
                        left_date = searchingForDate.getLeft_date();
                    }
                    xml.addKid(new BasicXmlData("title", "Список аварий за период с " + format.format(left_date) + " по " + format.format(right_date) + ".<br>Список отфильтрован по id узла - " + device_name));
                }
                for (Devcapsule d : devc_find) {
                    Date date_down = new Date(Long.valueOf(d.getTimedown() != null ? d.getTimedown() : "0"));
                    Trouble trouble = dataModel.getTroubleForDevcapsule(d);

                    xml_level_1 = new BasicXmlData("failures_entry");
                    xml_level_1.addKid(new BasicXmlData("devicename", d.getDevice().getName()));
                    xml_level_1.addKid(new BasicXmlData("devicedesc", d.getDevice().getDescription()));
                    xml_level_1.addKid(new BasicXmlData("devicehoststatus", d.getDevice().getHoststatus() == null ? "" : d.getDevice().getHoststatus().getName()));
                    xml_level_1.addKid(new BasicXmlData("region", d.getDevice().getRegion() == null ? "" : d.getDevice().getRegion().getName()));

                    xml_level_1.addKid(new BasicXmlData("timedown", format.format(date_down)));

                    String desc = "";
                    String fio = "";

                    desc = (trouble.getActualProblem() == null ? " --- " : trouble.getActualProblem());
                    if (trouble.getComments().size() > 0) {
                        desc = desc + "<br><strong>Коментарии: </strong><br>";
                        for (Comment comment : trouble.getComments()) {
                            desc = desc + "<div class=\"comment_item\"><div class=\"comment_title\"><div class=\"comment_author\"><i>" + comment.getAuthor().getFio() + "</i></div><div class=\"comment_date\"><i>" + format.format(new Date(Long.valueOf(comment.getTime()))) + "</i></div></div><div class=\"comment_text\"><small>" + comment.getText() + "</small></div></div><br>";
                        }
                    }
                    fio = trouble.getAuthor() == null ? "system" : trouble.getAuthor().getFio();

                    xml_level_1.addKid(new BasicXmlData("troubledesc", desc));
                    xml_level_1.addKid(new BasicXmlData("author", fio));

                    String interval;
                    if (d.getTimeup() != null) {
                        long full_minute = (Long.valueOf(d.getTimeup()) - Long.valueOf(d.getTimedown())) / (60 * 1000);
                        long hour = (Long.valueOf(d.getTimeup()) - Long.valueOf(d.getTimedown())) / (60 * 1000) / 60;
                        long minute = full_minute - (hour * 60);
                        interval = String.valueOf(hour) + ":" + String.valueOf(minute);
                        time_down_summ += Long.valueOf(d.getTimeup()) - Long.valueOf(d.getTimedown());
                        count_devc++;
                    } else {
                        interval = "N/A";
                    }

                    xml_level_1.addKid(new BasicXmlData("interval", interval));
                    xml_level_1.addKid(new BasicXmlData("trouble_id", String.valueOf(trouble.getId())));
                    xml.addKid(xml_level_1);
                }

                if (count_devc > 0) {
                    long full_minute_all = Long.valueOf(time_down_summ) / (60 * 1000);
                    long hour_all = Long.valueOf(time_down_summ) / (60 * 1000) / 60;
                    long minute_all = full_minute_all - (hour_all * 60);

                    time_down_for_every_devc = time_down_summ / count_devc;
                    long full_minute = Long.valueOf(time_down_for_every_devc) / (60 * 1000);
                    long hour = Long.valueOf(time_down_for_every_devc) / (60 * 1000) / 60;
                    long minute = full_minute - (hour * 60);

                    xml.addKid(new BasicXmlData("all_down_interval", String.valueOf(hour_all) + ":" + String.valueOf(minute_all)));
                    xml.addKid(new BasicXmlData("count_failures", String.valueOf(count_devc)));
                    xml.addKid(new BasicXmlData("for_every_down_interval", String.valueOf(hour) + ":" + String.valueOf(minute)));
                }
            }

            OutputStream out = getResponse().getOutputStream();
            xml.save(out);
        }
        return null;
    }
}
