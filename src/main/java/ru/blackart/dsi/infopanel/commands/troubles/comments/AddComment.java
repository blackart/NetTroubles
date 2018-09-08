package ru.blackart.dsi.infopanel.commands.troubles.comments;

import com.myjavatools.xml.BasicXmlData;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.User;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.crm.CrmComment;
import ru.blackart.dsi.infopanel.model.DataModel;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.TroubleService;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddComment extends AbstractCommand {
    DataModel dataModel = DataModel.getInstance();
    TroubleService troubleService = TroubleService.getInstance();
    CommentService commentService = CommentService.getInstance();

    @Override
    public String execute() throws Exception {
        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        String text = this.getRequest().getParameter("text");

        Calendar calendar = Calendar.getInstance();

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTime(String.valueOf(calendar.getTimeInMillis()));
        comment.setAuthor((User) this.getSession().getAttribute("info"));

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

            Date date = new Date(Long.valueOf(comment.getTime()));

            BasicXmlData xml = new BasicXmlData("comment");
            xml.addKid(new BasicXmlData("text", comment.getText()));
            xml.addKid(new BasicXmlData("date", format.format(date)));
            xml.addKid(new BasicXmlData("author", comment.getAuthor().getFio()));

            OutputStream out = getResponse().getOutputStream();
            xml.save(out);
        }
        return null;
    }
}
