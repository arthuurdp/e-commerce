package com.arthuurdp.e_commerce.entities;

import com.arthuurdp.e_commerce.entities.enums.StateUF;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 2)
    private StateUF state;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private Set<Address> addresses = new HashSet<>();

    public City() {
    }

    public City(String name, StateUF state) {
        this.name = name;
        this.state = state;
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

    public StateUF getState() {
        return state;
    }

    public void setState(StateUF state) {
        this.state = state;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(id, city.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
