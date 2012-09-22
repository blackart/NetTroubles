package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;

@Entity
@Table(name = "users_settings")
public class UserSettings implements Persistent {
    private int id;
    private String timeoutReload;
    private Boolean openControlPanel;
    private Boolean currentTroublesPageReload;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "timeout_reload")
    public String getTimeoutReload() {
        return timeoutReload;
    }

    public void setTimeoutReload(String timeoutReload) {
        this.timeoutReload = timeoutReload;
    }

    @Column(name = "open_control_panel")
    public Boolean getOpenControlPanel() {
        return openControlPanel;
    }

    public void setOpenControlPanel(Boolean openControlPanel) {
        this.openControlPanel = openControlPanel;
    }

    @Column(name = "curr_troubles_page_reload")
    public Boolean getCurrentTroublesPageReload() {
        return currentTroublesPageReload;
    }

    public void setCurrentTroublesPageReload(Boolean currentTroublesPageReload) {
        this.currentTroublesPageReload = currentTroublesPageReload;
    }
}
