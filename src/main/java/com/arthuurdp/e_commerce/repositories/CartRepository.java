package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
