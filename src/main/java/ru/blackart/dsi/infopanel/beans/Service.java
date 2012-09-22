package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "service")
public class Service implements Persistent {
    private int id;
    private String name;

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

/*    @ManyToMany(
            mappedBy = "services",
            cascade = {CascadeType.PERSIST,CascadeType.MERGE},
            targetEntity = Trouble.class
    )
    public List<Trouble> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<Trouble> troubles) {
        this.troubles = troubles;
    }*/
}