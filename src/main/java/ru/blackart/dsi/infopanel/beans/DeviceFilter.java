package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "device_filter")
public class DeviceFilter implements Persistent {
    private int id;
    private String name;
    private String value;
    private TypeDeviceFilter type;
    private boolean policy;
    private boolean enable;
    private List<TroubleList> troubleLists;

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

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @ManyToOne
    @JoinColumn(name = "_type")    
    public TypeDeviceFilter getType() {
        return type;
    }

    public void setType(TypeDeviceFilter type) {
        this.type = type;
    }

    @Column(name = "policy")
    public boolean isPolicy() {
        return policy;
    }

    public void setPolicy(boolean policy) {
        this.policy = policy;
    }

    @Column(name = "enable")
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @ManyToMany(
            mappedBy = "filters",
            targetEntity = TroubleList.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
    )
    /*@ManyToMany(
            targetEntity = TroubleList.class,
            cascade = CascadeType.PERSIST, fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "trouble_list_filters",
            joinColumns = @JoinColumn(name = "_device_filter"),
            inverseJoinColumns = @JoinColumn(name = "_trouble_list")
    )*/
    public List<TroubleList> getTroubleLists() {
        return troubleLists;
    }

    public void setTroubleLists(List<TroubleList> troubleLists) {
        this.troubleLists = troubleLists;
    }

    public DeviceFilter() {}

    public DeviceFilter(String name, String value, TypeDeviceFilter type, boolean policy) {
        this.setName(name);
        this.setValue(value);
        this.setType(type);
        this.setPolicy(policy);
    }    
}
