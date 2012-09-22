package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="menu_tabs")
public class Tab implements Persistent {
    private int id;
    private String file_name;
    private String caption;
    private int menu_group;
    private List<Group> groups;
    private String type;
    private int group_position;

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name="file_name")
    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    @Column(name="caption")
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Column(name="menu_group")
    public int getMenu_group() {
        return menu_group;
    }

    public void setMenu_group(int menu_group) {
        this.menu_group = menu_group;
    }

    @Column(name="type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToMany(
            targetEntity = Group.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            mappedBy = "tabs"
    )
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Column(name="group_position")
    public int getGroup_position() {
        return group_position;
    }

    public void setGroup_position(int group_position) {
        this.group_position = group_position;
    }

    public Tab() {}
}
