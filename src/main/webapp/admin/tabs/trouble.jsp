<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.*" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Service> services = (ArrayList<Service>) config.getServletContext().getAttribute("services");
    ArrayList<Region> regions = (ArrayList<Region>) config.getServletContext().getAttribute("regions");
%>
<script type="text/javascript" src="../js/trouble.js"></script>

<div class="menu_top">
    <div class="control_panel">
        <div class="button_panel_failures">
            <div class="merge_panel">
                <input type="checkbox" id="checkall">
                <input type="button" id="merge" value="merge"/>
            </div>
        </div>
        <div class="button_print">
            <span id="current_timeout"></span>
            Интервал обновления:
            <select id="interval_val">
                <option value="1800000">30 минут</option>
                <option value="1500000">25 минут</option>
                <option value="1200000">20 минут</option>
                <option value="900000">15 минут</option>
                <option value="600000">10 минут</option>
                <option value="10000">10 секунд</option>
                <option value="5000">5 секунд</option>
            </select>
            <input type="button" value="set interval" id="set_interval"/>
            <input type="button" value="start reload" id="reload_page">
            <input type="button" value="обновить" id="refresh_trouble_list"/>
        </div>
    </div>
    <div class="bottom_button" id="troubles_page_control_panel">панель управления</div>
</div>

