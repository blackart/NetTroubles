<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.*" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ArrayList<DeviceFilter> mainFilter = (ArrayList<DeviceFilter>) config.getServletContext().getAttribute("mainFilter");
    ArrayList<TypeDeviceFilter> typeDeviceFilters = (ArrayList<TypeDeviceFilter>) config.getServletContext().getAttribute("typeDeviceFilters");
    ArrayList<Hostgroup> hostgroups = (ArrayList<Hostgroup>) config.getServletContext().getAttribute("hostgroups");
%>

    <script type="text/javascript" src="../js/filters.js"></script>

    <div class="settings" >

        <div class="settings_block">
            <div class="title">Добавить фильтр</div>

            <table id="filters_add" class="settings_add" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>value</td>
                    <td>type</td>
                    <td>policy</td>
                    <td>enable</td>
                    <td>add</td>
                </tr>
                <tr>
                    <td width="20%"><input type="text" class="filter_name"/></td>
                    <td width="40%" id="filter_add_value"><input type="text" class="filter_value"/></td>
                    <td width="10%">
                        <select class="filter_type" id="filter_add_type">
                            <%for (TypeDeviceFilter tdf : typeDeviceFilters) {%>
                                <option value="<%=tdf.getId()%>"><%=tdf.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="10%">
                        <select class="filter_policy">
                            <option>true</option>
                            <option>false</option>
                        </select>
                    </td>
                    <td width="10%">
                        <select class="filter_enable">
                            <option>true</option>
                            <option>false</option>
                        </select>
                    </td>
                    <td width="10%"><input type="button" value="add" class='add_filter_button'/></td>
                </tr>
            </table>

            <div class="title">Таблица фильтров</div>

            <table id="filters_list_table" class="settings_list" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>value</td>
                    <td>type</td>
                    <td>policy</td>
                    <td>enable</td>
                    <td>edit</td>
                    <td>delete</td>
                </tr>
                <%
                    for (DeviceFilter df : mainFilter) {
                        String value = "";
                        if (df.getType().getName().equals("group")) {
                            for (Hostgroup hg : hostgroups) {
                                if (Integer.valueOf(hg.getNum()) == Integer.valueOf(df.getValue())) {
                                    value = hg.getName();
                                }
                            }
                        } else {
                            value = df.getValue();
                        }
                %>


                        <tr id="<%=df.getId()%>_mainFilter">
                            <td  class="filter_name"><%=df.getName()%></td>
                            <td  class="filter_value"><%=value%></td>
                            <td  class="filter_type"><%=df.getType().getName()%></td>
                            <td  class="filter_policy"><%=df.isPolicy()%></td>
                            <td  class="filter_enable"><%=df.isEnable()%></td>
                            <td><input type="button" value="edit" class="edit_button"/></td>
                            <td><input type="button" value="delete" class="delete_button"/></td>
                        </tr>
                    <%}
                 %>  
            </table>

        </div>

    </div>