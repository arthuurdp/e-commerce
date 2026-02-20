package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"product"})
    Page<CartItem> findByCartId(Long cartId, Pageable pageable);
}
