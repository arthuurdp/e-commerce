package com.arthuurdp.e_commerce.modules.product.entity;

import com.arthuurdp.e_commerce.modules.category.entity.Category;
import com.arthuurdp.e_commerce.modules.order.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "width", nullable = false)
    private Integer width;
    
    @Column(name = "height", nullable = false)
    private Integer height;
    
    @Column(name = "length", nullable = false)
    private Integer length;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = Instant.now();
    }

    public Product(String name, String description, BigDecimal price, Integer stock, Double weight, Integer width, Integer height, Integer length) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.weight = weight != null ? weight : 0.5;
        this.width = width != null ? width : 15;
        this.height = height != null ? height : 10;
        this.length = length != null ? length : 20;
    }

    public Double getWeight() { return weight != null ? weight : 0.5; }
    public Integer getWidth() { return width != null ? width : 15; }
    public Integer getHeight() { return height != null ? height : 10; }
    public Integer getLength() { return length != null ? length : 20; }

    public String getMainImageUrl() {
        return images.stream().filter(ProductImage::isMainImage).map(ProductImage::getUrl).findFirst().orElse(null);
    }

    public void setMainImage(ProductImage img) {
        if (img == null) return;

        images.forEach(i -> i.setMainImage(false));
        img.setMainImage(true);
    }

    public void addImage(ProductImage img) {
        if (!images.contains(img)) {
            images.add(img);
            img.setProduct(this);
        }
    }

    public void addImages(List<ProductImage> imgs) {
        imgs.forEach(this::addImage);
    }

    public void removeAllImages() {
        images.forEach(img -> img.setProduct(null));
        images.clear();
    }

    public void addCategory(Category category) {
        if (categories.add(category)) {
            category.getProducts().add(this);
        }
    }

    public void addCategories(List<Category> categories) {
        categories.forEach(this::addCategory);
    }

    public void removeAllCategories() {
        categories.forEach(c -> c.getProducts().remove(this));
        categories.clear();
    }
}
