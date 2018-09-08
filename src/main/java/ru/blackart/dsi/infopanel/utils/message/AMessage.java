package ru.blackart.dsi.infopanel.utils.message;

import com.google.gson.Gson;

public abstract class AMessage implements Message {
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
