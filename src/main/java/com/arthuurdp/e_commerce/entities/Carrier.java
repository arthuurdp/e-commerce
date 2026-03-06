package com.arthuurdp.e_commerce.entities;

import com.arthuurdp.e_commerce.entities.enums.ActiveCarrierStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActiveCarrierStatus status;

    @ManyToMany(mappedBy = "carriers")
    List<Shipping> shippings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @PrePersist
    void PrePersist() {
        this.status = ActiveCarrierStatus.AVAILABLE;
    }

    public Carrier() {}

    public Carrier (String name, State state) {
        this.name = name;
        this.state = state;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ActiveCarrierStatus getStatus() {
        return status;
    }

    public void setStatus(ActiveCarrierStatus status) {
        this.status = status;
    }
}
