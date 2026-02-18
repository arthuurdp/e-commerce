package com.arthuurdp.e_commerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductImage> images = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Product() {}

    public Product(String name, String description, Double price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public Optional<ProductImage> getMainImage() {
        return images.stream().filter(ProductImage::isMainImage).findFirst();
    }

    public void setMainImage(ProductImage img) {
        if (!images.contains(img)) {
            throw new IllegalArgumentException("Image does not belong to this product");
        }

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

    public void removeImage(ProductImage img) {
        if (images.remove(img)) {
            img.setProduct(null);
        }
    }

    public void removeAllImages() {
        images.forEach(img -> img.setProduct(null));
        images.clear();
    }

    public void addCategory(Category category) {
        if (categories.add(category)) {
            category.addProduct(this);
        }
    }

    public void addCategories(List<Category> categories) {
        categories.forEach(this::addCategory);
    }

    public void removeCategory(Category category) {
        if (categories.remove(category)) {
            category.getProducts().remove(this);
        }
    }

    public void removeAllCategories() {
        categories.forEach(c -> c.getProducts().remove(this));
        categories.clear();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
