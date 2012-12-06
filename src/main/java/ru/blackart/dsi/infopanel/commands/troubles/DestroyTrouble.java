package ru.blackart.dsi.infopanel.commands.troubles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.DevcapsuleService;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;

import java.util.ArrayList;
import java.util.List;

public class DestroyTrouble extends AbstractCommand {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private DataModel dataModel = DataModel.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
    private CommentService commentService = CommentService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();

    @Override
    public String execute() throws Exception {
        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        TroubleList troubleList;
        Trouble trouble;

        synchronized (dataModel) {
            trouble = dataModel.getTroubleForId(id);
            troubleList = dataModel.getTroubleListForTroubleId(id);

            List<Devcapsule> devcapsules = new ArrayList<Devcapsule>(trouble.getDevcapsules());
            List<Comment> comments = new ArrayList<Comment>(trouble.getComments());

            if (trouble.getDevcapsules() != null) trouble.getDevcapsules().clear();
            if (trouble.getServices() != null) trouble.getServices().clear();
            if (trouble.getComments() != null) trouble.getComments().clear();

            synchronized (troubleService) {
                troubleService.update(trouble);
            }

            synchronized (devcapsuleService) {
                for (Devcapsule d : devcapsules) {
                    devcapsuleService.delete(d);
                }
            }
            synchronized (commentService) {
                for (Comment c : comments) {
                    commentService.delete(c);
                }
            }
        }

        synchronized (troubleListService) {
            troubleList.getTroubles().remove(trouble);
            troubleListService.update(troubleList);
        }

        synchronized (troubleService) {
            troubleService.delete(trouble);
        }

        log.info("The trouble " + trouble.getTitle() + " has been destroyed");
        return null;
    }
}