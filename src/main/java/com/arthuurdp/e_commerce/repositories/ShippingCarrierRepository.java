package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.ShippingCarrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingCarrierRepository extends JpaRepository<ShippingCarrier, Long> {
    Optional<ShippingCarrier> findByShippingIdAndLegOrder(Long shippingId, Integer legOrder);
}
