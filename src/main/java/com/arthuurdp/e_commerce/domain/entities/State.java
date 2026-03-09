package com.arthuurdp.e_commerce.domain.entities;

import com.arthuurdp.e_commerce.domain.enums.Region;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "states")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    private Region region;

    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private Set<City> cities;

    @ManyToMany
    @JoinTable(
            name = "state_neighbors",
            joinColumns = @JoinColumn(name = "state_id"),
            inverseJoinColumns = @JoinColumn(name = "neighbor_id")
    )
    private List<State> neighbors = new ArrayList<>();

    public State() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Set<City> getCities() {
        return cities;
    }

    public List<State> getNeighbors() {
        return neighbors;
    }
}
