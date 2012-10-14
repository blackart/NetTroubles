<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessUserObject" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessTab" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.*" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessItemMenu" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="ru.blackart.dsi.infopanel.utils.model.DataModelConstructor" %>
<%@ page import="ru.blackart.dsi.infopanel.access.menu.*" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Group" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
//    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    /*Enumeration en = session.getAttributeNames();
    log.info("Session elements: ");
    while (en.hasMoreElements()) {
        String elem = String.valueOf(en.nextElement());
        log.info(elem + " - " + session.getAttribute(elem));
    }*/

    HttpSession req_session = request.getSession(true);

    if (req_session.getAttribute("login") != null) {
        if (!(Boolean) req_session.getAttribute("login")) {
//            log.info("++++++++++++++++++++++++++ Not login !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            response.sendRedirect("/login");
        } else {
            if (req_session.getAttribute("page").equals("admin")) {
//                log.info("++++++++++++++++++++++++++ login true !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                response.sendRedirect("/admin");
            } else {
//                log.info("++++++++++++++++++++++++++ page - not admin !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                response.sendRedirect("/login");
            }
        }
    } else {
//        log.info("++++++++++++++++++++++++++ Login is null !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        response.sendRedirect("/login");
    }
%>
<%
    ArrayList<Service> services = (ArrayList<Service>) config.getServletContext().getAttribute("services");
    ArrayList<TypeDeviceFilter> typeDeviceFilters = (ArrayList<TypeDeviceFilter>) config.getServletContext().getAttribute("typeDeviceFilters");
    ArrayList<Group> groups = (ArrayList<Group>) config.getServletContext().getAttribute("groups");
    ArrayList<Users> users = (ArrayList<Users>) config.getServletContext().getAttribute("users");
    ArrayList<AccessItemMenu> generalMenu = (ArrayList<AccessItemMenu>) config.getServletContext().getAttribute("generalMenu");
    ArrayList<Hostgroup> hostgroups = (ArrayList<Hostgroup>) config.getServletContext().getAttribute("hostgroups");
    ArrayList<Hoststatus> hoststatuses = (ArrayList<Hoststatus>) config.getServletContext().getAttribute("hoststatuses");
    ArrayList<Region> regions = (ArrayList<Region>) config.getServletContext().getAttribute("regions");
%>

<%
    AccessUserObject accessUserObject = (AccessUserObject) session.getAttribute("access");
%>


<html>
<head>
    <title>Административная панель</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, no-store, must-revalidate">
    <META HTTP-EQUIV="REFRESH" CONTENT="1800">           <!--1800 - 30 минут-->
    <!-- jQuery -->
    <%--<script type="text/javascript" src="../js/jQuery/js/jquery-1.5.1.js"></script>--%>
    <script type="text/javascript" src="../js/jQuery/js/jquery-1.8.2.min.js"></script>
    <script type="text/javascript" src="../js/jQuery/js/jquery.form.js"></script>
    <script type="text/javascript" src="../js/jQuery/js/jquery.clickToForm.js"></script>

    <!-- UI -->
    <script type="text/javascript" src="../js/jQuery/js/jquery-ui-1.8.2.custom.min.js"></script>
    <script type="text/javascript" src="../js/jQuery/js/jquery.timepicker.js"></script>

    <!--CSS style-->
    <link rel="stylesheet" type="text/css" href="../css/jquery/jquery-ui-1.8.1.custom.css">
    <link rel="stylesheet" type="text/css" href="../css/style_adminko.css"/>
    <link rel="stylesheet" type="text/css" href="../css/tabs-vertical.css"/>
    <link rel="stylesheet" type="text/css" href="../css/jquery.clickToForm.css"/>
    <link rel="stylesheet" type="text/css" href="../css/accordion_dev.css"/>
    <link rel="stylesheet" type="text/css" href="../css/accordion_main_menu.css"/>
    <link rel="stylesheet" type="text/css" href="../css/accordion.css"/>
    <link rel="stylesheet" type="text/css" href="../css/dialog.css"/>
    <link rel="stylesheet" type="text/css" href="../css/settings.css">
    <link rel="stylesheet" type="text/css" href="../css/top_menu.css">
    <%--<link rel="stylesheet" type="text/css" href="../css/ui-datepicker.css">--%>

    <!-- markItUp! -->
    <%--<script type="text/javascript" src="../js/markitup/markitup/jquery.markitup.pack.js"></script>
    <script type="text/javascript" src="../js/markitup/markitup/sets/html/set.js"></script>
    <link rel="stylesheet" type="text/css" href="../js/markitup/markitup/skins/markitup/style.css"/>
    <link rel="stylesheet" type="text/css" href="../js/markitup/markitup/sets/html/style.css"/>--%>

    <!--JS-->
    <script type="text/javascript" src="../js/adminko.js"></script>
    <%
        if ((session.getAttribute("login") != null) && ((Boolean)(session.getAttribute("login")))) {
            Users user = (Users)session.getAttribute("info");
            if ((Boolean)session.getAttribute("change_passwd")) {%>
                <script type="text/javascript">
                    $(document).ready(function() {
                         $("#change_passwd_dialog").dialog({ autoOpen: false, title: "Change passwd for login [<%=user.getLogin()%>]", position: "center", modal: true, resizable: false, draggable: true,
                            buttons: {
                                "ok": function() {
                                    var $passwd = $("#change_passwd_new_passwd").val();
                                    $.ajax({
                                        url : "/controller",
                                        type : "POST",
                                        data : {
                                            cmd: "changePasswd",
                                            id: <%=user.getId()%>,
                                            passwd: $passwd
                                        },
                                        beforeSend: function() {
                                            if ($.trim($passwd) === '') {
                                                alert("Введите пароль!");
                                                return false;
                                            }
                                        }/*,
                                        success: function(data) {
                                            $("#v_tabs").tabs('load', $("#v_tabs").tabs('option', 'selected'));
                                        }*/
                                    });

                                    $(this).dialog("close");
                                }
                            }
                        });
                        $("#change_passwd_dialog").dialog('open');
                    });
                </script>
    <%      }
        }
    %>
