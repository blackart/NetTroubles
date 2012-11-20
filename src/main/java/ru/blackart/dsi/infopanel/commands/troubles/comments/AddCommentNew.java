package ru.blackart.dsi.infopanel.commands.troubles.comments;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.crm.CrmComment;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.view.CommentView;
import ru.blackart.dsi.infopanel.view.UserView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddCommentNew extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    CommentService commentService = CommentService.getInstance();

    @Override
    public String execute() throws Exception {
        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        String text = this.getRequest().getParameter("text");
        User author = (User) this.getSession().getAttribute("info");

        Calendar calendar = Calendar.getInstance();

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTime(String.valueOf(calendar.getTimeInMillis()));
        comment.setAuthor(author);

        synchronized (dataModel) {
            Trouble trouble = dataModel.getTroubleForId(id);

            if (trouble.getCrm()) {
                //Отправляем комменет в CRM
                CrmComment crmComment = new CrmComment(trouble, comment);
                crmComment.send();
                comment.setCrm(true);
            } else {
                comment.setCrm(false);
            }
            //сохраняем комент в DB

            synchronized (commentService) {
                commentService.save(comment);
            }
            /**
             * проверяем, есть ли у проблемы комментарии, если есть, то добавляем к ним новый комментарий, если нет,
             * то создаём новый массив коментариев и добавляем к нему коментарий.
             */
            List<Comment> comments = trouble.getComments() != null ? trouble.getComments() : new ArrayList<Comment>();
            comments.add(comment);
            trouble.setComments(comments);

            synchronized (troubleService) {
                troubleService.update(trouble);
            }

            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd/MM/yyyy HH:mm:ss");

            UserView userView = new UserView();
            userView.setFio(author.getFio());

            CommentView commentView = new CommentView();
            commentView.setCrm(comment.getCrm());
            commentView.setText(comment.getText());
            commentView.setTime(comment.getTime());
            commentView.setAuthor(userView);

            Gson gson = new Gson();

            this.getResponse().getWriter().write(gson.toJson(commentView));
        }
        return null;
    }
}