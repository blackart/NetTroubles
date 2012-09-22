<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Group" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Group> groups = (ArrayList<Group>) config.getServletContext().getAttribute("groups");
    ArrayList<Users> users = (ArrayList<Users>) config.getServletContext().getAttribute("users");
%>

    <script type="text/javascript" src="../js/users.js"></script>

    <div class="settings" >

        <div class="settings_block">
            <div class="title">Add account of user</div>

            <table id="users_add"  class="settings_add" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>login</td>
                    <td>passwd</td>
                    <td>name</td>
                    <td>group</td>
                    <td>block</td>
                    <td>add</td>
                </tr>
                <tr>
                    <td width="20%"><input type="text" class="account_login"/></td>
                    <td width="20%"><input type="text" class="account_passwd"/></td>
                    <td width="30%"><input type="text" class="account_name"/></td>
                    <td width="15%">
                        <select class="account_group">
                            <%for (Group g : groups) {%>
                                <option value="<%=g.getId()%>"><%=g.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="5%"><input type="checkbox" class="account_block"/></td>
                    <td width="10%"><input type="button" value="add" class='account_add_button'/></td>
                </tr>
            </table>

            <div class="title">Users</div>

            <table id="users_list_table" class="settings_list" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>login</td>
                    <td>passwd</td>
                    <td>name</td>
                    <td>group</td>
                    <td>block</td>
                    <td>edit</td>
                    <td>delete</td>
                </tr>
                <%
                    for (Users u : users) {
                        if (u.getLogin().equals("system")) {
                %>
                            <tr id="<%=u.getId()%>_user">
                                <td  class="account_login"><%=u.getLogin()%></td>
                                <td  class="account_passwd">********</td>
                                <td  class="account_name"><%=u.getFio()%></td>
                                <td  class="account_group"><%=u.getGroup_id().getName()%></td>
                                <td  class="account_block">immortal</td>
                                <td><input type="button" value="immortal" onclick="alert('I\'m immortal!!!');"/></td>
                                <td><input type="button" value="immortal" onclick="alert('I\'m immortal!!!');"/></td>
                            </tr>

                <%      } else {
                %>
                            <tr id="<%=u.getId()%>_user">
                                <td  class="account_login"><%=u.getLogin()%></td>
                                <td  class="account_passwd"><%for (int i=0; i < u.getPasswd().length(); i++) out.print("*");%></td>
                                <td  class="account_name"><%=u.getFio()%></td>
                                <td  class="account_group"><%=u.getGroup_id().getName()%></td>
                                <td  class="account_block"><%=u.getBlock() ? "true" : "false"%></td>
                                <td><input type="button" value="edit" class="account_edit_button"/></td>
                                <td><input type="button" value="delete" class="account_delete_button"/></td>
                            </tr>
                <%      }
                    }
                %>
            </table>

        </div>

    </div>