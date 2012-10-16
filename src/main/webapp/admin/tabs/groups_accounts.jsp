<%@ page import="ru.blackart.dsi.infopanel.access.AccessItemMenu" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessMenuForGroup" %>
<%@ page import="ru.blackart.dsi.infopanel.access.menu.Menu" %>
<%@ page import="ru.blackart.dsi.infopanel.access.menu.MenuItem" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Group" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Tab" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Users" %>
<%@ page import="ru.blackart.dsi.infopanel.services.AccessService" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Group> groups = (ArrayList<Group>) config.getServletContext().getAttribute("groups");
    ArrayList<Users> users = (ArrayList<Users>) config.getServletContext().getAttribute("users");
    ArrayList<Tab> tabs = (ArrayList<Tab>) config.getServletContext().getAttribute("tabs");
    ArrayList<AccessMenuForGroup> tabs_of_groups = (ArrayList<AccessMenuForGroup>) config.getServletContext().getAttribute("tabs_of_groups");
    ArrayList<AccessItemMenu> generalMenu = (ArrayList<AccessItemMenu>) config.getServletContext().getAttribute("generalMenu");
    AccessService accessService = AccessService.getInstance();
%>

    <script type="text/javascript" src="../js/groups.js"></script>

    <div class="settings" >

        <div class="settings_block">
            <div class="title">Add group of account</div>

            <table id="groups_add"  class="settings_add" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>Name</td>
                    <td>Menu</td>
                    <td>Add</td>
                </tr>
                <tr>
                    <td width="40%"><div><input type="text" class="group_name"/></div></td>
                        <td width="50%" class="menu_items">
                                <ul class="l1">
                                    <%
                                        Menu menu = accessService.getCanonicalMenu();
                                        for (MenuItem group : menu.getItems()) {
                                            if (group.getItems() == null) {
                                                %><li class="group" id="group-add-<%=group.getId()%>"><input type="checkbox"/><%=group.getName()%></li><%
                                            } else {
                                                %><li class="group" id="group-add-<%=group.getId()%>"><input type="checkbox"/><%=group.getName()%></li><%
                                                %><ul class="l2"><%
                                                for (MenuItem item : group.getItems()) {
                                                    %><li class="item" id="group-add-<%=item.getId()%>"><input type="checkbox"/><%=item.getName()%></li><%
                                                }
                                                %></ul><%
                                            }
                                        }
                                    %>
                                </ul>
                    </td>
                    <td width="10%"><input type="button" value="add" class='group_add_button'/></td>
                </tr>
            </table>

            <div class="title">Groups</div>

            <table id="groups_list_table" class="settings_list" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td width="30%">Group name</td>
                    <td width="50%">Menu items</td>
                    <td width="10%">Edit</td>
                    <td width="10%">Delete</td>
                </tr>
                <%
                    HashMap<Integer, Menu> menuForGroups = accessService.getMenuForGroups();
                    HashMap<Integer, Group> groups_ = accessService.getGroups();

                    for (Group group_ : groups_.values()) {
                        if (group_.getName().equals("system")) {
                %>
                            <tr id="<%=group_.getId()%>_group" class="item">
                                <td class="group_name"><%=group_.getName()%></td>
                                <td class="menu_items">
                                    <ul class="l1">
                                        <li>all</li>
                                    </ul>
                                </td>

                                <td><input type="button" value="immortal" onclick="alert('I\'m immortal!!!');"/></td>
                                <td><input type="button" value="immortal" onclick="alert('I\'m immortal!!!');"/></td>
                            </tr>
                <%
                        } else {
                %>
                            <tr id="<%=group_.getId()%>_group" class="item">
                                <td class="group_name"><%=group_.getName()%></td>
                                <td class="menu_items">
                                    <ul class="l1">
                                        <%
                                            for (MenuItem item0 : menuForGroups.get(group_.getId()).getItems()) {
                                                if (item0.getItems() == null) {
                                                    %><li class="group" id="group-<%=group_.getId()%>-edit-<%=item0.getId()%>"><input type="checkbox"/><%=item0.getName()%></li><%
                                                } else {
                                                    %><li class="group" id="group-<%=group_.getId()%>-edit-<%=item0.getId()%>"><input type="checkbox"/><%=item0.getName()%></li><%
                                                    %><ul class="l2"><%
                                                        for (MenuItem item1 : item0.getItems()) {
                                                    %><li class="item" id="group-<%=group_.getId()%>-edit-<%=item1.getId()%>"><input type="checkbox"/><%=item1.getName()%></li><%
                                                    }
                                                %></ul><%
                                            }
                                            }
                                        %>
                                    </ul>
                                </td>

                                <td><input type="button" value="edit" class="group_edit_button"/></td>
                                <td><input type="button" value="delete" class="group_delete_button"/></td>
                            </tr>
                <%
                        }
                    }
                %>
            </table>

        </div>

    </div>