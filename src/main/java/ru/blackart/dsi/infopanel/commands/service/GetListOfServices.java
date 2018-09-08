package ru.blackart.dsi.infopanel.commands.service;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Service;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;

import java.util.ArrayList;

public class GetListOfServices extends AbstractCommand {

    @Override
    public String execute() throws Exception {
        Gson gson = new Gson();
        ArrayList<Service> services = (ArrayList<Service>) this.getServletContext().getAttribute("services");
        this.getResponse().getWriter().print(gson.toJson(services.toArray()));

        return null;
    }
}
