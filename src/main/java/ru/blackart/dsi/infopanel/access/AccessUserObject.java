package ru.blackart.dsi.infopanel.access;

import com.google.gson.Gson;
import ru.blackart.dsi.infopanel.access.menu.Menu;
import ru.blackart.dsi.infopanel.beans.Group;
import ru.blackart.dsi.infopanel.beans.User;

public class AccessUserObject {
    private User user;
    private Menu menu;

    public AccessUserObject(User user) {
        this.user = user;
        Gson gson = new Gson();
        Group group = user.getGroup_id();
        this.menu = gson.fromJson(group.getMenuConfig(), Menu.class);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}