package com.arthuurdp.e_commerce.modules.address;

import com.arthuurdp.e_commerce.modules.address.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    Optional<State> findByUf(String uf);
}
