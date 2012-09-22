package ru.blackart.dsi.infopanel.access;

import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.Tab;

import java.util.ArrayList;
import java.util.List;

public class AccessMenuForGroup {
    private Group group;
    private List<AccessItemMenu> itemMenu;

    public AccessMenuForGroup(Group group, List<Tab> allTabs) {
        this.group = group;
        this.itemMenu = this.generateMenu(allTabs);
    }

    private List<AccessItemMenu> sortMenu(List<AccessItemMenu> menu) {
        for (int i=0; i < menu.size(); i++) {
            for (int j=0; j < i; j++) {
                if (menu.get(i).getPosition() < menu.get(j).getPosition()) {
                    AccessItemMenu replaceItem = menu.get(i);
                    menu.remove(i);
                    menu.add(i,menu.get(j));
                    menu.remove(j);
                    menu.add(i,replaceItem);
                }
            }
        }
        return menu;
    }

    private boolean containId(int id) {
        boolean result = false;
        for (Tab tab : group.getTabs()) {
            if (tab.getId() == id) {
                result = true;
            }
        }
        return result;
    }

    private List<AccessItemMenu> generateMenu(List<Tab> tabs) {
        ArrayList<AccessItemMenu> menu = new ArrayList<AccessItemMenu>();
        for (Tab tab : tabs) {
            if (tab.getType().equals("tab")) {
                List<AccessTab> childrens = new ArrayList<AccessTab>();
                for (Tab tab_c : tabs) {
                    if ((tab_c.getType().equals("item")) && (tab_c.getMenu_group() == tab.getMenu_group())) {
                        childrens.add(new AccessTab(this.containId(tab_c.getId()),tab_c));
                    }
                }
                menu.add(new AccessItemMenu(new AccessTab(this.containId(tab.getId()),tab),childrens,tab.getGroup_position()));
            }
        }
        return this.sortMenu(menu);
    }

    public Group getGroup() {
        return group;
    }

    public List<AccessItemMenu> getItemMenu() {
        return itemMenu;
    }
}
