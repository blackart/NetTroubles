package ru.blackart.dsi.infopanel.commands.security.groups;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.access.menu.MenuItem;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;

import java.util.HashMap;

public class AddGroup extends AbstractCommand {
    AccessService accessService = AccessService.getInstance();

    @Override
    public String execute() throws Exception {
        Gson gson = new Gson();

        String group_name = this.getRequest().getParameter("name");
        String menu_config = this.getRequest().getParameter("menu_config");

        Menu newMenu = accessService.resolveMenu(menu_config);

        Group new_group = new Group();
        new_group.setName(group_name);
        new_group.setMenuConfig(gson.toJson(newMenu));

        synchronized (accessService) {
            accessService.saveGroup(new_group);
        }

        return null;
    }
}
