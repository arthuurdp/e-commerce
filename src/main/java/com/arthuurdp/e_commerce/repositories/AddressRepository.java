package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @EntityGraph(attributePaths = {"user"})
    Page<Address> findByUserId(Pageable pageable, Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<Address> findByIdAndUserId(Long id, Long userId);

}
