package ru.blackart.dsi.infopanel.access;

import ru.blackart.dsi.infopanel.beans.Tab;

import java.io.Serializable;
import java.util.List;

public class AccessItemMenu implements Serializable {
    private int position;

    public int getPosition() {
        return position;
    }

    private AccessTab tab;
    private List<AccessTab> childrens;

    public AccessItemMenu(AccessTab mainTab, List<AccessTab> childrens, int position) {
        this.tab = mainTab;
        this.childrens = sortTab(childrens);
        this.position = position;
    }

    private List<AccessTab> sortTab(List<AccessTab> tabs) {
        for (int i=0; i < tabs.size(); i++) {
            for (int j=0; j < i; j++) {
                if (tabs.get(i).getTab().getGroup_position() < tabs.get(j).getTab().getGroup_position()) {
                    AccessTab replaceTab = tabs.get(i);
                    tabs.remove(i);
                    tabs.add(i,tabs.get(j));
                    tabs.remove(j);
                    tabs.add(i,replaceTab);
                }
            }
        }
        return tabs;
    }

    public AccessTab getTab() {
        return tab;
    }

    public List<AccessTab> getChildrens() {
        return childrens;
    }

/*    private int position;
private AccessTab item;

public AccessItemMenu(int position, AccessTab item) {
    this.position = position;
    this.item = item;
}

public int getPosition() {
    return position;
}

public void setPosition(int position) {
    this.position = position;
}

public AccessTab getItem() {
    return item;
}

public void setItem(AccessTab item) {
    this.item = item;
}*/
}
