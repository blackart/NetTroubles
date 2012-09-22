package ru.blackart.dsi.infopanel.commands.troubles;

import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.util.ArrayList;
import java.util.List;

public class ConvertLegendAndDescToComment extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        /*DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();
        TroubleService troubleService = TroubleService.getInstance();
        CommentService commentService = CommentService.getInstance();

        for (Trouble t : dataModelConstructor.getList_of_complete_troubles().getTroubles()) {
            Comment comment_1 = new Comment(t.getLegend(), t.getDate_in(), t.getAuthor(), true);
            Comment comment_2 = new Comment(t.getDescription(), t.getDate_in(), t.getAuthor(), true);
            commentService.save(comment_1);
            commentService.save(comment_2);

            List<Comment> comments = t.getComments() == null ? new ArrayList<Comment>() : t.getComments();
            comments.add(comment_1);
            comments.add(comment_2);

            t.setComments(comments);
            troubleService.update(t);
        }*/

        return null;
    }
}
