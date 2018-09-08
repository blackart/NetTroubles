package ru.blackart.dsi.infopanel.view;

import ru.blackart.dsi.infopanel.beans.Trouble;
import ru.blackart.dsi.infopanel.beans.TroubleList;
import ru.blackart.dsi.infopanel.beans.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CurrentsTroublesView {
    private static CurrentsTroublesView currentsTroublesView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat();
    private TroubleListView current = new TroubleListView();
    private TroubleListView wait = new TroubleListView();
    private TroubleListView actual = new TroubleListView();

    private void transformTroubleList(TroubleList model, TroubleListView view) {
        view.setId(model.getId());
        view.setName(model.getName());
        view.setTroubles(this.transformTroubles(model.getTroubles()));
    }

    private List<TroubleView> transformTroubles(List<Trouble> troubles) {
        List<TroubleView> troubleViewList = new ArrayList<TroubleView>();
        for (Trouble trouble : troubles) {
            troubleViewList.add(this.transformTrouble(trouble));
        }
        return troubleViewList;
    }

    private TroubleView transformTrouble(Trouble trouble) {
        this.dateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");

        TroubleView troubleView = new TroubleView();
        troubleView.setId(trouble.getId());
        troubleView.setTitle(trouble.getTitle());
        troubleView.setActualProblem(trouble.getActualProblem());

        troubleView.setTimeout(trouble.getTimeout());

        Date date_in = new Date(Long.valueOf(trouble.getDate_in() != null ? trouble.getDate_in() : "0"));
        troubleView.setDate_in(dateFormat.format(date_in));

        Date date_out = new Date(Long.valueOf(trouble.getDate_out() != null ? trouble.getDate_out() : "0"));
        troubleView.setDate_out(dateFormat.format(date_out));

        troubleView.setAuthor(this.transformUser(trouble.getAuthor()));

        return troubleView;
    }

    private UserView transformUser(User user) {
        UserView userView = new UserView();
        userView.setId(user.getId());
        userView.setBlock(user.getBlock());
        userView.setFio(user.getFio());
        userView.setLogin(user.getLogin());
        return userView;
    }

    public void init(TroubleList current, TroubleList wait, TroubleList actual) {
        this.transformTroubleList(current, this.current);
        this.transformTroubleList(wait, this.wait);
        this.transformTroubleList(actual, this.actual);
    }

    public static CurrentsTroublesView getInstance() {
        if (currentsTroublesView == null) {
            currentsTroublesView = new CurrentsTroublesView();
        }
        return currentsTroublesView;
    }

    public CurrentsTroublesView() {

    }

    public void getTrouble(int id) {

    }

    public void updateTrouble() {

    }

    public void deleteTrouble() {

    }
}
