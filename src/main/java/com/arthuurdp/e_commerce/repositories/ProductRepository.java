package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @EntityGraph(attributePaths = {"images", "categories"})
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"images", "categories"})
    @NonNull
    Optional<Product> findById(@NonNull Long id);
}
