package ru.blackart.dsi.infopanel.commands.troubles;

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

public class ClearTrashList extends AbstractCommand {
    private DataModel dataModel = DataModel.getInstance();
    private TroubleService troubleService = TroubleService.getInstance();
    private DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
    private CommentService commentService = CommentService.getInstance();
    private TroubleListService troubleListService = TroubleListService.getInstance();

    @Override
    public String execute() throws Exception {
        synchronized (dataModel) {
            TroubleList trashTroubleList = dataModel.getList_of_trash_troubles();
            List<Trouble> troubles = new ArrayList<Trouble>(trashTroubleList.getTroubles());

            for (Trouble t : troubles) {
                List<Devcapsule> devcapsules = new ArrayList<Devcapsule>(t.getDevcapsules());
                List<Comment> comments = new ArrayList<Comment>(t.getComments());

                if (t.getDevcapsules() != null) t.getDevcapsules().clear();
                if (t.getServices() != null) t.getServices().clear();
                if (t.getComments() != null) t.getComments().clear();

                synchronized (troubleService) {
                    troubleService.update(t);
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
                trashTroubleList.getTroubles().clear();
                troubleListService.update(trashTroubleList);
            }

            synchronized (troubleService) {
                for (Trouble t : troubles) {
                    troubleService.delete(t);
                }
            }
        }
        return null;
    }
}
