package ru.blackart.dsi.infopanel.commands.security.groups;

import com.google.gson.Gson;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.access.menu.MenuItem;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.access.AccessMenuForGroup;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Tab;
import ru.blackart.dsi.infopanel.services.AccessService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditGroup extends AbstractCommand {
    AccessService accessService = AccessService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Gson gson = new Gson();

    @Override
    public String execute() throws Exception {

        String group_id = this.getRequest().getParameter("id");
        String group_name = this.getRequest().getParameter("name");
        String menu_config = this.getRequest().getParameter("menu_config");

        int id;
        try {
            id = Integer.valueOf(group_id);
        } catch (Exception e ) {
            log.error("Can't cast id " + group_id + " to Integer type \n" + e.getMessage());
            return null;
        }

        Menu menu = accessService.resolveMenu(menu_config);
        Group group = accessService.getGroup(id);

        group.setName(group_name.trim());
        group.setMenuConfig(gson.toJson(menu));

        accessService.updateGroup(group);

        return null;
    }
}