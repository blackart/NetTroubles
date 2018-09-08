package ru.blackart.dsi.infopanel.view;

import com.google.gson.Gson;

public class View {
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
