package com.arthuurdp.e_commerce.entities;

import jakarta.persistence.*;

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

    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private Set<City> cities;

    public State() {}

    public State(String name, String uf) {
        this.name = name;
        this.uf = uf;
    }

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

    public Set<City> getCities() {
        return cities;
    }
}