</head>
<body>

<div style="display: none;">
    <select id="host_status_replace">
        <%for (Hoststatus hs : hoststatuses) {%>
            <option value="<%=hs.getId()%>"><%=hs.getName()%></option>
        <%}%>
    </select>
    <select id="host_group_replace">
        <%for (Hostgroup hg : hostgroups) {%>
            <option value="<%=hg.getId()%>"><%=hg.getName()%></option>
        <%}%>
    </select>
    <select id="host_region_replace">
        <%for (Region r : regions) {%>
            <option value="<%=r.getId()%>"><%=r.getName()%></option>
        <%}%>
    </select>
</div>

<div id="change_passwd_dialog" class="settings_dialog" style="display: none;">
    <div style="margin: 10px 0 10px 0; font-size:9pt;">Смените пароль для вашей учётной записи:</div>
    <input type="text" id="change_passwd_new_passwd"/>
</div>

<div id="append_hostgroups" style="display: none;">
    <select>
        <%for (Hostgroup hg : hostgroups) {%>
            <option value="<%=hg.getNum()%>"><%=hg.getName()%></option>
        <%}%>
    </select>
</div>

<div id="merge_troubles_dialog" style="display: none;">
    <input type="hidden" id="ids_merge">

    <div class="caption_merge">Заголовок:<div class="redstar">*</div></div>
    <div class="field_merge">
        <select name="title_merge" id="title_merge"></select>
    </div>

    <div class="caption_merge">Затронутые сервисы:</div>
    <div class="field_merge">
        <select name="service_merge" id="service_merge" size="5" multiple>
            <%
                if (services != null) {
                    for (Service s : services) {
            %>
                        <option id="<%=s.getId()%>_service_merge"><%=s.getName()%></option>
            <%
                    }
                }
            %>
        </select>
    </div>

    <div class="caption_merge">Фактическая проблема:</div>
    <div class="field_merge">
        <select name="actual_problem_merge" id="actual_problem_merge"></select>
    </div>

    <div class="caption_merge">Список узлов:</div>
    <div class="field_merge">
        <select name="dev_list_merge" id="dev_list_merge" size="6" multiple></select>
    </div>
</div>

<div id="device_change_dialog" class="settings_dialog" style="display: none;">
    <table id="devices_change_list_table" class="settings_table" cellpadding="0" cellspacing="0">

    </table>
</div>

