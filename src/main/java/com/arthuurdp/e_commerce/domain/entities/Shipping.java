package com.arthuurdp.e_commerce.domain.entities;

import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shippings")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingStatus status;

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL)
    private List<ShippingCarrier> carriers = new ArrayList<>();

    @Column(name = "tracking_code")
    private String trackingCode;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = ShippingStatus.SHIPPED;
    }

    public Shipping() {}

    public Shipping(Order order) {
        this.order = order;
    }

    public Long getId() { return id; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    public ShippingStatus getStatus() { return status; }

    public void setStatus(ShippingStatus status) { this.status = status; }

    public List<ShippingCarrier> getCarriers() {
        return carriers;
    }

    public String getTrackingCode() { return trackingCode; }

    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public LocalDateTime getShippedAt() { return shippedAt; }

    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}