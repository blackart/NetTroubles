package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;

@Entity
@Table(name = "type_device_filters")
public class TypeDeviceFilter implements Persistent {
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
}
