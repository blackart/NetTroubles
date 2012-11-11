package ru.blackart.dsi.infopanel.utils;

import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.model.DataModel;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

public class GenerateMonitoringPanel {
    private static GenerateMonitoringPanel generateMonitoringPanel;
    private Properties settings;
    private String cathome;
    private int timeout_reload;

    public static GenerateMonitoringPanel getInstance(Properties set, String cathome) {
        if (generateMonitoringPanel == null) {
            generateMonitoringPanel = new GenerateMonitoringPanel();
            generateMonitoringPanel.settings = new Properties(set);
            generateMonitoringPanel.cathome = cathome;
            generateMonitoringPanel.timeout_reload = 10;
        }
        return generateMonitoringPanel;
    }

    private StringBuffer generatePageEngin() {
        StringBuffer page = new StringBuffer("");

        page.append("" +
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Net Troubles</title>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">\n" +
                "    <META HTTP-EQUIV=\"Cache-Control\" CONTENT=\"no-cache, no-store, must-revalidate\">\n" +
                "    <META HTTP-EQUIV=\"REFRESH\" CONTENT=\"" + this.timeout_reload + "\">\n" +
                "    <!-- jQuery -->\n" +
                "    <script type=\"text/javascript\" src=\"../js/jQuery/js/jquery-1.4.2.min.js\"></script>\n" +
                "    <!-- UI -->\n" +
                "    <script type=\"text/javascript\" src=\"../js/jQuery/js/jquery-ui-1.8.accordion.min.js\"></script>\n" +
                "    <!--JS-->\n" +
                "    <script type=\"text/javascript\" src=\"../js/panel.js\"></script>\n" +
                "    <!--CSS style-->\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\"/>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/accordion.css\"/>\n" +
                "     \n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"top_spoiler\">&nbsp;</div>\n" +
                "<div class=\"general\">\n" +
                "    <div class=\"cotroll_panel\">\n" +
                "        <div style=\"position: relative; margin: 0 20px 0 20px;\">\n" +
                "            Net Troubles\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <div id=\"admin_trouble_list\">");

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy hh:mm:ss");

        DataModel dataModel = DataModel.getInstance();
        List<Trouble> currTroubles = dataModel.getList_of_current_troubles().getTroubles();

        try {
            for (Trouble t : currTroubles) {
                Date date_in = new Date(Long.valueOf(t.getDate_in() != null ? t.getDate_in() : "0"));
                Date date_out = new Date(Long.valueOf(t.getDate_out() != null ? t.getDate_out() : "0"));
                Date timeout = new Date(Long.valueOf(t.getTimeout() != null ? t.getTimeout() : "0"));

                List<Service> services = t.getServices();
                String serv = "";
                for (Service s : services) {
                    serv += s.getName() + " ; ";
                }

                page.append("<div id=\"" + String.valueOf(t.getId()) + "_block_item_admin_trouble_lists\">");
                page.append("  <h3>");
                page.append("    <div class='panel_kit'>");
                page.append("      <div class='title_p'>");
                page.append("        <div id='" + String.valueOf(t.getId()) + "_title_item_admin_trouble_list'>" + (t.getTitle() == null ? "" : t.getTitle()) + "</div>");
                page.append("        <div class='title_ex'>Время аварии: " + format.format(date_in) + "</div>");
                page.append("      </div>");
                page.append("    </div>");
                page.append("  </h3>");
                page.append("  <div class=\"content\" id=\"" + String.valueOf(t.getId()) + "\">");
                page.append("    <div class=\"text_bold title_rubric\">Заголовок:</div><div id=\"" + String.valueOf(t.getId()) + "_title\" class=\"cont_rubric\">" + t.getTitle() + "</div>");


                page.append("<div class=\"dev_list\">");
                page.append("<div class=\"text_bold title_rubric\">Список узлов:</div>");
                for (Devcapsule devcapsule : t.getDevcapsules()) {
                    date_in = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));
                    date_out = new Date(Long.valueOf(devcapsule.getTimeup() != null ? devcapsule.getTimeup() : "0"));

                    page.append("<div id=\"" + String.valueOf(t.getId()) + "_dev\">");
                    page.append("  <h1>");
                    page.append("    <div class='panel_kit'>");
                    page.append("      <div class='title_p'>");
                    page.append("        <div>" + devcapsule.getDevice().getName() + ", " + (devcapsule.getDevice().getDescription() != null ? devcapsule.getDevice().getDescription() : "") + "</div>");
                    page.append("        <div class='title_ex'>down: " + (t.getDate_in() != null ? format.format(date_in) : "") + "</div>");
                    page.append("      </div>");
                    page.append("    </div>");
                    page.append("  </h1>");
                    page.append("</div>");
                }
                page.append("</div>");

                page.append("<div class=\"text_bold title_rubric\">Затронутые сервисы:</div><div id=\"" + String.valueOf(t.getId()) + "_service\" class=\"cont_rubric\">" + serv + "</div>");
                page.append("<div class=\"text_bold title_rubric\">Дата и время аварии:</div><div id=\"" + String.valueOf(t.getId()) + "_date_in\" class=\"cont_rubric\">" + (t.getDate_in() != null ? format.format(date_in) : "") + "</div>");
                page.append("<div class=\"text_bold title_rubric\">Сроки устранения:</div><div id=\"" + String.valueOf(t.getId()) + "_timeout\" class=\"cont_rubric\">" + (t.getTimeout() != null ? format.format(timeout) : "") + "</div>");
                page.append("<div class=\"text_bold title_rubric\">Дата и время устранения аварии:</div><div id=\"" + String.valueOf(t.getId()) + "_date_out\" class=\"cont_rubric\">" + (t.getDate_out() != null ? format.format(date_out) : "") + "</div>");
                page.append("<div class=\"text_bold title_rubric\">Фактическая проблема:</div><div id=\"" + String.valueOf(t.getId()) + "_description\" class=\"cont_rubric\">" + (t.getActualProblem() != null ? t.getActualProblem() : "") + "</div>");

                page.append("<div class='text_bold title_rubric'>Комментарии:</div>");
                page.append("<div class=\"trouble_comments\">");
                for (Comment comment : t.getComments()) {
                    Date date_comment = new Date(Long.valueOf(comment.getTime() != null ? comment.getTime() : "0"));

                    page.append("<div class=\"comment_item\">");
                    page.append("  <div class=\"comment_title\">");
                    page.append("    <div class=\"comment_author\">" + comment.getAuthor().getFio() + "</div>");
                    page.append("    <div class=\"comment_date\">" + format.format(date_comment) + "</div>");
                    page.append("  </div>");
                    page.append("  <div class=\"comment_text\">" + comment.getText() + "</div>");
                    page.append("</div>");
                }
                page.append("</div>");

                page.append("</div>");
                page.append("</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        page.append("</div>" +
                "</div>\n" +
                "\n" +
                "<div class=\"footer_b\">\n" +
                "    <div class=\"ramka_1\">\n" +
                "        <div class=\"ramka_2\">\n" +
                "            <div>by Bl@ckArt</div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>");

        return page;
    }

    public void generatePage() throws IOException {
        DataOutputStream dos;
        File f = new File(this.cathome + this.settings.getProperty("pathToPageFile") + this.settings.getProperty("namePageFile"));
        if (f.exists()) {
            f.delete();
            f.createNewFile();
        }
        dos = new DataOutputStream(new FileOutputStream(f));

        dos.write(this.generatePageEngin().toString().getBytes("utf-8"));
        dos.close();
    }
}
