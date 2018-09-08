package ru.blackart.dsi.infopanel.utils.message;

public class CompleteStatusMessage extends SimpleMessage {
    private boolean status;

    public CompleteStatusMessage() {}

    public void setStatus(boolean status) {
        this.status = status;
    }

    public CompleteStatusMessage(String message, Boolean status) {
        this.setStatus(status);
        this.setMessage(message);
    }
}
