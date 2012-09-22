package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "device")
public class Device implements Persistent {
    private int id;
    private String name;
    private String description;
    private Hostgroup hostgroup;
    private Hoststatus hoststatus;
    private Region region;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    

    @ManyToOne
    @JoinColumn(name = "_group")
    public Hostgroup getHostgroup() {
        return hostgroup;
    }

    public void setHostgroup(Hostgroup hostgroup) {
        this.hostgroup = hostgroup;
    }

    @ManyToOne
    @JoinColumn(name = "_status")
    public Hoststatus getHoststatus() {
        return hoststatus;
    }

    public void setHoststatus(Hoststatus hoststatus) {
        this.hoststatus = hoststatus;
    }

    @ManyToOne
    @JoinColumn(name = "_region")
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
