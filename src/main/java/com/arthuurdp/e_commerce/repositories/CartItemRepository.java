package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci.product FROM CartItem ci WHERE ci.cart.id = :cartId")
    Page<CartItem> findByCartId(Long id, Pageable pageable);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
