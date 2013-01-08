package ru.blackart.dsi.infopanel.commands.reports;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.utils.searching.SearchParam;

public class GetTroubleReport extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        String searchParam_str = this.getRequest().getParameter("param");

        Gson gson = new Gson();
        SearchParam searchParam = gson.fromJson(searchParam_str, SearchParam.class);


        return null;
    }
}
