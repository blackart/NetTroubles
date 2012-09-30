<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.*" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Users user = (Users)session.getAttribute("info");
%>

<script type="text/javascript" src="../../js/trouble_trash.js"></script>

<div class="menu_top">
    <div class="control_panel">
        <div class="button_panel_failures">
            <div class="merge_panel">

            </div>
        </div>
        <div class="button_print">
            <%if (user.getGroup_id().getName().equals("admin") || user.getGroup_id().getName().equals("sustem")) {%><input type="button" value="destroy all" id="destroy_all"><%}%>
        </div>
    </div>
    <div class="bottom_button" id="troubles_trash_page_control_panel">панель управления</div>
</div>

<div id="trash_trouble_list">
        <div class="trouble_list_title">Удалённые проблемы:</div>
            <%
                SimpleDateFormat format = new SimpleDateFormat();
                format.applyPattern("dd/MM/yyyy HH:mm:ss");

                TroubleList trashTroubleList = (TroubleList) config.getServletContext().getAttribute("trashTroubleList");
                ArrayList<Region> regions = (ArrayList<Region>) config.getServletContext().getAttribute("regions");
                List<Trouble> trashTroubles = trashTroubleList.getTroubles();

                try {
                    for (Trouble t : trashTroubles) {
                        Date date_in = new Date(Long.valueOf(t.getDate_in() != null ? t.getDate_in() : "0"));
                        Date date_out = new Date(Long.valueOf(t.getDate_out() != null ? t.getDate_out() : "0"));
                        Date timeout = new Date(Long.valueOf(t.getTimeout() != null ? t.getTimeout() : "0"));

                        List<Service> services_t = t.getServices();
                        String serv = "";
                        String serv_id = "";
                        if (services_t != null) {
                            for (Service s : services_t) {
                                serv += s.getName() + " ; ";
                                serv_id += String.valueOf(s.getId()) + " ; ";
                            }
                        }
            %>
            <%
                Properties regions_check = new Properties();
                for (Region r : regions) {
                    regions_check.put(r.getName(), 0);
                }

                for (Devcapsule devcapsule : t.getDevcapsules()) {
                    if ((devcapsule.getDevice().getRegion() != null) && (!devcapsule.getDevice().getRegion().getName().equals(""))) {
                        String region_name = devcapsule.getDevice().getRegion().getName();
                        int count = (Integer) regions_check.get(region_name) + 1;
                        regions_check.put(devcapsule.getDevice().getRegion().getName(), count);
                    }
                }

                String region_str = "";
                for (Region r : regions) {
                    if ((Integer) regions_check.get(r.getName()) > 0) {
                        region_str += r.getName() + " ; ";
                    }
                }
            %>
                    <div class='trouble_item'>
                        <div id='<%=t.getId()%>_block_item_admin_trouble_lists' class='trouble_item_accord'>
                            <h3>
                                <div class='panel_kit'>
                                    <div class='title_p'>
                                        <div id='<%=t.getId()%>_title_item_admin_trouble_list'><%=t.getTitle()%></div>
                                        <div class='title_ex'>Время аварии: <%=format.format(date_in)%>  <%if (t.getDate_out() != null) {%>|  Время устранения аварии: <%=format.format(date_out)%><%}%>  |  Регион: <%=region_str%></div>
                                        <div class='title_ex'>Последний редактор: <%=t.getAuthor().getFio()%></div>
                                        <% if (t.getCrm()) {%><div class="title_crm_status">Отправлено в CRM</div><%}%>
                                        <div class="title_timeout"></div>
                                    </div>
                                </div>
                            </h3>
                            <div class='content' id='<%=t.getId()%>'>

                                <div class='text_bold title_rubric'>Заголовок:</div>
                                <div id='<%=t.getId()%>_title' class='content_block_inactive'><%=t.getTitle()%></div>

                                <div class='text_bold title_rubric'>Список узлов:</div>
                                <div>
                                    <div class="short_dev_list"></div>
                                    <div id='<%=t.getId()%>_dev_list' class='dev_list_down'>
                                        <div>
                                            <%
                                                for (Devcapsule devcapsule : t.getDevcapsules()) {
                                                    if (devcapsule.getComplete()) {
                                                        Date date_in_dev = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));
                                                        Date date_out_dev = new Date(Long.valueOf(devcapsule.getTimeup() != null ? devcapsule.getTimeup() : "0"));

                                            %>
                                            <div class='dev_ent' id='<%=devcapsule.getId()%>_dev'>
                                                <h1 class="up">
                                                    <div class='panel_kit'>
                                                        <div class='title_p'>
                                                            <div id='<%=devcapsule.getId()%>_title_dev'><%=devcapsule.getDevice().getName()%>, <%=devcapsule.getDevice().getDescription()%></div>
                                                            <div class='title_ex'>down: <%=format.format(date_in_dev)%> up: <%=format.format(date_out_dev)%></div>
                                                        </div>
                                                    </div>
                                                </h1>
                                            </div>
                                            <%
                                                    }
                                                }
                                            %>

                                            <%
                                                for (Devcapsule devcapsule : t.getDevcapsules()) {
                                                    if (!devcapsule.getComplete()) {
                                                        Date date_in_dev = new Date(Long.valueOf(devcapsule.getTimedown() != null ? devcapsule.getTimedown() : "0"));

                                            %>
                                            <div class='dev_ent' id='<%=devcapsule.getId()%>_dev'>
                                                <h1 class="down">
                                                    <div class='panel_kit'>
                                                        <div class='title_p'>
                                                            <div id='<%=devcapsule.getId()%>_title_dev'><%=devcapsule.getDevice().getName()%> , <%=devcapsule.getDevice().getDescription()%></div>
                                                            <div class='title_ex'>down: <%=format.format(date_in_dev)%></div>
                                                        </div>
                                                    </div>
                                                </h1>
                                            </div>
                                            <%
                                                    }
                                                }
                                            %>

                                        </div>
                                    </div>
                                </div>

                                <div class='text_bold title_rubric'>Затронутые сервисы:</div>
                                <div id='<%=t.getId()%>_service' class='content_block_inactive'><%=serv%></div>
                                <input type='hidden' value='<%=serv_id%>'/>

                                <div class='text_bold title_rubric'>Дата и время аварии:</div>
                                <div id='<%=t.getId()%>_date_in' class='content_block_inactive'><%=format.format(date_in)%></div>

                                <div class='text_bold title_rubric'>Сроки устранения:</div>
                                <div id='<%=t.getId()%>_timeout' class='content_block_inactive'><%=t.getTimeout() == null ? "" : format.format(timeout)%></div>

                                <div class='text_bold title_rubric'>Фактическая проблема:</div>
                                <div id='<%=t.getId()%>_actual_problem' class='content_block_inactive'><%=t.getActualProblem()%></div>

                                <div class='text_bold title_rubric'>Комментарии:</div>
                                <div class="trouble_comments">
                                    <%
                                        List<Comment> comments = t.getComments();
                                        for (Comment comment : comments) {
                                            Date date_comment = new Date(Long.valueOf(comment.getTime() != null ? comment.getTime() : "0"));
                                    %>
                                            <div class="comment_item">
                                                <div class="comment_title">
                                                    <div class="comment_author"><%=comment.getAuthor().getFio()%></div>
                                                    <div class="comment_date"><%=format.format(date_comment)%></div>
                                                </div>
                                                <div class="comment_text"><%=comment.getText()%></div>
                                            </div>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
        <%
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>

</div>

<input type="hidden" id="openControlPanel_trash_troubles" value="<%=((Users)session.getAttribute("info")).getSettings_id().getOpenControlPanel()%>"/>