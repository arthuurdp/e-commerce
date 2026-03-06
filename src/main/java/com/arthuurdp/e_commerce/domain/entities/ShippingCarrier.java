package com.arthuurdp.e_commerce.domain.entities;

import com.arthuurdp.e_commerce.domain.enums.ShippingCarrierStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "shipping_carriers")
public class ShippingCarrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @Column(name = "leg_order", nullable = false)
    private Integer legOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShippingCarrierStatus status;

    @PrePersist
    public void PrePersist() {
        this.status = ShippingCarrierStatus.PENDING;
    }

    public ShippingCarrier() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Integer getLegOrder() {
        return legOrder;
    }

    public void setLegOrder(Integer legOrder) {
        this.legOrder = legOrder;
    }

    public ShippingCarrierStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingCarrierStatus status) {
        this.status = status;
    }
}
