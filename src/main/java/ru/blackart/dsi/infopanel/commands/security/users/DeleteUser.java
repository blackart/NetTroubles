package ru.blackart.dsi.infopanel.commands.security.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.SimpleMessage;

public class DeleteUser extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final AccessService accessService = AccessService.getInstance();

    @Override
    public String execute() throws Exception {
        String user_id = this.getRequest().getParameter("id");

        int user_int_id;
        try {
            user_int_id = Integer.valueOf(user_id);
        } catch (Exception e ) {
            log.error("Can't cast id " + user_id + " to Integer type \n" + e.getMessage());
            return null;
        }

        synchronized (accessService) {
            if (!accessService.deleteUser(user_int_id)) {
                SimpleMessage message = new SimpleMessage("При удалении произошла ошибка. Аккаунт пользователя не удален.");
                this.getResponse().getWriter().print(message.toJson());
                return null;
            }
        }
        return null;
    }
}
