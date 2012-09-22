<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Group" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Users" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Tab" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessTab" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessItemMenu" %>
<%@ page import="ru.blackart.dsi.infopanel.access.AccessMenuForGroup" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Group> groups = (ArrayList<Group>) config.getServletContext().getAttribute("groups");
    ArrayList<Users> users = (ArrayList<Users>) config.getServletContext().getAttribute("users");
    ArrayList<Tab> tabs = (ArrayList<Tab>) config.getServletContext().getAttribute("tabs");
    ArrayList<AccessMenuForGroup> tabs_of_groups = (ArrayList<AccessMenuForGroup>) config.getServletContext().getAttribute("tabs_of_groups");
    ArrayList<AccessItemMenu> generalMenu = (ArrayList<AccessItemMenu>) config.getServletContext().getAttribute("generalMenu");
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
                                        for (AccessItemMenu item : generalMenu) {
                                            if (item.getChildrens().size() == 0) {%>
                                                <li id="<%=item.getTab().getTab().getId()%>_<%=item.getTab().getTab().getMenu_group()%>_main"><input type="checkbox"/><%=item.getTab().getTab().getCaption()%></li>
                                            <%
                                            } else {
                                            %>
                                                <li id="<%=item.getTab().getTab().getId()%>_<%=item.getTab().getTab().getMenu_group()%>_main"><input type="checkbox"/><%=item.getTab().getTab().getCaption()%></li>
                                                <ul class="l2">
                                                <%for (AccessTab tab : item.getChildrens()) {%>
                                                    <li id="<%=tab.getTab().getId()%>_<%=tab.getTab().getMenu_group()%>_child"><input type="checkbox"/><%=tab.getTab().getCaption()%></li>
                                                <%}%>
                                                </ul>
                                            <%}
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
                    for (AccessMenuForGroup menu : tabs_of_groups) {
                        if (menu.getGroup().getName().equals("system")) {
                %>
                            <tr id="<%=menu.getGroup().getId()%>_group" class="item">
                                <td class="group_name"><%=menu.getGroup().getName()%></td>
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
                            <tr id="<%=menu.getGroup().getId()%>_group" class="item">
                                <td class="group_name"><%=menu.getGroup().getName()%></td>
                                <td class="menu_items">
                                    <ul class="l1">
                                        <%
                                            for (AccessItemMenu item : menu.getItemMenu()) {
                                                if (item.getChildrens().size() == 0) {%>
                                                    <li id="<%=item.getTab().getTab().getId()%>_<%=item.getTab().getTab().getMenu_group()%>_main-<%=menu.getGroup().getId()%>"><input type="checkbox" <%=item.getTab().isPolicy() ? "checked=\"checked\"" : ""%> DISABLED/><%=item.getTab().getTab().getCaption()%></li>
                                                <%
                                                } else {
                                                %>
                                                    <li id="<%=item.getTab().getTab().getId()%>_<%=item.getTab().getTab().getMenu_group()%>_main-<%=menu.getGroup().getId()%>"><input type="checkbox" <%=item.getTab().isPolicy() ? "checked=\"checked\"" : ""%> DISABLED/><%=item.getTab().getTab().getCaption()%></li>
                                                    <ul class="l2">
                                                    <%for (AccessTab tab : item.getChildrens()) {%>
                                                        <li id="<%=tab.getTab().getId()%>_<%=tab.getTab().getMenu_group()%>_child-<%=menu.getGroup().getId()%>"><input type="checkbox" <%=tab.isPolicy() ? "checked=\"checked\"" : ""%> DISABLED/><%=tab.getTab().getCaption()%></li>
                                                    <%}%>
                                                    </ul>
                                                <%}
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