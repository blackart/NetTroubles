//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.blackart.dsi.infopanel.beans;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(
        name = "trouble"
)
public class Trouble implements Persistent {
    private int id;
    private String title;
    private String actualProblem;
    private String timeout;
    private String date_in;
    private String date_out;
    private User author;
    private List<Devcapsule> devcapsules;
    private List<Service> services;
    private Boolean close;
    private Boolean crm;
    private List<Comment> comments;

    public Trouble() {
    }

    @Id
    @Column(
            name = "id"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(
            name = "title"
    )
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(
            name = "actual_problem"
    )
    public String getActualProblem() {
        return this.actualProblem;
    }

    public void setActualProblem(String actualProblem) {
        this.actualProblem = actualProblem;
    }

    @Column(
            name = "timeout"
    )
    public String getTimeout() {
        return this.timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @Column(
            name = "date_in"
    )
    public String getDate_in() {
        return this.date_in;
    }

    public void setDate_in(String date_in) {
        this.date_in = date_in;
    }

    @Column(
            name = "date_out"
    )
    public String getDate_out() {
        return this.date_out;
    }

    public void setDate_out(String date_out) {
        this.date_out = date_out;
    }

    @ManyToOne
    @JoinColumn(
            name = "_author"
    )
    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @OneToMany(
            targetEntity = Devcapsule.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "dev_trouble",
            joinColumns = {@JoinColumn(
                    name = "_trouble"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "_devcapsule"
            )}
    )
    public List<Devcapsule> getDevcapsules() {
        return this.devcapsules;
    }

    public void setDevcapsules(List<Devcapsule> devcapsules) {
        this.devcapsules = devcapsules;
    }

    @OneToMany(
            targetEntity = Service.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "trouble_service",
            joinColumns = {@JoinColumn(
                    name = "_trouble"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "_service"
            )}
    )
    public List<Service> getServices() {
        return this.services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Column(
            name = "close"
    )
    public Boolean getClose() {
        return this.close;
    }

    public void setClose(Boolean close) {
        this.close = close;
    }

    @Column(
            name = "crm"
    )
    public Boolean getCrm() {
        return this.crm;
    }

    public void setCrm(Boolean crm) {
        this.crm = crm;
    }

    @OneToMany(
            targetEntity = Comment.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "trouble_comment",
            joinColumns = {@JoinColumn(
                    name = "_trouble"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "_comment"
            )}
    )
    public List<Comment> getComments() {
        return this.sortCommentByDate(this.comments);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> sortCommentByDate(List<Comment> comments) {
        for(int i = 0; i < comments.size(); ++i) {
            for(int j = 0; j < i; ++j) {
                Long comment_time_i = Long.valueOf(((Comment)comments.get(i)).getTime());
                Long comment_time_j = Long.valueOf(((Comment)comments.get(j)).getTime());
                if (comment_time_i.longValue() < comment_time_j.longValue()) {
                    Comment comment = (Comment)comments.get(i);
                    comments.set(i, comments.get(j));
                    comments.set(j, comment);
                }
            }
        }

        return comments;
    }

    public void save() {
    }
}
