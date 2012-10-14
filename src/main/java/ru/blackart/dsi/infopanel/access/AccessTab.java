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
}
