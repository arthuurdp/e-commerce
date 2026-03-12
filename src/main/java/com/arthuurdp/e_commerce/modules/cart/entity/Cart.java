package com.arthuurdp.e_commerce.modules.cart.entity;

import com.arthuurdp.e_commerce.shared.exceptions.ProductOutOfStockException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "cart")
    private User user;

    @JsonIgnore
    @OneToMany(
            mappedBy = "cart",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> items = new ArrayList<>();

    public CartItem addProduct(Product product) {
        Optional<CartItem> existingItem = items.stream()
                .filter(i -> i.getProduct().equals(product))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            if (item.getQuantity() >= product.getStock()) {
                throw new ProductOutOfStockException("Product is out of stock");
            }

            item.incrementQuantity();
            return item;
        }

        if (product.getStock() < 1) {
            throw new ProductOutOfStockException("Product is out of stock");
        }

        CartItem newItem = new CartItem(this, product, 1);
        items.add(newItem);
        return newItem;
    }

    public Optional<CartItem> removeProduct(Product product) {
        CartItem item = items.stream()
                .filter(i -> i.getProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (item.getQuantity() <= 1) {
            items.remove(item);
            return Optional.empty();
        }

        item.decrementQuantity();
        return Optional.of(item);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int quantity() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal total() {
        return items.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
