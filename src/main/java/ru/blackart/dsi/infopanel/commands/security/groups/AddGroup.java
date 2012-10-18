package ru.blackart.dsi.infopanel.commands.security.groups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.SimpleMessage;

public class AddGroup extends AbstractCommand {
    AccessService accessService = AccessService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {
        String group_name = this.getRequest().getParameter("name");
        String menu_config = this.getRequest().getParameter("menu_config");

        Menu newMenu = accessService.resolveMenu(menu_config);
        if (newMenu == null) {
            SimpleMessage message = new SimpleMessage("Некорректная конфигурация меню");
            this.getResponse().getWriter().print(message.toJson());
            return null;
        }

        Group new_group = new Group();
        new_group.setName(group_name);
        new_group.setMenuConfig(newMenu.toJson());

        synchronized (accessService) {
            if (!accessService.saveGroup(new_group)) {
                SimpleMessage message = new SimpleMessage("Неудается добавить группу");
                this.getResponse().getWriter().print(message.toJson());
                return null;
            }
        }

        return null;
    }
}
