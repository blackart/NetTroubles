package ru.blackart.dsi.infopanel.commands.troubles;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Region;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: blackart
 * Date: 29.12.12
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class GetRegions extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        ArrayList<Region> regions = (ArrayList<Region>) this.getServletContext().getAttribute("regions");
        Gson gson = new Gson();

        this.getResponse().getWriter().print(gson.toJson(regions));
        return null;
    }
}
