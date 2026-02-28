package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);                       // ← era Optional, causava bug no OrderService
    Optional<Order> findByIdAndUserId(Long id, Long userId);    // ← para buscar pedido por id + segurança
}