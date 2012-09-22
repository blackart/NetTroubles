package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "devcapsule")
public class Devcapsule implements Persistent {
    private int id;
    private Device device;
    private String timedown;
    private String timeup;
    private List<Trouble> troubles;
    private Boolean complete;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "_device")
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Column(name = "timedown")
    public String getTimedown() {
        return timedown;
    }

    public void setTimedown(String timedown) {
        this.timedown = timedown;
    }

    @Column(name = "timeup")    
    public String getTimeup() {
        return timeup;
    }

    public void setTimeup(String timeup) {
        this.timeup = timeup;
    }

    @ManyToMany(
            mappedBy = "devcapsules",
            cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            targetEntity = Trouble.class
    )
    public List<Trouble> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<Trouble> troubles) {
        this.troubles = troubles;
    }

    @Column(name = "complete")
    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }
}
