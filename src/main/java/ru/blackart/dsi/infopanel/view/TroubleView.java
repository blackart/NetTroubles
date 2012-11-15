package ru.blackart.dsi.infopanel.view;

import ru.blackart.dsi.infopanel.beans.Service;

import java.util.List;

public class TroubleView {
    private int id;
    private String title;
    private String actualProblem;
    private String timeout;
    private String date_in;
    private String date_out;
    private UserView author;
    private List<DevcapsuleView> devcapsules;
    private List<Service> services;
    private Boolean close;
    private Boolean crm;
    private List<CommentView> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActualProblem() {
        return actualProblem;
    }

    public void setActualProblem(String actualProblem) {
        this.actualProblem = actualProblem;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getDate_in() {
        return date_in;
    }

    public void setDate_in(String date_in) {
        this.date_in = date_in;
    }

    public String getDate_out() {
        return date_out;
    }

    public void setDate_out(String date_out) {
        this.date_out = date_out;
    }

    public UserView getAuthor() {
        return author;
    }

    public void setAuthor(UserView author) {
        this.author = author;
    }

    public List<DevcapsuleView> getDevcapsules() {
        return devcapsules;
    }

    public void setDevcapsules(List<DevcapsuleView> devcapsules) {
        this.devcapsules = devcapsules;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public Boolean getClose() {
        return close;
    }

    public void setClose(Boolean close) {
        this.close = close;
    }

    public Boolean getCrm() {
        return crm;
    }

    public void setCrm(Boolean crm) {
        this.crm = crm;
    }

    public List<CommentView> getComments() {
        return comments;
    }

    public void setComments(List<CommentView> comments) {
        this.comments = comments;
    }
}
