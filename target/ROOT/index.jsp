<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.blackart.dsi.infopanel.temp.LogEngine" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>nets trouble</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="REFRESH" CONTENT="600">
    <!-- jQuery -->
    <script type="text/javascript" src="js/jQuery/js/jquery-1.4.2.min.js"></script>
    <!-- UI -->
    <script type="text/javascript" src="js/jQuery/js/jquery-ui-1.8.accordion.min.js"></script>
    <!--JS-->
    <script type="text/javascript" src="js/panel.js"></script>
    <!--CSS style-->
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <link rel="stylesheet" type="text/css" href="css/accordion.css"/>
    <link rel="stylesheet" type="text/css" href="css/accordion_dev.css"/>
</head>
<body>
<div class="top_spoiler">&nbsp;</div>
<div class="general">
    <div class="cotroll_panel">
        <div style="position: relative; margin: 0 20px 0 20px;">
            <input type="button" onclick="window.location.reload(true)" id="refresh_trouble_list" value="обновить"/>
        </div>
    </div>

    <div id="admin_trouble_list">
        <%
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd/MM/yyyy HH:mm:ss");

            TroubleList currTroubleList = (TroubleList) config.getServletContext().getAttribute("callCenterTroubleList");
            List<Trouble> currTroubles = currTroubleList.getTroubles();

            try {
                for (Trouble t : currTroubles) {
                    if (!t.getClose()) {
                        Date date_in = new Date(Long.valueOf(t.getDate_in() != null ? t.getDate_in() : "0"));
                        Date date_out = new Date(Long.valueOf(t.getDate_out() != null ? t.getDate_out() : "0"));
                        Date timeout = new Date(Long.valueOf(t.getTimeout() != null ? t.getTimeout() : "0"));

                        List<Service> services = t.getServices();
                        String serv = "";
                        if (services != null) {
                            for (Service s : services) {
                                serv += s.getName() + " ; ";
                            }
                        }
        %>
        <div id='<%=String.valueOf(t.getId())%>_block_item_admin_trouble_lists'>
            <h3>
                <div class='panel_kit'>
                    <div class='title_p'>
                        <div id='<%=String.valueOf(t.getId())%>_title_item_admin_trouble_list'><%=(t.getTitle() == null ? "" : t.getTitle())%>
                        </div>
                        <div class='title_ex'>Время аварии: <%=format.format(date_in)%>
                        </div>
                    </div>
                </div>
            </h3>
            <div class="content" id="<%=String.valueOf(t.getId())%>">

                <div class="text_bold title_rubric">Список узлов:</div>
                <div class="cont_rubric">
                    <%
                        for (Devcapsule devcapsule : t.getDevcapsules()) {
                            if (!Pattern.matches("(p)[0-9]*", devcapsule.getDevice().getName())) {
                                date_in = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));
                                date_out = new Date(Long.valueOf(devcapsule.getTimeup() != null ? devcapsule.getTimeup() : "0"));

                    %>
                    <div><%=devcapsule.getDevice().getName()%><%=(devcapsule.getDevice().getDescription() != null ? " , " + devcapsule.getDevice().getDescription() : "")%>
                    </div>

                    <%
                            }
                        }
                    %>
                </div>
                <div class="text_bold title_rubric">Затронутые сервисы:</div>
                <div class="cont_rubric"><%=serv%></div>
                <div class="text_bold title_rubric">Дата и время аварии:</div>
                <div class="cont_rubric"><%=(t.getDate_in() != null ? format.format(date_in) : "")%></div>
                <div class="text_bold title_rubric">Сроки устранения:</div>
                <div class="cont_rubric"><%=(t.getTimeout() != null ? format.format(timeout) : "")%></div>
                <div class="text_bold title_rubric">Факатическая проблема:</div>
                <div class="cont_rubric"><%=(t.getActualProblem() != null ? t.getActualProblem() : "")%></div>
                <div class='text_bold title_rubric'>Комментарии:</div>
                <div class="trouble_comments">
                    <%
                        List<Comment> comments = t.getComments();
                        for (Comment comment : comments) {
                            Date date_comment = new Date(Long.valueOf(comment.getTime() != null ? comment.getTime() : "0"));
                    %>
                            <div class="comment_item">
                                <div class="comment_title">
                                    <div class="comment_author"><%=comment.getAuthor().getFio()%>
                                    </div>
                                    <div class="comment_date"><%=format.format(date_comment)%>
                                    </div>
                                </div>
                                <div class="comment_text"><%=comment.getText()%>
                                </div>
                            </div>
                    <%
                        }
                    %>
                </div>
                <br>
            </div>
        </div>

        <%
            boolean empty = false;
            for (Devcapsule devcapsule : t.getDevcapsules()) {
                if (devcapsule.getComplete()) {
                    if (!empty) {%><div class="title_block_after_accordion">Восстановренное оборудование:</div><%}

                    date_in = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));
        %>
                    <div class="block_after_accordion">
                        <div><%=devcapsule.getDevice().getName()%>, <%=(devcapsule.getDevice().getDescription() != null ? devcapsule.getDevice().getDescription() : "")%> <%=(devcapsule.getTimedown() != null ? "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;down: [" + format.format(date_in) + "]" : "")%> <%=devcapsule.getTimeup() != null ? "up: [" + format.format(new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimeup() : "0"))) + "]" : ""%></div>
                    </div>
        <%
                    empty = true;
                }
            }
        %>

        <div class="title_block_after_accordion">Неработающее оборудование:</div>

        <%
            for (Devcapsule devcapsule : t.getDevcapsules()) {
                if (!devcapsule.getComplete()) {
                    date_in = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));
        %>
                    <div class="block_after_accordion">
                        <div><%=devcapsule.getDevice().getName()%>, <%=(devcapsule.getDevice().getDescription() != null ? devcapsule.getDevice().getDescription() : "")%> <%=(devcapsule.getTimedown() != null ? "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;down: [" + format.format(date_in) + "]" : "")%> <%=devcapsule.getTimeup() != null ? "up: [" + format.format(date_out) + "]" : ""%></div>
                    </div>
        <%
                }
            }
        %>

        <%
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
    </div>
</div>

<div class="footer_b">
    <div class="ramka_1">
        <div class="ramka_2">
            <div>by Bl@ckArt</div>
        </div>
    </div>
</div>


</body>
</html>