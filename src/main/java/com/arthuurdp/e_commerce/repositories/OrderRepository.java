package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Pageable pageable, Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
}