package ru.blackart.dsi.infopanel.crm;

import com.myjavatools.xml.BasicXmlData;
import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Device;
import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrmTrouble {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Trouble trouble;
    private String status_crm;

    private String getStatusCrm(int id) {
        String status;
        switch (id) {
            case 1: status = "active"; break;
            case 2: status = "closed"; break;
            case 3: status = "inactive"; break;
            default: status = "inactive"; break;
        }

        return status;
    }

    public CrmTrouble(Trouble trouble, String status_crm) {
        this.trouble = trouble;
        this.status_crm = status_crm;
    }

    public Boolean validation() {
        boolean valid = true;
        try {
            if ((trouble.getTitle() == null) || (trouble.getTitle().trim().equals(""))) {
                valid = false;
            }
            if (trouble.getServices() == null) {
                valid = false;
            }
            if ((trouble.getDate_in() == null) || (trouble.getDate_in().equals(""))) {
                valid = false;
            }
            if ((trouble.getTimeout() == null) || (trouble.getTimeout().equals(""))) {
                valid = false;
            }
            if ((trouble.getClose()) && ((trouble.getDate_out() == null) || (trouble.getDate_out().equals("")))) {
                valid = false;
            }
            if ((trouble.getComments() == null) || trouble.getComments().size() == 0) {
                valid = false;
            }
        } catch (LazyInitializationException e) {
            valid = false;
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    public synchronized boolean send() {
        Boolean complete_send_to_crm = true;

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        try {
            String service = "";
            try {
                for (Service s : trouble.getServices()) {
                    service = service + s.getId() + ";";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String sw = "";
            for (Devcapsule d : trouble.getDevcapsules()) {
                Device device = d.getDevice();
                sw = sw + device.getName() + (device.getHoststatus() != null ? ", " + device.getHoststatus().getName() : "") + "; ";
            }

            BasicXmlData xml = new BasicXmlData("action");
            xml.setAttribute("name", "Problem");

            BasicXmlData xml_level_1 = new BasicXmlData("parameters");

            BasicXmlData xml_level_2_id = new BasicXmlData("parameter");
            xml_level_2_id.setAttribute("name", "id");
            xml_level_2_id.setAttribute("value", String.valueOf(trouble.getId()));
            xml_level_1.addKid(xml_level_2_id);

            BasicXmlData xml_level_2_category = new BasicXmlData("parameter");
            xml_level_2_category.setAttribute("name", "category");
            xml_level_2_category.setAttribute("value", "1");
            xml_level_1.addKid(xml_level_2_category);

            BasicXmlData xml_level_2_priority = new BasicXmlData("parameter");
            xml_level_2_priority.setAttribute("name", "priority");
            xml_level_2_priority.setAttribute("value", "1");
            xml_level_1.addKid(xml_level_2_priority);

            BasicXmlData xml_level_2_title = new BasicXmlData("parameter");
            xml_level_2_title.setAttribute("name", "title");
            xml_level_2_title.setAttribute("value", trouble.getTitle());
            xml_level_1.addKid(xml_level_2_title);

            BasicXmlData xml_level_2_sw = new BasicXmlData("parameter");
            xml_level_2_sw.setAttribute("name", "sw");
            xml_level_2_sw.setAttribute("value", sw);
            xml_level_1.addKid(xml_level_2_sw);

            BasicXmlData xml_level_2_services = new BasicXmlData("parameter");
            xml_level_2_services.setAttribute("name", "services");
            xml_level_2_services.setAttribute("value", service);
            xml_level_1.addKid(xml_level_2_services);

            BasicXmlData xml_level_2_legend = new BasicXmlData("parameter");
            xml_level_2_legend.setAttribute("name", "legend");
            xml_level_2_legend.setAttribute("value", "");
            xml_level_1.addKid(xml_level_2_legend);

            BasicXmlData xml_level_2_desc = new BasicXmlData("parameter");
            xml_level_2_desc.setAttribute("name", "desc");
            xml_level_2_desc.setAttribute("value", trouble.getActualProblem());
            xml_level_1.addKid(xml_level_2_desc);

            BasicXmlData xml_level_2_timeout = new BasicXmlData("parameter");
            xml_level_2_timeout.setAttribute("name", "planupdate");
            xml_level_2_timeout.setAttribute("value", format.format(new Date(Long.valueOf(trouble.getTimeout()))));
            xml_level_1.addKid(xml_level_2_timeout);

            BasicXmlData xml_level_2_date_in = new BasicXmlData("parameter");
            xml_level_2_date_in.setAttribute("name", "startproblem");
            xml_level_2_date_in.setAttribute("value", format.format(new Date(Long.valueOf(trouble.getDate_in()))));
            xml_level_1.addKid(xml_level_2_date_in);

            if ((trouble.getDate_out() != null) && (!trouble.getDate_out().trim().equals(""))) {
                BasicXmlData xml_level_2_date_out = new BasicXmlData("parameter");
                xml_level_2_date_out.setAttribute("name", "factupdate");
                xml_level_2_date_out.setAttribute("value", format.format(new Date(Long.valueOf(trouble.getDate_out()))));
                xml_level_1.addKid(xml_level_2_date_out);
            }

            BasicXmlData xml_level_2_status = new BasicXmlData("parameter");
            xml_level_2_status.setAttribute("name", "status");
            xml_level_2_status.setAttribute("value", status_crm);
            xml_level_1.addKid(xml_level_2_status);

            BasicXmlData xml_level_2_author = new BasicXmlData("parameter");
            xml_level_2_author.setAttribute("name", "author");
            xml_level_2_author.setAttribute("value", trouble.getAuthor().getFio());
            xml_level_1.addKid(xml_level_2_author);

            xml.addKid(xml_level_1);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            xml.save(baos);

            SendToCrm.send(baos);
        } catch (Exception e) {
            log.error("Trouble " + trouble.getTitle() + " [" + trouble.getId() + "] is not sent to CRM - " + e.getMessage());
            e.printStackTrace();
            complete_send_to_crm = false;
        }

        if (complete_send_to_crm) {
            log.info("Trouble " + trouble.getTitle() + " [" + trouble.getId() + "] submitted to CRM successfully. Trouble CRM status - [" + getStatusCrm(Integer.valueOf(status_crm)) + "]");
        }

        return complete_send_to_crm;
    }

    public boolean sendComment() {
        return true;
    }
}
