package ru.blackart.dsi.infopanel.utils.message;

import com.google.gson.Gson;

public class SimpleMessage implements Message {
    private String message;

    public SimpleMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
