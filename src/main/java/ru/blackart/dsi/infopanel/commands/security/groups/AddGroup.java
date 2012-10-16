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

        HashMap<Integer, MenuItem> canonicalIndexingMenu = accessService.getCanonicalIndexingMenu();
        Menu newMenu = gson.fromJson(menu_config, Menu.class);


        for (MenuItem item0 : newMenu.getItems()) {
            if (canonicalIndexingMenu.containsKey(item0.getId())) {
                MenuItem canonicalMenuItem = canonicalIndexingMenu.get(item0.getId());
                item0.setName(canonicalMenuItem .getName());
                item0.setPosition(canonicalMenuItem.getPosition());
                item0.setUrl(canonicalMenuItem .getUrl());
                if (item0.getItems() != null) {
                    for (MenuItem item1 : item0.getItems()) {
                        if (canonicalIndexingMenu.containsKey(item1.getId())) {
                            canonicalMenuItem = canonicalIndexingMenu.get(item1.getId());
                            item1.setName(canonicalMenuItem .getName());
                            item1.setPosition(canonicalMenuItem.getPosition());
                            item1.setUrl(canonicalMenuItem .getUrl());
                        }
                    }
                }
            }
        }


        Group new_group = new Group();
        new_group.setName(group_name);
        new_group.setMenuConfig(gson.toJson(newMenu));

        synchronized (accessService) {
            accessService.saveGroup(new_group);
        }

        return null;
    }
}
