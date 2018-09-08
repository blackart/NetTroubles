package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment implements Persistent{
    private int id;
    private String text;
    private String time;
    private User author;
    private Boolean crm;


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "time")
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @ManyToOne
    @JoinColumn(name = "_author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "crm")
    public Boolean getCrm() {
        return crm;
    }

    public void setCrm(Boolean crm) {
        this.crm = crm;
    }

    public Comment(String text, String time, User author, Boolean crm) {
        this.text = text;
        this.time = time;
        this.author = author;
        this.crm = crm;
    }

    public Comment() {
    }
}
