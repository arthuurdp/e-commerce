package com.arthuurdp.e_commerce.modules.address;

import com.arthuurdp.e_commerce.modules.address.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Page<City> findByStateIdAndNameContainingIgnoreCase(Pageable pageable, Long stateId, String name);
    Optional<City> findByNameIgnoreCaseAndStateId(String name, Long stateId);}
