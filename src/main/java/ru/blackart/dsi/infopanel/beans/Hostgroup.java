package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;

@Entity
@Table(name = "hostgroup")
public class Hostgroup implements Persistent {
    private int id;
    private String name;
    private Integer num;

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

    @Column(name = "num")
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
