package ru.blackart.dsi.infopanel.commands.security.groups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.SimpleMessage;

public class DeleteGroup extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final AccessService accessService = AccessService.getInstance();

    @Override
    public String execute() throws Exception {
        String group_id = this.getRequest().getParameter("id");

        int id;
        try {
            id = Integer.valueOf(group_id);
        } catch (Exception e ) {
            SimpleMessage message = new SimpleMessage("Неверный ID группы");
            this.getResponse().getWriter().print(message.toJson());
            log.error("Can't cast id " + group_id + " to Integer type \n" + e.getMessage());
            return null;
        }
        synchronized (accessService) {
            if(!accessService.deleteGroup(id)) {
                SimpleMessage message = new SimpleMessage("При удалении произошла ошибка. Группа не удалена.");
                this.getResponse().getWriter().print(message.toJson());
                return null;
            }
        }

        return null;
    }
}