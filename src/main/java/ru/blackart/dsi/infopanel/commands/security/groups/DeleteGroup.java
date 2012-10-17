package ru.blackart.dsi.infopanel.commands.security.groups;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.access.AccessMenuForGroup;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Tab;
import ru.blackart.dsi.infopanel.services.AccessService;

import java.util.ArrayList;

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
            log.error("Can't cast id " + group_id + " to Integer type \n" + e.getMessage());
            return null;
        }
        synchronized (accessService) {
            accessService.deleteGroup(id);
        }

        return null;
    }
}