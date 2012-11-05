package ru.blackart.dsi.infopanel.utils.message;

public class SimpleMessage extends AMessage {
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
}