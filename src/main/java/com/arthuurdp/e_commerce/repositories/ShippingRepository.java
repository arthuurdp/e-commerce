package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    boolean existsByOrderId(Long id);
    Optional<Shipping> findByOrderIdAndOrderUserId(Long orderId, Long id);
}
