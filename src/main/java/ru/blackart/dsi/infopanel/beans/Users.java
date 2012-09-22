package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users implements Persistent {
    private int id;
    private String login;
    private String passwd;
    private Boolean block;
    private String fio;
    private Group group_id;
    private UserSettings settings_id;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "passwd")
    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Column(name = "block")
    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }

    @Column(name = "fio")
    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    @ManyToOne
    @JoinColumn(name = "_group")
    public Group getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Group group_id) {
        this.group_id = group_id;
    }

    @OneToOne
    @JoinColumn(name = "_settings")
    public UserSettings getSettings_id() {
        return settings_id;
    }

    public void setSettings_id(UserSettings settings_id) {
        this.settings_id = settings_id;
    }

    public Users() {
    }

    public Users(String login, String passwd, Boolean block, Group group_id, String fio) {
        this.login = login;
        this.passwd = passwd;
        this.block = block;
        this.group_id = group_id;
        this.fio = fio;
    }
}
