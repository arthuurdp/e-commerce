package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {
}
