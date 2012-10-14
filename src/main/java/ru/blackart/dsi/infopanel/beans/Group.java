package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group implements Persistent {
    private int id;
    private String name;
    private List<Tab> tabs;
    private String menuConfig;

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

    @Column(name = "menu_config")
    public String getMenuConfig() {
        return menuConfig;
    }

    public void setMenuConfig(String menuConfig) {
        this.menuConfig = menuConfig;
    }

    @ManyToMany(
            targetEntity = Tab.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    @JoinTable(
            name = "group_tabs",
            joinColumns = @JoinColumn(name = "_group"),
            inverseJoinColumns = @JoinColumn(name = "_menu_tabs")
    )
    public List<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }
    
    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Group() {

    }
}
