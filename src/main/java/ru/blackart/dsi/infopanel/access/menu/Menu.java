package ru.blackart.dsi.infopanel.access.menu;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Menu {
    private ArrayList<MenuItem> items;

    public ArrayList<MenuItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<MenuItem> items) {
        this.items = items;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
