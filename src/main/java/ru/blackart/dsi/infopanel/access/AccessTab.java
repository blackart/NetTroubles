package ru.blackart.dsi.infopanel.access;

import ru.blackart.dsi.infopanel.beans.Tab;

import java.util.List;

public class AccessTab {
    private boolean policy;
    private Tab tab;

    public AccessTab(boolean policy, Tab tab) {
        this.policy = policy;
        this.tab = tab;
    }

    public boolean isPolicy() {
        return policy;
    }

    public Tab getTab() {
        return tab;
    }

/*    private Tab tab;
private List<Tab> childrens;

public AccessTab(Tab mainTab, List<Tab> childrens) {
    this.tab = mainTab;
    this.childrens = sortTab(childrens);
}

private List<Tab> sortTab(List<Tab> tabs) {
    for (int i=0; i < tabs.size(); i++) {
        for (int j=0; j < i; j++) {
            if (tabs.get(i).getGroup_position() < tabs.get(j).getGroup_position()) {
                Tab replaceTab = tabs.get(i);
                tabs.remove(i);
                tabs.add(i,tabs.get(j));
                tabs.remove(j);
                tabs.add(i,replaceTab);
            }
        }
    }
    return tabs;
}

public List<Tab> getChildrens() {
    return childrens;
}

public void setChildrens(List<Tab> childrens) {
    this.childrens = childrens;
}

public Tab getTab() {
    return tab;
}

public void setTab(Tab tab) {
    this.tab = tab;
}*/
}
