package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Hoststatus;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: blackart
 * Date: 30.12.12
 * Time: 1:02
 * To change this template use File | Settings | File Templates.
 */
public class GetHostStatuses extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        ArrayList<Hoststatus> hostStatuses = (ArrayList<Hoststatus>) this.getServletContext().getAttribute("hoststatuses");
        Gson gson = new Gson();

        this.getResponse().getWriter().print(gson.toJson(hostStatuses));
        return null;
    }
}
