package com.arthuurdp.e_commerce.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carriers")
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "carrier_state", joinColumns = @JoinColumn(name = "carrier_id"), inverseJoinColumns = @JoinColumn(name = "state_id"))
    private List<State> states = new ArrayList<>();

    public Carrier() {}

    public Carrier (String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
