package ru.blackart.dsi.infopanel.commands.security.groups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.SimpleMessage;

public class EditGroup extends AbstractCommand {
    AccessService accessService = AccessService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

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
        if (menu == null) {
            SimpleMessage message = new SimpleMessage("Неверная конфигурация меню");
            this.getResponse().getWriter().print(message.toJson());
            return null;
        }

        Group group = accessService.getGroup(id);
        group.setName(group_name.trim());
        group.setMenuConfig(menu.toJson());

        synchronized (accessService) {
            if (!accessService.updateGroup(group)) {
                SimpleMessage message = new SimpleMessage("При редактировании профиля группы произошла ошибка. Изменения не сохранены.");
                this.getResponse().getWriter().print(message.toJson());
                return null;
            }
        }

        return null;
    }
}