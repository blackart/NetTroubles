<%@ page import="ru.blackart.dsi.infopanel.beans.Device" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Hoststatus" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="../js/failures.js"></script>

<div class="menu_top">
    <div class="find_panel">
        <div class="button_panel">
            <div class="start_find_panel">
                Параметр поиска:
                <div class="type_find_select">
                    <select id="type_find_select">
                        <option value="0"></option>
                        <option value="1">по имени</option>
                        <option value="2">по дате</option>
                        <option value="3">по статусу</option>
                    </select>
                </div>
            </div>
            <div class="other_panel"></div>
        </div>
        <div class="button_print"><input id="page_for_print" type="button" value="страница для печати" alt="страница для печати"/></div>
    </div>
    <div class="bottom_button" id="failures_page_control_panel">панель поиска</div>
</div>

<div id="failures" class="failures"></div>

<input type="hidden" id="openControlPanel_failures_list" value="<%=((User)session.getAttribute("info")).getSettings_id().getOpenControlPanel()%>"/>