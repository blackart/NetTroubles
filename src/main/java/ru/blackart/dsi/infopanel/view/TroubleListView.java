package ru.blackart.dsi.infopanel.view;
import java.util.List;

public class TroubleListView {
    private int id;
    private String name;
    private List<TroubleView> troubles;

    public TroubleListView() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TroubleView> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<TroubleView> troubles) {
        this.troubles = troubles;
    }
}
