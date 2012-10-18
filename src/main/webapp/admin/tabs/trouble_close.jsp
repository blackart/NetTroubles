<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.*" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Service> services = (ArrayList<Service>) config.getServletContext().getAttribute("services");
%>
<script type="text/javascript" src="../../js/trouble_close.js"></script>

<div class="menu_top">
    <div class="control_panel">
        <div class="button_panel_failures">
            <div class="merge_panel">
                <input type="checkbox" id="checkall_close_list">
                <input type="button" value="merge" id="merge_close_list"/>
            </div>
        </div>
        <div class="button_print">
            <form id="settings_trouble_close_list" method="post" action="/controller" accept-charset="UTF-8">
                дата: <input type="text" id="date_trouble_close_list" name="date"/>
                искать за:
                <select name="find_entry" id="find_entry">
                    <option value="1">день</option>
                    <option value="2">месяц</option>
                    <option value="3">год</option>
                </select>
                <input type="submit" value="поиск">
                <input type="hidden" name="cmd" value="getListOfClosedTroubles">
            </form>
        </div>
    </div>
    <div class="bottom_button" id="troubles_close_page_control_panel">панель поиска</div>
</div>

<div class="trouble_list_title">Закрытые проблемы:</div>
<div id="admin_trouble_close_list"></div>

<input type="hidden" id="openControlPanel_close_troubles" value="<%=((User)session.getAttribute("info")).getSettings_id().getOpenControlPanel()%>"/>
<input type="hidden" id="service_sel" value="
    <%if (services != null) {
        for (Service s : services) {%>
            <%=s.getName() + "|" + s.getId() + ";"%>
        <%}
    }
%>
"/>
