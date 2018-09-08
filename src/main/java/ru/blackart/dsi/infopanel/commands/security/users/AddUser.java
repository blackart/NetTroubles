package ru.blackart.dsi.infopanel.commands.security.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.beans.UserSettings;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.AccessService;
import ru.blackart.dsi.infopanel.utils.message.SimpleMessage;

public class AddUser extends AbstractCommand {
    private final AccessService accessService = AccessService.getInstance();
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String execute() throws Exception {
        String login = this.getRequest().getParameter("login");
        String passwd = this.getRequest().getParameter("passwd");
        String name = this.getRequest().getParameter("name");
        String group_id = this.getRequest().getParameter("group");
        String block = this.getRequest().getParameter("block");

        int group_int_id;
        try {
            group_int_id = Integer.valueOf(group_id);
        } catch (Exception e ) {
            log.error("Can't cast id " + group_id + " to Integer type \n" + e.getMessage());
            return null;
        }

        synchronized (accessService) {
            Group group = accessService.getGroup(group_int_id);

            User user = new User();
            user.setLogin(login);
            user.setPasswd(passwd);
            user.setFio(name);
            user.setGroup_id(group);
            user.setBlock(Boolean.valueOf(block));

            UserSettings userSettings = new UserSettings();
            userSettings.setOpenControlPanel(false);
            userSettings.setCurrentTroublesPageReload(true);
            userSettings.setTimeoutReload("1200000");
            user.setSettings_id(userSettings);

            accessService.saveUserSettingsToDB(userSettings);
            if (!accessService.saveUser(user)) {
                SimpleMessage message = new SimpleMessage("При сохранении аккаунта произошла ошибка. Информация не сохранена.");
                this.getResponse().getWriter().print(message.toJson());
                return null;
            }
        }

        return null;
    }
}
