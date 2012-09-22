package ru.blackart.dsi.infopanel.commands.troubles.comments;

import org.hibernate.Criteria;
import org.hibernate.Session;
import ru.blackart.dsi.infopanel.commands.AbstractCommand;
import ru.blackart.dsi.infopanel.SessionFactorySingle;
import ru.blackart.dsi.infopanel.beans.Comment;
import ru.blackart.dsi.infopanel.beans.Trouble;

import java.util.ArrayList;
import java.util.List;

public class TransformDescToComment extends AbstractCommand {
    @Override
    public String execute() throws Exception {
        /*Session session = SessionFactorySingle.getSessionFactory().openSession();
        session.beginTransaction();

        Criteria crt_curr_trouble = session.createCriteria(Trouble.class);
        List<Trouble> troubles = crt_curr_trouble.list();

        for (Trouble t : troubles) {
            Comment comment = new Comment(t.getDescription(), t.getDate_in(), t.getAuthor(),);
            comment.setText(t.getDescription());
            comment.setAuthor();
            comment.setTime();
            session.save(comment);

            List<Comment> comments = new ArrayList<Comment>();
            comments.add(comment);
            t.setComments(comments);
            session.update(t);
        }

        session.getTransaction().commit();
        session.flush();
        session.close();*/

        return null;
    }
}
