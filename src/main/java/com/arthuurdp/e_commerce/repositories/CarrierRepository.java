package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.Carrier;
import com.arthuurdp.e_commerce.entities.enums.CarrierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Page<Carrier> findByStateIdAndStatus(Long stateId, CarrierStatus status, Pageable pageable);
}
