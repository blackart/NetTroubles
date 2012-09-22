package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Devcapsule;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.DevcapsuleService;
import ru.blackart.dsi.infopanel.services.TroubleListService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.util.ArrayList;
import java.util.List;

public class ClearTrashList extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
        TroubleService troubleService = TroubleService.getInstance();
        DevcapsuleService devcapsuleService = DevcapsuleService.getInstance();
        CommentService commentService = CommentService.getInstance();
        TroubleListService troubleListService = TroubleListService.getInstance();

        TroubleList trashTroubleList = dataModelConstructor.getList_of_trash_troubles();
        List<Trouble> troubles = new ArrayList<Trouble>(trashTroubleList.getTroubles());

        for (Trouble t : troubles) {
            List<Devcapsule> devcapsules = new ArrayList<Devcapsule>(t.getDevcapsules());
            List<Comment> comments = new ArrayList<Comment>(t.getComments());

            if (t.getDevcapsules() != null) t.getDevcapsules().clear();
            if (t.getServices() != null) t.getServices().clear();
            if (t.getComments() != null) t.getComments().clear();
            troubleService.update(t);

            for (Devcapsule d : devcapsules) {
                devcapsuleService.delete(d);
            }
            for (Comment c : comments) {
                commentService.delete(c);
            }
        }

        trashTroubleList.getTroubles().clear();
        troubleListService.update(trashTroubleList);

        for (Trouble t : troubles) {
            troubleService.delete(t);
        }

        return null;
    }
}
