package com.arthuurdp.e_commerce.modules.shipping;

import com.arthuurdp.e_commerce.modules.shipping.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    Optional<Shipping> findByOrderId(Long orderId);
    Optional<Shipping> findByOrderIdAndOrderUserId(Long orderId, Long userId);
    Optional<Shipping> findByMeOrderId(String meOrderId);
}
