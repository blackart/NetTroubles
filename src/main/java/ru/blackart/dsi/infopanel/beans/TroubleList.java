package ru.blackart.dsi.infopanel.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "troublelist")
public class TroubleList implements Persistent {
    private int id;
    private String name;
    private List<DeviceFilter> filters;
    private List<Trouble> troubles;

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

    @ManyToMany(
            targetEntity = DeviceFilter.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "trouble_list_filters",
            joinColumns = @JoinColumn(name = "_trouble_list"),
            inverseJoinColumns = @JoinColumn(name = "_device_filter")
    )
    public List<DeviceFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<DeviceFilter> filters) {
        this.filters = filters;
    }

    @OneToMany(
            targetEntity = Trouble.class,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY

    )
    @JoinTable(
            name = "tl_t",
            joinColumns = @JoinColumn(name = "_troublelist"),
            inverseJoinColumns = @JoinColumn(name = "_trouble")
    )
    public List<Trouble> getTroubles() {
        return troubles;
    }

    public void setTroubles(List<Trouble> troubles) {
        this.troubles = troubles;
    }
}
