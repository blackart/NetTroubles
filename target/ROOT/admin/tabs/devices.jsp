<%@ page import="ru.blackart.dsi.infopanel.beans.Device" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Hostgroup" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Hoststatus" %>
<%@ page import="ru.blackart.dsi.infopanel.beans.Region" %>
<%@ page import="java.util.Properties" %>
<%@ page import="ru.blackart.dsi.infopanel.commands.device.DeviceManager" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ page import="java.lang.reflect.Array" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
//    ArrayList<Device> devices = (ArrayList<Device>) config.getServletContext().getAttribute("deviceList");
    DeviceManager deviceManager = DeviceManager.getInstance();
    Properties devices = deviceManager.getDevice_list();

    ArrayList<Hostgroup> hostgroups = (ArrayList<Hostgroup>) config.getServletContext().getAttribute("hostgroups");
    ArrayList<Hoststatus> hoststatuses = (ArrayList<Hoststatus>) config.getServletContext().getAttribute("hoststatuses");
    ArrayList<Region> regions = (ArrayList<Region>) config.getServletContext().getAttribute("regions");
%>

<script type="text/javascript" src="../js/devices.js"></script>

<div class="settings" >

        <div class="settings_block">
            <div class="title">Добавить устройство</div>

            <table id="devices_add" class="settings_add" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>name</td>
                    <td>description</td>
                    <td>status</td>
                    <td>group</td>
                    <td>region</td>
                    <td>add</td>
                </tr>
                <tr>
                    <td width="15%"><input type="text" class="device_name"/></td>
                    <td width="30%"><input type="text" class="device_description"/></td>
                    <td width="15%">
                        <select class="device_status">
                            <%for (Hoststatus hs : hoststatuses) {%>
                                <option value="<%=hs.getId()%>"><%=hs.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="15%">
                        <select class="device_group">
                            <%for (Hostgroup hg : hostgroups) {%>
                                <option value="<%=hg.getId()%>"><%=hg.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="15%">
                        <select class="device_region">
                            <%for (Region r : regions) {%>
                                <option value="<%=r.getId()%>"><%=r.getName()%></option>
                            <%}%>
                        </select>
                    </td>
                    <td width="10%"><input type="button" value="add" class='add_device_button'/></td>
                </tr>
            </table>

            <div class="title">Таблица устройств</div>

            <table id="devices_list_table" class="settings_list" cellpadding="0" cellspacing="0">
                <tr class="header">
                    <td>№</td>
                    <td>name</td>
                    <td>description</td>
                    <td>status</td>
                    <td>group</td>
                    <td>region</td>
                    <td>edit</td>
                    <td>delete</td>
                </tr>
                <%
                    try {
                        int i = 0;
                        for (Enumeration en = devices.keys(); en.hasMoreElements();) {
                            String device_name = (String)en.nextElement();
                            Device device = (Device)devices.get(device_name);
                            i++;
                %>
                            <tr id="<%=device.getId()%>_device">
                                <td><%=i%></td>
                                <td  class="device_name"><%=device.getName()%></td>
                                <td  class="device_desc"><%=device.getDescription()%></td>
                                <td  class="device_host_status"><%=(device.getHoststatus() != null ? device.getHoststatus().getName() : "")%></td>
                                <td  class="device_host_group"><%=(device.getHostgroup() != null ? device.getHostgroup().getName() : "")%></td>
                                <td  class="device_host_region"><%=(device.getRegion() != null ? device.getRegion().getName() : "")%></td>
                                <td><input type="button" value="edit" class="edit_button"/></td>
                                <td><input type="button" value="delete" class="delete_button"/></td>
                            </tr>
                <%
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                %>
            </table>

        </div>

    </div>