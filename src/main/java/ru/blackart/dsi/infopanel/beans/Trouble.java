package ru.blackart.dsi.infopanel.beans;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "trouble")
public class Trouble implements Persistent {
    private int id;
    private String title;
    private String actualProblem;
    private String timeout;
    private String date_in;
    private String date_out;
    private Users author;
    private List<Devcapsule> devcapsules;
    private List<Service> services;
    private Boolean close;
    private Boolean crm;
    private List<Comment> comments;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "actual_problem")
    public String getActualProblem() {
        return actualProblem;
    }

    public void setActualProblem(String actualProblem) {
        this.actualProblem = actualProblem;
    }

    @Column(name = "timeout")
    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @Column(name = "date_in")
    public String getDate_in() {
        return date_in;
    }


    public void setDate_in(String date_in) {
        this.date_in = date_in;
    }

    @Column(name = "date_out")
    public String getDate_out() {
        return date_out;
    }

    public void setDate_out(String date_out) {
        this.date_out = date_out;
    }

    @ManyToOne
    @JoinColumn(name = "_author")
    public Users getAuthor() {
        return author;
    }

    public void setAuthor(Users author) {
        this.author = author;
    }

    @OneToMany(
            targetEntity = Devcapsule.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @JoinTable(
            name = "dev_trouble",
            joinColumns = @JoinColumn(name = "_trouble"),
            inverseJoinColumns = @JoinColumn(name = "_devcapsule")
    )
    public List<Devcapsule> getDevcapsules() {
        return devcapsules;
    }

    public void setDevcapsules(List<Devcapsule> devcapsules) {
        this.devcapsules = devcapsules;
    }

    @ManyToMany(
            targetEntity = Service.class,
            cascade = {CascadeType.MERGE,CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "trouble_service",
            joinColumns = @JoinColumn(name = "_trouble"),
            inverseJoinColumns = @JoinColumn(name = "_service")
    )
    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Column(name = "close")
    public Boolean getClose() {
        return close;
    }

    public void setClose(Boolean close) {
        this.close = close;
    }

    @Column(name = "crm")
    public Boolean getCrm() {
        return crm;
    }

    public void setCrm(Boolean crm) {
        this.crm = crm;
    }

    @OneToMany(
            targetEntity = Comment.class,
            cascade = {CascadeType.MERGE,CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "trouble_comment",
            joinColumns = @JoinColumn(name = "_trouble"),
            inverseJoinColumns = @JoinColumn(name = "_comment")
    )
    public List<Comment> getComments() {
        return this.sortCommentByDate(comments);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> sortCommentByDate(List<Comment> comments) {
        for (int i=0; i < comments.size(); i++) {
            for (int j=0; j < i; j++) {
                Long comment_time_i = Long.valueOf(comments.get(i).getTime());
                Long comment_time_j = Long.valueOf(comments.get(j).getTime());

                if (comment_time_i < comment_time_j) {  /*> - по убыванию, < - по возрастанию*/
                    Comment comment = comments.get(i);
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
