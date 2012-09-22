package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.*;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.OutputStream;

import com.myjavatools.xml.BasicXmlData;
import ru.blackart.dsi.infopanel.utils.DateStr;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

public class GetListOfClosedTroubles extends AbstractCommand {
    public String execute() throws Exception {
        this.getResponse().setCharacterEncoding("utf-8");
        this.getResponse().setContentType("text/xml");

        DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
        TroubleList currTroubleList = dataModelConstructor.getList_of_complete_troubles();
        List<Trouble> currTroubles = currTroubleList.getTroubles();

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        int criteria_find = Integer.valueOf(this.getRequest().getParameter("find_entry"));
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

        BasicXmlData xml = new BasicXmlData("logEntry");
        BasicXmlData xml_level_1;

        try {
            for (Trouble t : currTroubles) {
                Date date_in = new Date(Long.valueOf(t.getDate_in() != null ? t.getDate_in() : "0"));
                if ((date_in.compareTo(left_date) >= 0) && (date_in.compareTo(right_date) <= 0)) {
                    xml_level_1 = new BasicXmlData("entry");

                    Date date_out = new Date(Long.valueOf(t.getDate_out() != null ? t.getDate_out() : "0"));
                    Date timeout = new Date(Long.valueOf(t.getTimeout() !=null ? t.getTimeout() : "0"));

                    String serv = "";
                    String serv_id = "";

                    if (t.getServices() != null) {
                        List<Service> services = t.getServices();
                        for (Service s : services) {
                            serv+=s.getName() + " ; ";
                            serv_id+=String.valueOf(s.getId()) + " ; ";
                        }
                    }

                    xml_level_1.addKid(new BasicXmlData("id", String.valueOf(t.getId())));

                    xml_level_1.addKid(new BasicXmlData("title", t.getTitle() != null ? t.getTitle() : ""));

                    xml_level_1.addKid(new BasicXmlData("service", serv));
                    xml_level_1.addKid(new BasicXmlData("service_id", serv_id));

                    xml_level_1.addKid(new BasicXmlData("date_in", t.getDate_in() != null ? format.format(date_in) : ""));
                    xml_level_1.addKid(new BasicXmlData("timeout", t.getTimeout() != null ? format.format(timeout) : ""));
                    xml_level_1.addKid(new BasicXmlData("date_out", t.getDate_out() != null ? format.format(date_out) : ""));

                    xml_level_1.addKid(new BasicXmlData("actual_problem", t.getActualProblem() != null ? t.getActualProblem() : ""));

                    for (Comment comment : t.getComments()) {
                        BasicXmlData xml_level_3 = new BasicXmlData("descriptions");

                        Date date_desc = new Date(Long.valueOf(comment.getTime() != null ? comment.getTime() : "0"));

                        xml_level_3.addKid(new BasicXmlData("id_desc",String.valueOf(comment.getId())));
                        xml_level_3.addKid(new BasicXmlData("text_desc",comment.getText()));
                        xml_level_3.addKid(new BasicXmlData("time_desc",comment.getTime() != null ? format.format(date_desc) : ""));
                        xml_level_3.addKid(new BasicXmlData("author_desc",comment.getAuthor().getFio()));
                        xml_level_3.addKid(new BasicXmlData("id_author_desc",String.valueOf(comment.getAuthor().getId())));

                        xml_level_1.addKid(xml_level_3);
                    }

                    for (Devcapsule devcapsule : t.getDevcapsules()) {
                        BasicXmlData xml_level_2 = new BasicXmlData("devcapsul");

                        date_in = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));
                        date_out = new Date(Long.valueOf(devcapsule.getTimeup() != null ? devcapsule.getTimeup() : "0"));

                        xml_level_2.addKid(new BasicXmlData("id_dev", String.valueOf(devcapsule.getId())));
                        xml_level_2.addKid(new BasicXmlData("hostId",devcapsule.getDevice().getName()));
                        xml_level_2.addKid(new BasicXmlData("desc",devcapsule.getDevice().getDescription() != null ? devcapsule.getDevice().getDescription() : ""));
                        xml_level_2.addKid(new BasicXmlData("timedown", t.getDate_in() != null ? format.format(date_in) : ""));
                        xml_level_2.addKid(new BasicXmlData("timeup", t.getDate_out() != null ? format.format(date_out) : ""));
                        xml_level_1.addKid(xml_level_2);
                    }
                    xml_level_1.addKid(new BasicXmlData("devc_count", String.valueOf(t.getDevcapsules().size())));
                    xml.addKid(xml_level_1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OutputStream out = this.getResponse().getOutputStream();
        xml.save(out);

        return null;
    }
}
