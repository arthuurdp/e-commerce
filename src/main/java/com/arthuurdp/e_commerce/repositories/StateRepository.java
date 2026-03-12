package com.arthuurdp.e_commerce.repositories;

import com.arthuurdp.e_commerce.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    Optional<State> findByUf(String uf);
}
