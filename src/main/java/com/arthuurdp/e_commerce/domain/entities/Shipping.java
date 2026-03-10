package com.arthuurdp.e_commerce.domain.entities;

import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
    private ShippingStatus status;

    @Column(name = "me_order_id")
    private String meOrderId;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "tracking_code")
    private String trackingCode;

    @Column(name = "tracking_url")
    private String trackingUrl;

    @Column(name = "label_url")
    private String labelUrl;

    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ShippingStatus.PENDING;
        }
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

    public String getMeOrderId() { return meOrderId; }

    public void setMeOrderId(String meOrderId) { this.meOrderId = meOrderId; }

    public String getCarrier() { return carrier; }

    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingCode() { return trackingCode; }

    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public String getTrackingUrl() { return trackingUrl; }

    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public String getLabelUrl() { return labelUrl; }

    public void setLabelUrl(String labelUrl) { this.labelUrl = labelUrl; }

    public BigDecimal getShippingCost() { return shippingCost; }

    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }

    public LocalDateTime getPostedAt() { return postedAt; }

    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}