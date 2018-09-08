<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <div class="settings" >

        <div class="filters_list">
            <div class="title">Добавить фильтр</div>

            <table id="filters_add_table" class="filters_table" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>value</td>
                    <td>type</td>
                    <td>policy</td>
                    <td>add</td>
                </tr>
                <tr>
                    <td width="20%"><input type="text" class="filter_name"/></td>
                    <td width="50%"><input type="text" class="filter_value"/></td>
                    <td width="10%">
                        <select class="filter_type">
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
                    <td width="10%"><input type="button" value="add" class='add_filter_button'/></td>
                </tr>
            </table>

            <div class="title">Таблица фильтров</div>

            <table id="filters_list_table" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>value</td>
                    <td>type</td>
                    <td>policy</td>
                    <td>edit</td>
                    <td>delete</td>
                </tr>
                <%
                    for (DeviceFilter df : mainFilter) {%>
                        <tr id="<%=df.getId()%>_mainFilter">
                            <td  class="filter_name"><%=df.getName()%></td>
                            <td  class="filter_value"><%=df.getValue()%></td>
                            <td  class="filter_type"><%=df.getType().getName()%></td>
                            <td  class="filter_policy"><%=df.isPolicy()%></td>
                            <td><input type="button" value="edit" class="edit_button"/></td>
                            <td><input type="button" value="delete" class="delete_button"/></td>
                        </tr>
                    <%}
                 %>
            </table>

        </div>

    </div>