<div id="settings_edit_dialog" class="settings_dialog" style="display: none;">
        <input type="hidden" id="settings_main_filter_edit_id" />
        <table class="settings_table" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>value</td>
                    <td>type</td>
                    <td>policy</td>
                    <td>enable</td>
                </tr>
                <tr>
                    <td width="15%"><input type="text" id="settings_main_filter_edit_name"/></td>
                    <td width="40%" id="filter_edit_value"><input type="text"/></td>
                    <td width="15%">
                        <select id="settings_main_filter_edit_type">
                            <%for (TypeDeviceFilter tdf : typeDeviceFilters) {%>
                                <option value="<%=tdf.getId()%>"><%=tdf.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="15%">
                        <select id="settings_main_filter_edit_policy">
                            <option>true</option>
                            <option>false</option>
                        </select>
                    </td>
                    <td width="10%">
                        <select id="settings_main_filter_edit_enable">
                            <option>true</option>
                            <option>false</option>
                        </select>
                    </td>
                </tr>
            </table>
</div>
<div id="settings_delete_dialog" style="display: none;">
    <div>Удалить фильтр <strong id="delete_main_filter" ></strong> ?</div>
</div>

<div id="users_edit_dialog" class="settings_dialog" style="display: none;">
        <input type="hidden" id="users_edit_id" />
        <table class="settings_table" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>login</td>
                    <td>passwd</td>
                    <td>name</td>
                    <td>group</td>
                    <td>block</td>
                </tr>
                <tr>
                    <td width="20%"><input type="text" id="users_edit_login"/></td>
                    <td width="20%"><input type="text" id="users_edit_passwd"/></td>
                    <td width="30%"><input type="text" id="users_edit_name"/></td>
                    <td width="15%">
                        <select id="users_edit_group">
                            <%for (Group g : groups) {%>
                                <option value="<%=g.getId()%>"><%=g.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="5%"><input type="checkbox" id="users_edit_block"/></td>
                </tr>
        </table>
</div>
<div id="users_delete_dialog" style="display: none;">
    <div>Удалить фильтр <strong id="delete_user" ></strong> ?</div>
</div>

<div id="groups_edit_dialog" class="settings_dialog" style="display: none;">
        <input type="hidden" id="groups_edit_id" />
        <table class="settings_table_group" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>items</td>
                </tr>
                <tr>
                    <td width="40%"><input type="text" id="groups_edit_name"/></td>
                    <td width="60%">


                        <div class="settings">
                            <div class="settings_block">
                                <div class="menu_items">
                                        <ul class="l1">
                                            <%
                                                for (AccessItemMenu item : generalMenu) {
                                                    if (item.getChildrens().size() == 0) {%>
                                                        <li id="<%=item.getTab().getTab().getId()%>_<%=item.getTab().getTab().getMenu_group()%>_main_d"><input type="checkbox"/><%=item.getTab().getTab().getCaption()%></li>
                                                    <%
                                                    } else {
                                                    %>
                                                        <li id="<%=item.getTab().getTab().getId()%>_<%=item.getTab().getTab().getMenu_group()%>_main_d"><input type="checkbox"/><%=item.getTab().getTab().getCaption()%></li>
                                                        <ul class="l2">
                                                        <%for (AccessTab tab : item.getChildrens()) {%>
                                                            <li id="<%=tab.getTab().getId()%>_<%=tab.getTab().getMenu_group()%>_child_d"><input type="checkbox"/><%=tab.getTab().getCaption()%></li>
                                                        <%}%>
                                                        </ul>
                                                    <%}
                                                }
                                            %>
                                        </ul>
                                </div>
                            </div>
                        </div>


                    </td>
                </tr>
            </table>
</div>
<div id="groups_delete_dialog" style="display: none;">
    <div>Удалить фильтр <strong id="delete_group" ></strong> ?</div>
</div>

<div id="devices_edit_dialog" class="settings_dialog" style="display: none;">
    <input type="hidden" id="devices_edit_id"/>
    <table class="settings_table" cellpadding="0" cellspacing="0">
        <tr class="header">
            <td>name</td>
            <td>description</td>
            <td>status</td>
            <td>group</td>
            <td>region</td>
        </tr>
        <tr>
            <td width="15%"><input type="text" id="device_edit_name"/></td>
            <td width="40%"><input type="text" id="device_edit_description"/></td>
            <td width="15%">
                <select id="device_edit_status">

                </select>
            </td>
            <td width="15%">
                <select id="device_edit_group">

                </select>
            </td>
            <td width="15%">
                <select id="device_edit_region">

                </select>
            </td>
        </tr>
    </table>
</div>
<div id="devices_delete_dialog" style="display: none;">
    <div>Удалить устройство <strong id="delete_device" ></strong> ?</div>
</div>

<div class="logout_bar"><%
    if ((session.getAttribute("login") != null) && ((Boolean) (session.getAttribute("login")))) {
            Users user = (Users) session.getAttribute("info");
    %><%=user.getLogin()%> (<%=user.getFio()%>) [<a href='' id='logout'>logout</a>]<%}
%></div>

<div id="main_menu">
    <div id="v_tabs">
        <ul>
            <%
                DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
                if (accessUserObject != null) {
                    Menu menu = accessUserObject.getMenu();
                    for (ru.blackart.dsi.infopanel.access.menu.Group group : menu.getGroups()) {
                        if ((group.getItems() == null) || (group.getItems().size() == 0)) {
                            %><li><a href="tabs/<%=group.getUrl()%>" class="menu_item"><%=group.getName()%></a></li><%
                        } else {
                            %><h3><%=group.getName()%></h3><div><%
                            for (Item item : group.getItems()) {
                                TroubleList troubleList = dataModelConstructor.getTroubleListForName(item.getName().toLowerCase());
                                String count_trobles = " ";
                                if (troubleList != null) {
                                        if (troubleList.getName().equals("current")) {
                                            count_trobles += "<div class='count_need_actual_problem_troubles'></div>/<div class='count_waiting_close_troubles'></div>/<div class='count_current_troubles'></div>";
                                        } else if (troubleList.getName().equals("complete")) {
                                            count_trobles += "<div class='count_complete_troubles'></div>";
                                        } else if (troubleList.getName().equals("trash")) {
                                            count_trobles += "<div class='count_trash_troubles'></div>";
                                        }
                                }
                                %><li><a href="tabs/<%=item.getUrl()%>"><%=item.getName()+ count_trobles%></a></li><%
                            }
                            %></div><%
                        }
                    }
                }
        %></ul>
    </div>
</div>
</body>
</html>