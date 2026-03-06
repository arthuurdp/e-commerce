package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.Carrier;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    @EntityGraph(attributePaths = {"shipping"})
    Optional<Carrier> findByShippingId(Long shippingId);
}
