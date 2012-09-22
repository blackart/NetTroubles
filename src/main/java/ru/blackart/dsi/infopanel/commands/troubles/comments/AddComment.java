package ru.blackart.dsi.infopanel.commands.troubles.comments;

import com.myjavatools.xml.BasicXmlData;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.Users;
import ru.blackart.dsi.infopanel.services.CommentService;
import ru.blackart.dsi.infopanel.services.TroubleService;
import ru.blackart.dsi.infopanel.utils.crm.CrmComment;
import ru.blackart.dsi.infopanel.utils.model.DataModelConstructor;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddComment extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        int id = Integer.valueOf(this.getRequest().getParameter("id"));
        String text = this.getRequest().getParameter("text");

        DataModelConstructor dataModelConstructor = DataModelConstructor.getInstance();

        Calendar calendar = Calendar.getInstance();

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTime(String.valueOf(calendar.getTimeInMillis()));
        comment.setAuthor((Users) this.getSession().getAttribute("info"));

        TroubleService troubleService = TroubleService.getInstance();
        Trouble trouble = dataModelConstructor.getTroubleForId(id);

        if (trouble.getCrm()) {
            //Отправляем комменет в CRM
            CrmComment crmComment = new CrmComment(trouble, comment);
            crmComment.send();
            comment.setCrm(true);
        } else {
            comment.setCrm(false);
        }
        //сохраняем комент в DB
        CommentService commentService = CommentService.getInstance();
        commentService.save(comment);
        /**
         * проверяем, есть ли у проблемы комментарии, если есть, то добавляем к ним новый комментарий, если нет,
         * то создаём новый массив коментариев и добавляем к нему коментарий.
         */
        List<Comment> comments = trouble.getComments() != null ? trouble.getComments() : new ArrayList<Comment>();
        comments.add(comment);
        trouble.setComments(comments);

        troubleService.update(trouble);

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy HH:mm:ss");

        Date date = new Date(Long.valueOf(comment.getTime()));

        BasicXmlData xml = new BasicXmlData("comment");
        xml.addKid(new BasicXmlData("text", comment.getText()));
        xml.addKid(new BasicXmlData("date", format.format(date)));
        xml.addKid(new BasicXmlData("author", comment.getAuthor().getFio()));

        OutputStream out = getResponse().getOutputStream();
        xml.save(out);

        return null;
    }
}
