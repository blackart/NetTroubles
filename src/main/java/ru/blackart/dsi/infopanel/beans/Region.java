package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "region")
public class Region implements Persistent {
    private int id;
    private String name;
    private String prefix;

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

    @Column(name = "prefix")
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Region() {

    }

    public Region(String name, String prefix) {
        this.setName(name);
        this.setPrefix(prefix);
    }

    public Region(int id, String name, String prefix) {
        this.setId(id);
        this.setName(name);
        this.setPrefix(prefix);
    }
}
