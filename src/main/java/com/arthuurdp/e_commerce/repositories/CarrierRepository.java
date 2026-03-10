package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.Carrier;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Page<Carrier> findByRegionAndStatus(Region region, CarrierStatus status, Pageable pageable);
    Page<Carrier> findByRegion(Region region, Pageable pageable);
    boolean existsByEmail(String email);

}