<div class="troubles_lists">
    <div id="admin_need_actual_problem_trouble_list">
        <%
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd/MM/yyyy HH:mm:ss");

            TroubleList needActualProblemTroubleList = (TroubleList) config.getServletContext().getAttribute("needActualProblemTroubleList");
            List<Trouble> needActualProblemTroubles = needActualProblemTroubleList.getTroubles();

            if (needActualProblemTroubles.size() != 0) {
                out.print("<div class=\"trouble_list_title\">Проблемы решены, отправлены в CRM, отсутствует фактическая проблема:</div>");
            }

            try {
                for (Trouble t : needActualProblemTroubles) {
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
        <div class='trouble_item'>
            <div class='trouble_item_check'><input type='checkbox'/></div>
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
            <div id='<%=t.getId()%>_block_item_admin_trouble_lists' class='trouble_item_accord'>
                <h3>
                    <div class='panel_kit'>
                        <div class='title_p'>
                            <div id='<%=t.getId()%>_title_item_admin_trouble_list'><%=t.getTitle()%></div>
                            <div class='title_ex'>Время аварии: <%=format.format(date_in)%>  <%if (t.getDate_out() != null) {%>|  Время устранения аварии: <%=format.format(date_out)%><%}%>  |  Регион: <%=region_str%></div>
                        </div>
                    </div>
                </h3>
                <div class='content' id='<%=t.getId()%>'>

                    <div class='text_bold title_rubric'>Заголовок:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_title' class='cont_rubric'><%=t.getTitle()%></div>

                    <div class='text_bold title_rubric'>Список узлов:</div>
                    <div class="short_dev_list"></div>
                    <div id='<%=t.getId()%>_dev_list' class='dev_list_up'>
                        <div>
                            <%
                                for (Devcapsule devcapsule : t.getDevcapsules()) {
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
                                        <%if (t.getDevcapsules().size() > 1) {%>
                                            <div class="func">
                                                <div><input type="button" id="<%=devcapsule.getId()%>_unmerge" value="unmerge"/></div>
                                            </div>
                                        <%}%>
                                    </div>

                                </h1>
                            </div>
                            <%
                                }
                            %>

                        </div>
                    </div>

                    <div class='text_bold title_rubric'>Затронутые сервисы:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_service' class='service_sel'><%=serv%></div>
                    <input type='hidden' value='<%=serv_id%>'/>

                    <input type="hidden" id="<%=t.getId()%>_timeout" value="<%=t.getTimeout() == null ? "" : format.format(timeout)%>"/>

                    <div class='text_bold title_rubric'>Фактическая проблема:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_actual_problem' class='actual_problem'><%=t.getActualProblem()%></div>

                    <div class='text_bold title_rubric'>Комментарии:<div class="redstar">*</div></div>
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
                        <div class="comment_send">
                            <textarea rows="4" cols="5"></textarea>
                            <input type="button" class="comment_send_button" value="отправить"/>
                        </div>
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
    <div id="admin_waiting_close_trouble_list">
        <%
            TroubleList waitingCloseTroubleList = (TroubleList) config.getServletContext().getAttribute("waitingCloseTroubleList");
            List<Trouble> waitingCloseTroubles = waitingCloseTroubleList.getTroubles();

            if (waitingCloseTroubles.size() != 0) {
                out.print("<div class=\"trouble_list_title\">Разрешенные проблемы, но не отправленные в CRM:</div>");
            }

            try {
                for (Trouble t : waitingCloseTroubles) {
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
        <div class='trouble_item'>
            <div class='trouble_item_check'><input type='checkbox'/></div>
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
            <div id='<%=t.getId()%>_block_item_admin_trouble_lists' class='trouble_item_accord'>
                <h3>
                    <div class='panel_kit'>
                        <div class='title_p'>
                            <div id='<%=t.getId()%>_title_item_admin_trouble_list'><%=t.getTitle()%></div>
                            <div class='title_ex'>Время аварии: <%=format.format(date_in)%>  <%if (t.getDate_out() != null) {%>|  Время устранения аварии: <%=format.format(date_out)%><%}%>  |  Регион: <%=region_str%></div>
                        </div>
                    </div>
                </h3>
                <div class='content' id='<%=t.getId()%>'>

                    <div class='text_bold title_rubric'>Заголовок:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_title' class='cont_rubric'><%=t.getTitle()%></div>

                    <div class='text_bold title_rubric'>Список узлов:</div>
                    <div class="short_dev_list"></div>
                    <div id='<%=t.getId()%>_dev_list' class='dev_list_up'>
                        <div>
                            <%
                                for (Devcapsule devcapsule : t.getDevcapsules()) {
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
                                        <%if (t.getDevcapsules().size() > 1) {%>
                                            <div class="func">
                                                <div><input type="button" id="<%=devcapsule.getId()%>_unmerge" value="unmerge"/></div>
                                            </div>
                                        <%}%>
                                    </div>

                                </h1>
                            </div>
                            <%
                                }
                            %>

                        </div>
                    </div>

                    <div class='text_bold title_rubric'>Затронутые сервисы:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_service' class='service_sel'><%=serv%></div>
                    <input type='hidden' value='<%=serv_id%>'/>

                    <input type="hidden" id="<%=t.getId()%>_timeout" value="<%=t.getTimeout() == null ? "" : format.format(timeout)%>"/>

                    <div class='text_bold title_rubric'>Фактическая проблема:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_actual_problem' class='actual_problem'><%=t.getActualProblem()%></div>

                    <div class='text_bold title_rubric'>Комментарии:<div class="redstar">*</div></div>
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
                        <div class="comment_send">
                            <textarea rows="4" cols="5"></textarea>
                            <input type="button" class="comment_send_button" value="отправить"/>
                        </div>
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
    <div id="admin_trouble_list">
        <div class="trouble_list_title">Текущие проблемы:</div>
        <%
            TroubleList currTroubleList = (TroubleList) config.getServletContext().getAttribute("currTroubleList");
            List<Trouble> currTroubles = currTroubleList.getTroubles();

            try {
                for (Trouble t : currTroubles) {
                    if (!t.getClose()) {
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
        <div class='trouble_item'>
            <div class='trouble_item_check'><input type='checkbox'/></div>
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
            <div id='<%=t.getId()%>_block_item_admin_trouble_lists' class='trouble_item_accord'>
                <h3>
                    <div class='panel_kit'>
                        <div class='title_p'>
                            <div id='<%=t.getId()%>_title_item_admin_trouble_list'><%=t.getTitle()%></div>
                            <div class='title_ex'>Время аварии: <%=format.format(date_in)%>  |  Регион: <%=region_str%></div>
                            <% if (t.getCrm()) {%><div class="title_crm_status">Отправлено в CRM</div><%}%>
                            <div class="title_timeout"></div>
                        </div>
                    </div>
                </h3>
                <div class='content' id='<%=t.getId()%>'>

                    <div class='text_bold title_rubric'>Заголовок:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_title' class='cont_rubric'><%=t.getTitle()%></div>

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
                                                <div id='<%=devcapsule.getId()%>_title_dev'><%=devcapsule.getDevice().getName()%>
                                                    , <%=devcapsule.getDevice().getDescription()%>
                                                </div>
                                                <div class='title_ex'>down: <%=format.format(date_in_dev)%> up: <%=format.format(date_out_dev)%></div>
                                            </div>
                                            <%if (t.getDevcapsules().size() > 1) {%>
                                                <div class="func">
                                                    <div><input type="button" id="<%=devcapsule.getId()%>_unmerge" value="unmerge"/></div>
                                                </div>
                                            <%}%>
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
                                            <%if (t.getDevcapsules().size() > 1) {%>
                                                <div class="func">
                                                    <div><input type="button" id="<%=devcapsule.getId()%>_unmerge" value="unmerge"/></div>
                                                </div>
                                            <%}%>
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

                    <div class='text_bold title_rubric'>Затронутые сервисы:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_service' class='service_sel'><%=serv%></div>
                    <input type='hidden' value='<%=serv_id%>'/>

                    <div class='text_bold title_rubric'>Сроки устранения:<div class="redstar">*</div></div>
                    <div id='<%=t.getId()%>_timeout' class='timeout'><%=t.getTimeout() == null ? "" : format.format(timeout)%></div>

                    <div class='text_bold title_rubric'>Фактическая проблема:</div>
                    <div id='<%=t.getId()%>_actual_problem' class='actual_problem'><%=t.getActualProblem()%></div>

                    <div class='text_bold title_rubric'>Комментарии:<div class="redstar">*</div></div>
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
                        <div class="comment_send">
                            <textarea rows="4" cols="5"></textarea>
                            <input type="button" class="comment_send_button" value="добавить комментарий"/>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <%
                    }
                }
            } catch (Exception e) {

            }
        %>
    </div>
</div>

<input type="hidden" id="openControlPanel" value="<%=((User)session.getAttribute("info")).getSettings_id().getOpenControlPanel()%>"/>
<input type="hidden" id="pageReload" value="<%=((User)session.getAttribute("info")).getSettings_id().getCurrentTroublesPageReload()%>"/>
<input type="hidden" id="timeoutReloadPage" value="<%=((User)session.getAttribute("info")).getSettings_id().getTimeoutReload()%>"/>
<input type="hidden" id="service_sel" value="
    <%if (services != null) {
        for (Service s : services) {%>
            <%=s.getName() + "|" + s.getId() + ";"%>
        <%}
    }
%>
"/>
