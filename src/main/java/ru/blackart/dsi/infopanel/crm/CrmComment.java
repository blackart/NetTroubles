package ru.blackart.dsi.infopanel.crm;

import com.myjavatools.xml.BasicXmlData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Trouble;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrmComment {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Trouble trouble;
    private Comment comment;

    public CrmComment(Trouble trouble, Comment comment) {
        this.trouble = trouble;
        this.comment = comment;
    }

    public synchronized boolean send() {
        Boolean complete_send_to_crm = true;

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        try {
            BasicXmlData xml = new BasicXmlData("action");
            xml.setAttribute("name", "Comment");

            BasicXmlData xml_level_1 = new BasicXmlData("parameters");

            BasicXmlData xml_level_2_id_trouble = new BasicXmlData("parameter");
            xml_level_2_id_trouble.setAttribute("name", "id_trouble");
            xml_level_2_id_trouble.setAttribute("value", String.valueOf(trouble.getId()));
            xml_level_1.addKid(xml_level_2_id_trouble);

            BasicXmlData xml_level_2_id_comment = new BasicXmlData("parameter");
            xml_level_2_id_comment.setAttribute("name", "id_comment");
            xml_level_2_id_comment.setAttribute("value", String.valueOf(comment.getId()));
            xml_level_1.addKid(xml_level_2_id_comment);

            BasicXmlData xml_level_2_author = new BasicXmlData("parameter");
            xml_level_2_author.setAttribute("name", "author");
            xml_level_2_author.setAttribute("value", comment.getAuthor().getFio());
            xml_level_1.addKid(xml_level_2_author);

            BasicXmlData xml_level_2_desc = new BasicXmlData("parameter");
            xml_level_2_desc.setAttribute("name", "content");
            xml_level_2_desc.setAttribute("value", comment.getText());
            xml_level_1.addKid(xml_level_2_desc);

            BasicXmlData xml_level_2_date = new BasicXmlData("parameter");
            xml_level_2_date.setAttribute("name", "date");
            xml_level_2_date.setAttribute("value", format.format(new Date(Long.valueOf(comment.getTime()))));
            xml_level_1.addKid(xml_level_2_date);

            xml.addKid(xml_level_1);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            xml.save(baos);

            SendToCrm.send(baos);
        } catch (Exception e) {
            log.error("Comment [" + comment.getId() + "] to trouble " + trouble.getTitle() + " [" + trouble.getId() + "] is not sent to CRM - " + e.getMessage());
            e.printStackTrace();
            complete_send_to_crm = false;
        }

        if (complete_send_to_crm) {
            log.info("Comment [" + trouble.getId() + "] to trouble " + trouble.getTitle() + " [" + trouble.getId() + "] successfully submitted to CRM successfully.");
        }

        return complete_send_to_crm;
    }

}
