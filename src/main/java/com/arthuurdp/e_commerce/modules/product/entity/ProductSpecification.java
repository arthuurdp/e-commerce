package com.arthuurdp.e_commerce.modules.product.entity;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecification {
    private ProductSpecification() {
    }

    public static Specification<Product> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return (root, query, cb) -> null;
        }

        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> inCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return (root, query, cb) -> null;
        }

        return (root, query, cb) -> {
            Join<Object, Object> categories = root.join("categories");
            return categories.get("id").in(categoryIds);
        };
    }
}
