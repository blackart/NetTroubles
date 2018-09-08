package ru.blackart.dsi.infopanel.view;

public class CommentView {
    private boolean crm;
    private String text;
    private String time;
    private UserView author;

    public boolean isCrm() {
        return crm;
    }

    public void setCrm(boolean crm) {
        this.crm = crm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UserView getAuthor() {
        return author;
    }

    public void setAuthor(UserView author) {
        this.author = author;
    }
}
